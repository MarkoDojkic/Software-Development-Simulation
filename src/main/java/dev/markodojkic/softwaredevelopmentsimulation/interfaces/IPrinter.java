package dev.markodojkic.softwaredevelopmentsimulation.interfaces;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(name = "iPrinter", defaultHeaders =
	@GatewayHeader(name = "description", value = "Printing in predefined format to console"))
public interface IPrinter {
	@Gateway(requestChannel = "infoChannel")
	void sendToInfo(String message);

	@Gateway(requestChannel = "errorChannel")
	void sendToError(String message);

	@Gateway(requestChannel = "jiraActivityStreamChannel")
	void sendToJiraActivityStream(String message);
}
