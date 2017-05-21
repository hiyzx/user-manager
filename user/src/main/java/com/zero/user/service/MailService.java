package com.zero.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zero.user.util.HttpClient;
import com.zero.util.JsonHelper;
import com.zero.vo.BaseReturnVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/10
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);
    private static final String STATUS_SUCCESS_CODE = "000000";
    @Resource
    private HttpClient mailHttpClient;

    boolean sendMail(String receiver, String title, String content) throws IOException {
        long startTime = System.currentTimeMillis();
        Map<String, String> params = new HashMap<>();
        params.put("receiver", receiver);
        params.put("title", title);
        params.put("content", content);
        String response = mailHttpClient.post("/mail/sendMail.json", params);
        final TypeReference<BaseReturnVo> REFERENCE = new TypeReference<BaseReturnVo>() {
        };
        LOG.info("request api cost time={}", System.currentTimeMillis() - startTime);
        BaseReturnVo returnVo = JsonHelper.readValue(response, REFERENCE);
        if (STATUS_SUCCESS_CODE.equals(returnVo.getResCode())) {
            LOG.info("send email to receiver={} success", receiver);
            return true;
        } else {
            LOG.info("send email to receiver={} fail", receiver);
            return false;
        }
    }
}
