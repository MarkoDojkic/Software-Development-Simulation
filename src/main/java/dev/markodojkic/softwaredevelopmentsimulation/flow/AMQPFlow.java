package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class AMQPFlow {
	@Bean(name = "infoOutputAMPQFlow")
	public IntegrationFlow infoOutputAMPQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("information.ampq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).routingKey("infoOutput").exchangeName("amq.topic")).get();
	}

	@Bean(name = "jiraActivityStreamOutputAMPQFlow")
	public IntegrationFlow jiraActivityStreamOutputAMPQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("jiraActivityStream.ampq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).routingKey("jiraActivityStreamOutput").exchangeName("amq.topic")).get();
	}

	@Bean
	public IntegrationFlow errorOutputAMPQFlow(RabbitTemplate rabbitTemplate){
		return IntegrationFlow.from("error.ampq.input")
				.handle(Amqp.outboundAdapter(rabbitTemplate).routingKey("errorOutput").exchangeName("amq.topic")).get();
	}
}