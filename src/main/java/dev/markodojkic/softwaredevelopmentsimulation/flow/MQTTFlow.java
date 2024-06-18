package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Configuration
public class MQTTFlow {
	@Bean(name = "infoOutputMQTTFlow")
	public IntegrationFlow infoOutputMQTTFlow(MqttPahoMessageHandler rabbitMQMessageHandler){
		return IntegrationFlow.from("information.mqtt.input")
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

		return IntegrationFlow.from("error.mqtt.input")
				.transform(Message.class, message -> MessageBuilder.fromMessage(message)
						.setHeader(MqttHeaders.TOPIC, "error-printout-topic")
						.build()).handle(rabbitMQMessageHandler).get();
	}

	//TODO: Intercept queue read message and flush to log file
}