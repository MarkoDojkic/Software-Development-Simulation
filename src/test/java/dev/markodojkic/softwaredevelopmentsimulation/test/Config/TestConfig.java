package dev.markodojkic.softwaredevelopmentsimulation.test.Config;

import com.github.fridujo.rabbitmq.mock.compatibility.MockConnectionFactoryFactory;
import com.github.fridujo.rabbitmq.mock.exchange.MockExchangeCreator;
import com.github.fridujo.rabbitmq.mock.exchange.MockTopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
	@Bean
	@Primary
	public ConnectionFactory rabbitConnectionFactoryTest() {
		return new CachingConnectionFactory(MockConnectionFactoryFactory.build().withAdditionalExchange(MockExchangeCreator.creatorWithExchangeType("topic", MockTopicExchange::new)));
	}

	@Bean
	@Primary
	public RabbitTemplate rabbitTemplateTest(){
		RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactoryTest());
		rabbitTemplate.setExchange("");
		rabbitTemplate.setMandatory(true);
		return rabbitTemplate;
	}
}