package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PrintoutFlow {
	private static final String PRINTER_TRANSFORMER_BEAN = "printerTransformer";

	@Bean
	public IntegrationFlow informationPrintoutFlow(@Qualifier("information.mqtt.input") MessageChannel mqttMessageChannel, @Qualifier("information.logFile.input") MessageChannel logFileMessageChannel) {
		return IntegrationFlow.from("information.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "infoOutput")
				.log(LoggingHandler.Level.INFO, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.handle(message -> {
					mqttMessageChannel.send(message);
					logFileMessageChannel.send(message);
				}).get();
	}

	@Bean
	public IntegrationFlow jiraActivityStreamPrintoutFlow(@Qualifier("jiraActivityStream.mqtt.input") MessageChannel mqttMessageChannel, @Qualifier("jiraActivityStream.logFile.input") MessageChannel logFileMessageChannel) {
		return IntegrationFlow.from("jiraActivityStream.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "jiraActivityStreamOutput")
				.log(LoggingHandler.Level.INFO, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.handle(message -> {
					mqttMessageChannel.send(message);
					logFileMessageChannel.send(message);
				}).get();
	}

	@Bean
	public IntegrationFlow errorPrintoutFlow(@Qualifier("errorChannel.mqtt.input") MessageChannel mqttMessageChannel, @Qualifier("errorChannel.logFile.input") MessageChannel logFileMessageChannel) {
		return IntegrationFlow.from("errorChannel")
				.transform(PRINTER_TRANSFORMER_BEAN, "errorOutput")
				.log(LoggingHandler.Level.ERROR, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.handle(message -> {
					mqttMessageChannel.send(message);
					logFileMessageChannel.send(message);
				}).get();
	}
}