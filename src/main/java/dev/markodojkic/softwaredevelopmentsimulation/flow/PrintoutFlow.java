package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;

@Configuration
public class PrintoutFlow {
	private static final String PRINTER_TRANSFORMER_BEAN = "printerTransformer";

	@Bean
	public IntegrationFlow informationPrintoutFlow(){
		return IntegrationFlow.from("information.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "infoOutput")
				.log(LoggingHandler.Level.INFO, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.channel("information.mqtt.input").channel("information.logFile.input").get();
	}

	@Bean
	public IntegrationFlow jiraActivityStreamPrintoutFlow(){
		return IntegrationFlow.from("jiraActivityStream.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "jiraActivityStreamOutput")
				.log(LoggingHandler.Level.INFO, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.channel("jiraActivityStream.mqtt.input").channel("jiraActivityStream.logFile.input").get();
	}

	@Bean
	public IntegrationFlow errorPrintoutFlow(){
		return IntegrationFlow.from("errorChannel")
				.transform(PRINTER_TRANSFORMER_BEAN, "errorOutput")
				.log(LoggingHandler.Level.ERROR, message -> System.lineSeparator().concat(message.getPayload().toString()))
				.channel("error.mqtt.input").channel("error.logFile.input").get();
	}
}
