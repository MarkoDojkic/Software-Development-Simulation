package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.integration.dsl.StandardIntegrationFlow;
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
	public IntegrationFlow infoOutputMQTTFlow(MqttPahoMessageHandler mqttMessageHandler) {
		return createMQTTFlow("information.mqtt.input", "information-printout-topic", mqttMessageHandler);
	}

	@Bean(name = "jiraActivityStreamOutputMQTTFlow")
	public IntegrationFlow jiraActivityStreamOutputMQTTFlow(MqttPahoMessageHandler mqttMessageHandler) {
		return createMQTTFlow("jiraActivityStream.mqtt.input", "java-activity-stream-printout-topic", mqttMessageHandler);
	}

	@Bean(name = "errorOutputMQTTFlow")
	public IntegrationFlow errorOutputMQTTFlow(MqttPahoMessageHandler mqttMessageHandler) {
		return createMQTTFlow("errorChannel.mqtt.input", "error-printout-topic", mqttMessageHandler);
	}

	private StandardIntegrationFlow createMQTTFlow(String inputChannelName, String topic, MqttPahoMessageHandler mqttMessageHandler) {
		return IntegrationFlow.from(inputChannelName)
				.transform(Message.class, message ->
						MessageBuilder.fromMessage(message)
								.setHeader(MqttHeaders.TOPIC, topic)
								.build())
				.handle(mqttMessageHandler)
				.get();
	}
}