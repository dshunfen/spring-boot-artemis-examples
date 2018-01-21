package com.example.spring.artemis.hello;

import java.util.concurrent.CountDownLatch;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * A simple JMS Queue Hello World example.
 */
@SpringBootApplication
public class HelloWorldExample {
	
	public static void main(String[] args) throws Exception {

		// Step 1. Instantiate the Spring Boot context
		ConfigurableApplicationContext context = SpringApplication.run(HelloWorldExample.class);

		// Step 2. Create an example text message
		String text = "This is a text message";

		// Step 3. Retrieve the Spring Boot autoconfigured JmsTemplate from the context
		JmsTemplate messageProducer = context.getBean(JmsTemplate.class);

		// Step 4. Send the message
		messageProducer.convertAndSend("exampleQueue", text);

		System.out.println("Sent message: " + text);
		
		// Step 5. Wait until we've received the message
		context.getBean(MessageConsumer.class).getLatch().await();

		// Step 6. Close the context so things get cleaned up properly and the test ends
		context.close();
	}
	
	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		String testUri = "tcp://localhost:61616";
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(testUri);
		return cf;
	}

	@Component
	public class MessageConsumer {

		private CountDownLatch latch = new CountDownLatch(1);

		@JmsListener(destination = "exampleQueue")
		public void messageConsumer(String text) {
			// Step 5. Receive the message and release waiting latch
			System.out.println("Received message: " + text);
			latch.countDown();
		}
		
		public CountDownLatch getLatch() {
			return latch;
		}

	}
}
