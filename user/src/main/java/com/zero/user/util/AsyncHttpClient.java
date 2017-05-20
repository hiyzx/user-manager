package com.zero.user.util;

import com.zero.util.JsonUtil;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: 异步请求
 * @author: yezhaoxing
 * @date: 2017/5/19
 */
public class AsyncHttpClient {
    private static final String APPLICATION_STRING = "application/json;charset=UTF-8";
    private static final String UTF_8 = "UTF-8";
    private static final Logger LOG = LoggerFactory.getLogger(AsyncHttpClient.class);

    public static void postTraceData(String url, Map<String, String> params) {
        CloseableHttpAsyncClient client = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000)
                    .build();
            client = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig).build();
            client.start();
            final HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(JsonUtil.toJSon(params), UTF_8));
            request.setHeader("Content-type", APPLICATION_STRING);
            // 请求接口===不需要回调,否则不能异步
            client.execute(request, null);
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

}
