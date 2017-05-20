package com.zero.user.util;

import com.zero.util.JsonUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/19
 */
public class AsyncHttpClient {
    private static final String URL = "http://localhost:8081/mail/requestData.json";
    private static final String APPLICATION_STRING = "application/json;charset=UTF-8";
    private static final String UTF_8 = "UTF-8";
    private static final Logger LOG = LoggerFactory.getLogger(AsyncHttpClient.class);

    public static void postTraceData(Map<String, String> params) {
        CloseableHttpAsyncClient client = null;
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000)
                    .build();
            client = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build();
            client.start();
            final HttpPost request = new HttpPost(URL);
            request.setEntity(new StringEntity(JsonUtil.toJSon(params), UTF_8));
            request.setHeader("Content-type", APPLICATION_STRING);
            // 请求接口并返回
            client.execute(request, new FutureCallback<HttpResponse>() {
                @Override
                public void cancelled() {
                    latch.countDown();
                }

                @Override
                public void completed(HttpResponse response) {
                    latch.countDown();
                }

                @Override
                public void failed(Exception ex) {
                    latch.countDown();
                    LOG.trace("postTraceData failed " + request.getRequestLine() + "->" + ex);
                }
            });
            latch.await();
            LOG.info("请求结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static int test(Map map) throws IOException {
        int status = 0;
        CloseableHttpAsyncClient client = null;
        try {
            client = HttpAsyncClients.createDefault();
            HttpPost httppost = new HttpPost("http://10.0.1.134:8081/mail/requestData.json");
            httppost.setEntity(new StringEntity(JsonUtil.toJSon(map), UTF_8));
            httppost.setHeader("Content-type", APPLICATION_STRING);
            client.start();
            Future<HttpResponse> future = client.execute(httppost, new FutureCallback<HttpResponse>() {
                @Override
                public void failed(Exception ex) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void completed(HttpResponse result) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void cancelled() {
                    // TODO Auto-generated method stub
                }
            });
            status = future.get().getStatusLine().getStatusCode();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return status;
    }

}
