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
package com.example.spring.artemis.ssl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

/**
 * A simple JMS Queue example that uses SSL secure transport.
 */
@SpringBootApplication
public class SSLExample {
	
	@JmsListener(destination = "exampleQueue")
	public void messageConsumer(String text) {
		// Step 5. Receive the message
		 System.out.println("Received message: " + text);
	}

   public static void main(String[] args) throws Exception {
	   
	   // Step 1. Instantiate the Spring Boot context
	   ConfigurableApplicationContext context = SpringApplication.run(SSLExample.class);
	   
	   // Step 2. Create an example text message
	   // Note: Spring will perform the conversion operations behind the scenes
	   // To turn this into a JMS TextMessage
	   String text = "This is a text message";
	   
	   // Step 3. Retrieve the Spring Boot autoconfigured JmsTemplate  from the context
	   JmsTemplate messageProducer = context.getBean(JmsTemplate.class);
	   
	   // Step 4. Send the message
	   messageProducer.convertAndSend("exampleQueue", text);
	   
       System.out.println("Sent message: " + text);
       
       // Step 6. Close the context so things get cleaned up properly and the test ends
       context.close();
   }
}
