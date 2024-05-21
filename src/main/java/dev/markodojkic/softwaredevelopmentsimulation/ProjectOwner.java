package dev.markodojkic.softwaredevelopmentsimulation;

import java.util.List;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import org.springframework.integration.annotation.Gateway;

public interface ProjectOwner {
	@Gateway(requestChannel = "epicsInput", replyChannel = "infoChannel")
	void generateEpics(List<Epic> epicList);
}
