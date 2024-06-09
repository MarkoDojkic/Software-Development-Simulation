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

	@Gateway(requestChannel = "information.ampq.input")
	void sendToInfoAMPQ(String message);

	@Gateway(requestChannel = "error.ampq.input")
	void sendToErrorAMPQ(String message);

	@Gateway(requestChannel = "jiraActivityStream.ampq.input")
	void sendToJiraActivityStreamAMPQ(String message);

	@Gateway(requestChannel = "epicMessage.input")
	void generateEpic(Epic epic);
}
