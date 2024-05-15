package dev.markodojkic.softwaredevelopmentsimulation.util;

import org.springframework.integration.annotation.Gateway;

public interface IPrinter {
	@Gateway(requestChannel = "infoChannel")
	void sendToInfo(String message);

	@Gateway(requestChannel = "errorChannel")
	void sendToError(String message);
}
