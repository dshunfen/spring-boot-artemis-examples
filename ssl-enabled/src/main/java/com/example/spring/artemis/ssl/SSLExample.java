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
