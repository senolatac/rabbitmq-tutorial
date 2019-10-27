package com.sha.rabbitmqtutorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitmqTutorialApplication {

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.port}")
	private int port;

	@Value("${spring.rabbitmq.virtualHost}")
	private String virtualHost;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	//If we don't define properties here, connection factory will use default settings described on properties.
	@Bean
	public ConnectionFactory connectionFactory() {
		AbstractConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setPort(port);
		connectionFactory.setHost(host);
		connectionFactory.setVirtualHost(virtualHost);
		return connectionFactory;
	}

	@Bean
	public MessageConverter messageConverter(){
		//POJO classes will be mapped to json objects to deal with RabbitMQ.
		ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
		return new Jackson2JsonMessageConverter(mapper);
	}

	@Bean
	public SimpleRabbitListenerContainerFactory createListener() {
		SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
		containerFactory.setConnectionFactory(connectionFactory());
		//Max consumer at the the same time.
		containerFactory.setMaxConcurrentConsumers(10);
		//It will start with 5 consumers.
		containerFactory.setConcurrentConsumers(5);
		containerFactory.setAutoStartup(true);
		containerFactory.setMessageConverter(messageConverter());
		containerFactory.setPrefetchCount(1);
		//If there is a problem, we don't queue them again.
		containerFactory.setDefaultRequeueRejected(false);
		return containerFactory;
	}

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqTutorialApplication.class, args);
	}

}
