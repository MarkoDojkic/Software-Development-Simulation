package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class AMQPFlow {
	@Bean(name = "infoOutputAMQFlow")
	public IntegrationFlow infoOutputAMQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("information.amq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).exchangeName(rabbitTemplate.getExchange()).routingKey("infoOutput")).get();
	}

	@Bean(name = "jiraActivityStreamOutputAMQFlow")
	public IntegrationFlow jiraActivityStreamOutputAMQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("jiraActivityStream.amq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).exchangeName(rabbitTemplate.getExchange()).routingKey("jiraActivityStreamOutput")).get();
	}

	@Bean
	public IntegrationFlow errorOutputAMQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("error.amq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).exchangeName(rabbitTemplate.getExchange()).routingKey("errorOutput")).get();
	}

	//TODO: Intercept queue read message and flush to log file
}