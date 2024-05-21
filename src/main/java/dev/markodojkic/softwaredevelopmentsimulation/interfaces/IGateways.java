package dev.markodojkic.softwaredevelopmentsimulation.interfaces;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import org.springframework.integration.annotation.Gateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IGateways {
	@Gateway(requestChannel = "infoChannel")
	void sendToInfo(String message);

	@Gateway(requestChannel = "errorChannel")
	void sendToError(String message);

	@Gateway(requestChannel = "jiraActivityStreamChannel")
	void sendToJiraActivityStream(String message);

	@Gateway(requestChannel = "epicsInput", replyChannel = "infoChannel")
	void generateEpics(List<Epic> epicList);
}
