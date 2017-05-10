/*
* Copyright (c) 2016 bond. All Rights Reserved.
*/
package com.zero.util;

import javax.mail.internet.MimeMessage;

/**
 * 邮件工具类.
 */
public class MailUtil {

    @javax.annotation.Resource
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;

    @javax.annotation.Resource
    private org.springframework.mail.SimpleMailMessage simpleMailMessage;

    @javax.annotation.Resource
    private org.springframework.core.task.TaskExecutor taskExecutor;

    private class SendMailThread implements Runnable {
        private String to;
        private String subject;
        private String content;

        private SendMailThread(String to, String subject, String content) {
            super();
            this.to = to;
            this.subject = subject;
            this.content = content;
        }

        public void run() {
            sendMail(to, subject, content);
        }
    }

    /**
     * 发送邮件-使用线程池
     */
    public void sendMailThread(String to, String subject, String content) {
        this.taskExecutor.execute(new SendMailThread(to, subject, content));
    }

    /**
     * 发送邮件
     */
    private void sendMail(String receiver, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper messageHelper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(simpleMailMessage.getFrom());
            messageHelper.setSubject(subject);
            messageHelper.setTo(receiver);
            messageHelper.setText(content, true);//支持html
            javaMailSender.send(message);
        } catch (javax.mail.MessagingException e) {
            e.printStackTrace();
        }
    }
}
