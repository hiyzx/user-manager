package com.zero.mail.service;

import com.zero.util.JsonHelper;
import com.zero.vo.MailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Created by Administrator on 2017/5/15.
 */
@Service
public class ActiveMqService {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveMqService.class);
    @Resource
    private JmsTemplate jmsTemplate;

    public void sendToMQ(final String receiver, final String title, final String content) {
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MailVo mailVo = new MailVo();
                mailVo.setTo(receiver);
                mailVo.setTitle(title);
                mailVo.setContent(content);
                return session.createTextMessage(JsonHelper.toJSon(mailVo));
            }
        });
        LOG.info("send msg receiver={} title={} content={} to MQ", receiver, title, content);
    }
}
