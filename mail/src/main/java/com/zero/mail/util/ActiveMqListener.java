package com.zero.mail.util;

import com.zero.util.JsonHelper;
import com.zero.vo.MailVo;
import org.apache.activemq.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by Administrator on 2017/5/15.
 */
public class ActiveMqListener implements SessionAwareMessageListener<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveMqListener.class);
    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private MailUtil mailUtil;

    public synchronized void onMessage(Message message, Session session) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        final String msg = textMessage.getText();
        System.out.println("收到消息：" + msg);
        // 转换成相应的对象
        MailVo mail = JsonHelper.readValue(msg, MailVo.class);
        if (mail == null) {
            return;
        }
        try {
            // 执行发送业务
            mailUtil.sendMailThread(mail.getTo(), mail.getTitle(), mail.getContent());
        } catch (Exception e) {
            // 发生异常，重新放回队列
            jmsTemplate.send(new MessageCreator() {
                @Override
                public javax.jms.Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(JsonHelper.toJSon(msg));
                }
            });
            LOG.error(e.getMessage());
        }
    }
}
