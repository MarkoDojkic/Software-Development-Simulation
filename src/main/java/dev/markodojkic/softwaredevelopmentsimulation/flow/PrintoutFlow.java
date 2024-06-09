package dev.markodojkic.softwaredevelopmentsimulation.flow;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class PrintoutFlow {
	@Bean
	public IntegrationFlow informationPrintout(){
		return IntegrationFlow.from("information.input")
				.transform("printerTransformer", "infoOutput")
				.handle(message -> {
					System.out.println(message.getPayload());
					Utilities.iGateways.sendToInfoAMPQ(message.getPayload().toString());
				}).get();
	}

	@Bean
	public IntegrationFlow jiraActivityStreamPrintout(){
		return IntegrationFlow.from("jiraActivityStream.input")
				.transform("printerTransformer", "jiraActivityStreamOutput")
				.handle(message -> {
					System.out.println(message.getPayload());
					Utilities.iGateways.sendToJiraActivityStreamAMPQ(message.getPayload().toString());
				}).get();
	}

	@Bean
	public IntegrationFlow errorPrintout(){
		return IntegrationFlow.from("error.input")
				.transform("printerTransformer", "errorOutput")
				.handle(message -> {
					System.out.println(message.getPayload());
					Utilities.iGateways.sendToErrorAMPQ(message.getPayload().toString());
				}).get();
	}
}
