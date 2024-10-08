package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Configuration
@SuppressWarnings("unchecked")
public class MQTTFlow {
	@Bean(name = "infoOutputMQTTFlow")
	public IntegrationFlow infoOutputMQTTFlow(MqttPahoMessageHandler rabbitMQMessageHandler){
		return IntegrationFlow.from("information.mqtt.input")
				//Unchecked cast cannot be resolved since Message<String> throws ClassCastException
				.transform(Message.class, message -> MessageBuilder.fromMessage(message)
						.setHeader(MqttHeaders.TOPIC, "information-printout-topic")
						.build()).handle(rabbitMQMessageHandler).get();
	}

	@Bean(name = "jiraActivityStreamOutputMQTTFlow")
	public IntegrationFlow jiraActivityStreamOutputMQTTFlow(MqttPahoMessageHandler rabbitMQMessageHandler){
		return IntegrationFlow.from("jiraActivityStream.mqtt.input")
				.transform(Message.class, message -> MessageBuilder.fromMessage(message)
						.setHeader(MqttHeaders.TOPIC, "java-activity-stream-printout-topic")
						.build()).handle(rabbitMQMessageHandler).get();
	}

	@Bean(name = "errorOutputMQTTFlow")
	public IntegrationFlow errorOutputMQTTFlow(MqttPahoMessageHandler rabbitMQMessageHandler){
		rabbitMQMessageHandler.setDefaultTopic("error-printout-topic");

		return IntegrationFlow.from("errorChannel.mqtt.input")
				.transform(Message.class, message -> MessageBuilder.fromMessage(message)
						.setHeader(MqttHeaders.TOPIC, "error-printout-topic")
						.build()).handle(rabbitMQMessageHandler).get();
	}
}