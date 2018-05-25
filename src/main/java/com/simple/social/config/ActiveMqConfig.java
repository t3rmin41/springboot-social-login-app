package com.simple.social.config;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
//import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import com.simple.social.jms.SessionQueueSender;
import com.simple.social.jms.SessionQueueSenderImpl;

@Configuration
public class ActiveMqConfig {

  public static final String BROKER_URL = "tcp://localhost:61616";
  //public static final String BROKER_URL = "vm://embedded?broker.persistent=false,useShutdownHook=true";
  public static final String QUEUE = "session.queue";
  private static final String BROKER_USERNAME = "admin";
  private static final String BROKER_PASSWORD = "admin";
  
  @Bean
  public BrokerService brokerService() throws Exception {
    BrokerService broker = new BrokerService();
    broker.addConnector(BROKER_URL);
    broker.setPersistent(false);
    broker.setUseShutdownHook(true);
//    broker.setUseJmx(true);
//    ManagementContext managementContext = broker.getManagementContext();
//    managementContext.setCreateConnector(true);
//    managementContext.setConnectorPort(1099);
//    managementContext.setRmiServerPort(1098);
    broker.start();
    return broker;
  }
  
  @Bean(name = "jmsConnectionFactory")
  public ConnectionFactory jmsConnectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(BROKER_URL);
    connectionFactory.setUserName(BROKER_USERNAME);
    connectionFactory.setPassword(BROKER_PASSWORD);
    return connectionFactory;
  }

  @Bean(name = "jmsTemplate")
  public JmsTemplate jmsTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setDefaultDestination(new ActiveMQQueue(QUEUE));
    jmsTemplate.setConnectionFactory(jmsConnectionFactory());
    jmsTemplate.setReceiveTimeout(100);
    return jmsTemplate;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
      DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
      factory.setConnectionFactory(jmsConnectionFactory());
      factory.setConcurrency("1-1");
      return factory;
  }
  
  @Bean
  public SessionQueueSender sessionQueueSender() {
    return new SessionQueueSenderImpl();
  }
  
}
