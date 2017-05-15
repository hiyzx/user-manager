package com.zero.mail.service;

import com.zero.mail.util.MailUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @author: yezhaoxing
 * @date: 2017/5/10
 */
@Service
public class MailService {

    @Resource
    private MailUtil mailUtil;

    public void sendMail(String receiver, String title, String content) {
        mailUtil.sendMailThread(receiver, title, content);
    }
}
