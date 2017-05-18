/*
* Copyright (c) 2016 bond. All Rights Reserved.
*/
package com.zero.mail.util;

import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * 邮件工具类.
 */
public class MailUtil {

    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private SimpleMailMessage simpleMailMessage;
    @Resource
    private TaskExecutor taskExecutor;

    private class SendMailThread implements Runnable {
        private String receiver;
        private String subject;
        private String content;

        private SendMailThread(String receiver, String subject, String content) {
            super();
            this.receiver = receiver;
            this.subject = subject;
            this.content = content;
        }

        public void run() {
            sendMail(receiver, subject, content);
        }
    }

    /**
     * 发送邮件-使用线程池
     */
    public void sendMailThread(String receiver, String subject, String content) {
        this.taskExecutor.execute(new SendMailThread(receiver, subject, content));
    }

    /**
     * 发送邮件
     */
    private void sendMail(String receiver, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(simpleMailMessage.getFrom());
            messageHelper.setSubject(subject);
            messageHelper.setTo(receiver);
            messageHelper.setText(content, true);// 支持html
            javaMailSender.send(message);
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }
}
