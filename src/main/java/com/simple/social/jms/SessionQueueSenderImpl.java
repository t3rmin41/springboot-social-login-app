package com.simple.social.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import com.simple.social.config.ActiveMqConfig;

@Component
public class SessionQueueSenderImpl implements SessionQueueSender {

  private static final Logger logger = LoggerFactory.getLogger(SessionQueueSenderImpl.class);

  @Autowired
  private JmsTemplate jmsTemplate;

  @Override
  public void sendMessageToQueue(String messageId) {
    jmsTemplate.send(ActiveMqConfig.QUEUE, new MessageCreator(){
      public Message createMessage(Session session) throws JMSException {
        TextMessage textMessage = session.createTextMessage(messageId);
        textMessage.setJMSCorrelationID(messageId);
        return textMessage;
      }
    });
    logger.info("Sent message to queue " + ActiveMqConfig.QUEUE + " with ID = " + messageId);
  }

}
