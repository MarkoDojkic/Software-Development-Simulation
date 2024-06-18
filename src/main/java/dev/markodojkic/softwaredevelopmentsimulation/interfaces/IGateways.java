package dev.markodojkic.softwaredevelopmentsimulation.interfaces;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface IGateways {
	@Gateway(requestChannel = "information.input")
	void sendToInfo(String message);

	@Gateway(requestChannel = "error.input")
	void sendToError(String message);

	@Gateway(requestChannel = "jiraActivityStream.input")
	void sendToJiraActivityStream(String message);

	@Gateway(requestChannel = "information.mqtt.input")
	void sendToInfoMQTT(byte[] message);

	@Gateway(requestChannel = "error.mqtt.input")
	void sendToErrorMQTT(byte[] message);

	@Gateway(requestChannel = "jiraActivityStream.mqtt.input")
	void sendToJiraActivityStreamMQTT(byte[] message);

	@Gateway(requestChannel = "epicMessage.input")
	void generateEpic(Epic epic);
}
