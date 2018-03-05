package com.example.spring.artemis.durable.sub;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import io.netty.util.internal.ThreadLocalRandom;

@Configuration
@EnableJms
public class DurableSubscriptionConglomeratedTest {
	
	private static final String CONNECTION_URL = "tcp://oasis-event-d2:61616";
	private static final String TOPIC_NAME = "Oasis.Uat.1";
	
	private static int group = 0;
	
	private Logger logger = LoggerFactory.getLogger(DurableSubscriptionConglomeratedTest.class);
	
//	@Bean(destroyMethod = "stop")
//	public ActiveMQServer testBroker() throws Exception {
//		EmbeddedActiveMQ server = new EmbeddedActiveMQ();
//		server.setConfigResourcePath("broker.xml");
//		server.start();
//		return server.getActiveMQServer();
//	}
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		ActiveMQConnectionFactory cf = 
				new ActiveMQConnectionFactory(CONNECTION_URL);
		return cf;
	}
	
	@Bean
	public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory(
			ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory dmlc = new DefaultJmsListenerContainerFactory();
		dmlc.setConnectionFactory(connectionFactory);
		
		// This sets the concurrency on the subscription, creating two message consumers
		dmlc.setConcurrency("1-50");
		dmlc.setSubscriptionShared(true);
//		dmlc.setSubscriptionDurable(true);
		
		// Automatically set with the above #setSubscriptionShared, but doing this for good measure
		dmlc.setPubSubDomain(true);
		return dmlc;
	}
	
	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jt = new JmsTemplate();
		jt.setConnectionFactory(connectionFactory());
		return jt;
	}
	
	@Bean
	public JmsTransactionManager jmsTransactionManager() {
		JmsTransactionManager jtm = new JmsTransactionManager(connectionFactory());
		return jtm;
	}
	
	@Bean
	public TransactionTemplate transactionTemplate() {
		TransactionTemplate tt = new TransactionTemplate(jmsTransactionManager());
		return tt;
	}
	
	// Seems to be an error when attempting to create binding to the topic:
	//     "AMQ119018: Binding already exists LocalQueueBinding" 
	@JmsListener(destination = TOPIC_NAME, subscription = "perf.test.subscriber")
	public void destinationListener(String testMessage) {
		logger.info("Received test message: " + testMessage);
	}
	
	public static void main(String [] args) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(DurableSubscriptionConglomeratedTest.class);
		ctx.refresh();
		
//		ActiveMQTopic topic = new ActiveMQTopic(TOPIC_NAME);
//		
//		JmsTemplate jmsTemplate = ctx.getBean(JmsTemplate.class);
//		TransactionTemplate jmsTransactionTemplate = ctx.getBean(TransactionTemplate.class);		
//		
//		// Send a message
//		for (; group < 10; group++) {
//			jmsTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
//				@Override
//				protected void doInTransactionWithoutResult(TransactionStatus status) {
//					for (int y = 0; y < 1000; y++) {
//						jmsTemplate.convertAndSend(topic, "This is message #" + y + " in group #" + group);
//					}
//				}
//			});
//			TimeUnit.SECONDS.sleep(10);
//		}
		
		// Wait more than enough time for the listener to consume the message
		TimeUnit.MINUTES.sleep(5);
		
		ctx.close();
	}

}