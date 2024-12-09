package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PrintoutFlow {
	@Bean
	public IntegrationFlow informationPrintoutFlow(@Qualifier("information.mqtt.input") MessageChannel mqttMessageChannel, @Qualifier("information.logFile.input") MessageChannel logFileMessageChannel) {
		return configurePrintoutFlow("information.input", "infoOutput", mqttMessageChannel, logFileMessageChannel, LoggingHandler.Level.INFO);
	}

	@Bean
	public IntegrationFlow jiraActivityStreamPrintoutFlow(@Qualifier("jiraActivityStream.mqtt.input") MessageChannel mqttMessageChannel, @Qualifier("jiraActivityStream.logFile.input") MessageChannel logFileMessageChannel) {
		return configurePrintoutFlow("jiraActivityStream.input", "jiraActivityStreamOutput", mqttMessageChannel, logFileMessageChannel, LoggingHandler.Level.INFO);
	}

	@Bean
	public IntegrationFlow errorPrintoutFlow(@Qualifier("errorChannel.mqtt.input") MessageChannel mqttMessageChannel, @Qualifier("errorChannel.logFile.input") MessageChannel logFileMessageChannel) {
		return configurePrintoutFlow("errorChannel", "errorOutput", mqttMessageChannel, logFileMessageChannel, LoggingHandler.Level.ERROR);
	}

	private StandardIntegrationFlow configurePrintoutFlow(String inputMessageChannel, String transformerMethod, MessageChannel mqttMessageChannel, MessageChannel logFileMessageChannel, LoggingHandler.Level loggingLevel) {
		return IntegrationFlow.from(inputMessageChannel)
				.transform("printerTransformer", transformerMethod)
				.log(loggingLevel, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.handle(message -> {
					mqttMessageChannel.send(message);
					logFileMessageChannel.send(message);
				}).get();
	}
}