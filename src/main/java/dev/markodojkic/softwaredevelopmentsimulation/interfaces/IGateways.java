package dev.markodojkic.softwaredevelopmentsimulation.interfaces;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface IGateways {
	@Gateway(requestChannel = "information.input")
	void sendToInfo(String message);

	@Gateway(requestChannel = "errorChannel")
	void sendToError(String message);

	@Gateway(requestChannel = "jiraActivityStream.input")
	void sendToJiraActivityStream(String message);

	@Gateway(requestChannel = "epicMessage.input")
	void generateEpic(Epic epic);
}
