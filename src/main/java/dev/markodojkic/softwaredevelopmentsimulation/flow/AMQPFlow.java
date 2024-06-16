package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class AMQPFlow {

	public static final String EXCHANGE_NAME = "amq.topic";

	@Bean(name = "infoOutputAMQFlow")
	public IntegrationFlow infoOutputAMQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("information.amq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).routingKey("infoOutput").exchangeName(EXCHANGE_NAME)).get();
	}

	@Bean(name = "jiraActivityStreamOutputAMQFlow")
	public IntegrationFlow jiraActivityStreamOutputAMQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("jiraActivityStream.amq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).routingKey("jiraActivityStreamOutput").exchangeName(EXCHANGE_NAME)).get();
	}

	@Bean
	public IntegrationFlow errorOutputAMQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("error.amq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).routingKey("errorOutput").exchangeName(EXCHANGE_NAME)).get();
	}
}