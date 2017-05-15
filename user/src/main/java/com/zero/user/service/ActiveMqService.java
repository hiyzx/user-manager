package com.zero.user.service;

import com.zero.util.JsonUtil;
import com.zero.vo.MailVo;
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

    @Resource
    private JmsTemplate jmsTemplate;

    public void sendMessage(final MailVo mailVo) {
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(JsonUtil.toJSon(mailVo));
            }
        });
    }

}
