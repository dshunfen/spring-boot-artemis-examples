/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.spring.artemis.durable.sub;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * A simple JMS example that shows how to use a durable subscription.
 */
@SpringBootApplication
public class DurableSubscriptionExample {

	public static void main(String[] args) throws Exception {

		// Step 1. Instantiate the Spring Boot context
		ConfigurableApplicationContext context = SpringApplication.run(DurableSubscriptionExample.class);
		MessageConsumer messageConsumer = context.getBean(MessageConsumer.class);

		// Step 2. Retrieve the Spring Boot autoconfigured JmsTemplate from the context
		JmsMessagingTemplate messageProducer = context.getBean(JmsMessagingTemplate.class);

		ActiveMQTopic topic = new ActiveMQTopic("exampleTopic");

		// Step 3. Send the message
		String text = "This is a text message 1";
		messageProducer.convertAndSend(topic, text);

		System.out.println("Sent message: " + text);

		// Step 4. Wait until we consume the message from the durable subscription
		messageConsumer.getLatch().await();
		messageConsumer.reinitializeLatch(1);
		
		JmsListenerEndpointRegistry jmsReg = context.getBean(JmsListenerEndpointRegistry.class);
		jmsReg.stop();
		
		TimeUnit.MINUTES.sleep(1);

		// Step 5. Send a second message
		String text2 = "This is a text message 2";
		messageProducer.convertAndSend(topic, text2);

		System.out.println("Sent message: " + text2);
		
		jmsReg.start();
		
		messageConsumer.getLatch().await();
		
		// Step 7. Close the context so things get cleaned up properly and the test ends
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

		@JmsListener(destination = "exampleTopic", id = "durable-client", subscription = "subscriber-1")
		public void messageConsumer(String text) {
			// Step 7. Receive the message and release waiting latch
			System.out.println("Received message: " + text);
			latch.countDown();
		}
		
		public void reinitializeLatch(int count) {
			this.latch = new CountDownLatch(count);
		}
		
		public CountDownLatch getLatch() {
			return latch;
		}

	}
}
