package com.example.spring.artemis.durable.sub;

import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;

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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@EnableJms
public class DurableSubscriptionConglomeratedTest {
	
	private static final String CONNECTION_URL = "tcp://localhost:61616";
	private static final String TOPIC_NAME = "exampleTopic";
	
	private Logger logger = LoggerFactory.getLogger(DurableSubscriptionConglomeratedTest.class);
	
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
		dmlc.setConcurrency("2-2");
		dmlc.setSubscriptionShared(true);
		
		// Automatically set with the above #setSubscriptionShared, but doing this for good measure
		dmlc.setPubSubDomain(true);
		return dmlc;
	}
	
	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jt = new JmsTemplate();
		jt.setConnectionFactory(connectionFactory);
		return jt;
	}

	// Seems to be an error when attempting to create binding to the topic:
	//     "AMQ119018: Binding already exists LocalQueueBinding" 
	@JmsListener(destination = TOPIC_NAME, subscription = "subscriber1")
	public void destinationListener(String testMessage) {
		logger.info("Received test message: " + testMessage);
	}
	
	public static void main(String [] args) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(DurableSubscriptionConglomeratedTest.class);
		ctx.refresh();
		
		ActiveMQTopic topic = new ActiveMQTopic(TOPIC_NAME);
		
		// Send a message
		ctx.getBean(JmsTemplate.class).convertAndSend(topic, "This is a string");
		
		// Wait more than enough time for the listener to consume the message
		TimeUnit.SECONDS.sleep(10);
		
		ctx.close();
	}

}