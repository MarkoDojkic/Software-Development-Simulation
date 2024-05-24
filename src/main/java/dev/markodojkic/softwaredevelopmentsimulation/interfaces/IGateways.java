package dev.markodojkic.softwaredevelopmentsimulation.interfaces;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.List;

@MessagingGateway
public interface IGateways {
	@Gateway(requestChannel = "information.input")
	void sendToInfo(String message);

	@Gateway(requestChannel = "error.input")
	void sendToError(String message);

	@Gateway(requestChannel = "jiraActivityStream.input")
	void sendToJiraActivityStream(String message);

	@Gateway(requestChannel = "epicsMessage.input")
	void generateEpics(List<Epic> epicList);
}
