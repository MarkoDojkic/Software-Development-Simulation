package dev.markodojkic.softwaredevelopmentsimulation.flow;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import java.util.logging.Logger;

@Configuration
public class PrintoutFlow {
	public static final String PRINTER_TRANSFORMER_BEAN = "printerTransformer";
	private static final Logger logger = Logger.getLogger(PrintoutFlow.class.getName());

	@Bean
	public IntegrationFlow informationPrintout(){
		return IntegrationFlow.from("information.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "infoOutput")
				.handle(message -> {
					logger.info(System.lineSeparator().concat(message.getPayload().toString()));
					Utilities.getIGateways().sendToInfoAMQ(message.getPayload().toString());
				}).get();
	}

	@Bean
	public IntegrationFlow jiraActivityStreamPrintout(){
		return IntegrationFlow.from("jiraActivityStream.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "jiraActivityStreamOutput")
				.handle(message -> {
					logger.info(System.lineSeparator().concat(message.getPayload().toString()));
					Utilities.getIGateways().sendToJiraActivityStreamAMQ(message.getPayload().toString());
				}).get();
	}

	@Bean
	public IntegrationFlow errorPrintout(){
		return IntegrationFlow.from("error.input")
				.transform(PRINTER_TRANSFORMER_BEAN, "errorOutput")
				.handle(message -> {
					logger.severe(System.lineSeparator().concat(message.getPayload().toString()));
					Utilities.getIGateways().sendToErrorAMQ(message.getPayload().toString());
				}).get();
	}
}
