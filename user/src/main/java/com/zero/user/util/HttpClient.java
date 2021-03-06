package com.zero.user.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.zero.vo.HealthCheckVo;

/**
 * @see https://yq.aliyun.com/articles/294
 */
public class HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private static final String CONTENT_TYPE = "content-type";
    private static final String APPLICATION_JSON = "application/json";
    private static final int TIME_OUT = 30 * 1000;
    private static final String UTF_8 = "UTF-8";
    private static final String AUTHORIZATION_KEY = "Authorization";
    private final String authorizationValue;
    private final RequestConfig requestConfig;
    private final String scheme;
    private final String hostname;
    private final int port;
    private CloseableHttpClient httpClient = null;

    public HttpClient(String scheme, String hostname, int port, String authorizationValue) {
        this.scheme = scheme;
        this.hostname = hostname;
        this.port = port;
        this.authorizationValue = authorizationValue;
        // 配置请求的超时设置
        requestConfig = RequestConfig.custom()
                /**
                 * 从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.
                 * ConnectionPoolTimeoutException: Timeout waiting for
                 * connection from pool
                 */
                .setConnectionRequestTimeout(TIME_OUT)
                /**
                 * 连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                 */
                .setConnectTimeout(TIME_OUT * 2)// 2017-3-18:中心机房只有移动出口，延长建立连接的时间以降低失败的概率
                /**
                 * 服务器返回数据(response)的时间，超过该时间抛出read timeout
                 */
                .setSocketTimeout(TIME_OUT).build();
        httpClient = createHttpClient(50, 50, 1, hostname, port);
    }

    private static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute, String hostname,
            int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", plainsf).register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 2) {// 如果已经重试了2次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();
        return httpClient;
    }

    public String post(String path, Map<String, String> params, Map<String, String> headers) throws IOException {
        CloseableHttpResponse response = null;
        String rtn = null;
        try {
            URI uri = createURIBuilder(path);
            HttpPost httppost = new HttpPost(uri);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (Entry<String, String> paramEntry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry.getValue()));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, UTF_8));
            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> headerEntry : headers.entrySet()) {
                    httppost.addHeader(headerEntry.getKey(), headerEntry.getValue());
                }
            }
            if (StringUtils.hasText(authorizationValue)) {
                httppost.addHeader(AUTHORIZATION_KEY, authorizationValue);
            }
            httppost.setConfig(requestConfig);
            response = httpClient.execute(httppost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF_8);
            EntityUtils.consume(entity);
            rtn = result;
        } catch (IOException e) {
            throw e;
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return rtn;
    }

    public String post(String path, Map<String, String> params) throws IOException {
        Map<String, String> headers = Collections.emptyMap();
        return post(path, params, headers);
    }

    public String post(String path, String requestBody) throws IOException {
        LOG.debug("{}", requestBody);
        CloseableHttpResponse response = null;
        String rtn = null;
        try {
            URI uri = createURIBuilder(path);
            HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new StringEntity(requestBody, UTF_8));
            httppost.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            if (StringUtils.hasText(authorizationValue)) {
                httppost.addHeader(AUTHORIZATION_KEY, authorizationValue);
            }
            httppost.setConfig(requestConfig);
            response = httpClient.execute(httppost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF_8);
            EntityUtils.consume(entity);
            rtn = result;
        } catch (IOException e) {
            throw e;
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return rtn;
    }

    public String put(String path, String requestBody) throws IOException {
        LOG.debug("{}", requestBody);
        CloseableHttpResponse response = null;
        String rtn = null;
        try {
            URI uri = createURIBuilder(path);
            HttpPut httpput = new HttpPut(uri);
            httpput.setEntity(new StringEntity(requestBody, UTF_8));
            httpput.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            if (StringUtils.hasText(authorizationValue)) {
                httpput.addHeader(AUTHORIZATION_KEY, authorizationValue);
            }
            httpput.setConfig(requestConfig);
            response = httpClient.execute(httpput, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF_8);
            EntityUtils.consume(entity);
            rtn = result;
        } catch (IOException e) {
            throw e;
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return rtn;
    }

    public String get(String path, Map<String, String> params) {
        CloseableHttpResponse response = null;
        String rtn = null;
        try {
            URI uri = createURIBuilder(path, params);
            HttpGet httpget = new HttpGet(uri);
            if (StringUtils.hasText(authorizationValue)) {
                httpget.addHeader(AUTHORIZATION_KEY, authorizationValue);
            }
            httpget.setConfig(requestConfig);
            response = httpClient.execute(httpget, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, UTF_8);
            EntityUtils.consume(entity);
            rtn = result;
        } catch (IOException | URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return rtn;
    }

    public String get(String path) {
        Map<String, String> params = Collections.emptyMap();
        return get(path, params);
    }

    URI createURIBuilder(String path, Map<String, String> params) throws URISyntaxException {
        URIBuilder builder = new URIBuilder().setScheme(scheme).setHost(hostname).setPort(port).setPath(path);
        for (Entry<String, String> entry : params.entrySet()) {
            builder.addParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    URI createURIBuilder(String path) throws URISyntaxException {
        Map<String, String> params = Collections.emptyMap();
        return createURIBuilder(path, params);
    }

    public HealthCheckVo healthCheck() {
        HealthCheckVo healthCheckVo = new HealthCheckVo();
        healthCheckVo.setServiceName(String.format("%s:%s", hostname, port));
        try {
            healthCheckVo.setNormal(true);
            healthCheckVo.setCostTime(String.format("%sms", checkHttpConnection(hostname, port)));
        } catch (IOException e) {
            healthCheckVo.setNormal(false);
            LOG.error("hostname={} port={}", hostname, port, e);
        }
        return healthCheckVo;
    }

    private static long checkHttpConnection(final String hostname, final int port) throws IOException {
        long startTimeMillis = System.currentTimeMillis();
        Socket server = null;
        server = new Socket();
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        server.connect(address, 300000);// 5分钟
        server.close();
        return System.currentTimeMillis() - startTimeMillis;
    }
}
