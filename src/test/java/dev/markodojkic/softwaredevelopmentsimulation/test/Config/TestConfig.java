package dev.markodojkic.softwaredevelopmentsimulation.test.Config;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
	@Bean
	@Primary
	public MockConnectionFactory mockConnectionFactory() {
		MockConnectionFactory mockConnectionFactory = new MockConnectionFactory();
		mockConnectionFactory.setHost("localhost");
		mockConnectionFactory.setPort(5672);
		mockConnectionFactory.setUsername("guest");
		mockConnectionFactory.setPassword("guest");
		mockConnectionFactory.setVirtualHost("/");
		return mockConnectionFactory;
	}
}