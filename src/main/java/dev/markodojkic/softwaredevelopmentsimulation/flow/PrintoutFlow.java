package dev.markodojkic.softwaredevelopmentsimulation.flow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class PrintoutFlow {
	@Bean
	IntegrationFlow informationPrintout(){
		return IntegrationFlow.from("information.input")
				.transform("printerTransformer", "infoOutput")
				.handle(message -> System.out.println(message.getPayload())).get();
	}

	@Bean
	IntegrationFlow jiraActivityStreamPrintout(){
		return IntegrationFlow.from("jiraActivityStream.input")
				.transform("printerTransformer", "jiraActivityStreamOutput")
				.handle(message -> System.out.println(message.getPayload())).get();
	}

	@Bean
	IntegrationFlow errorPrintout(){
		return IntegrationFlow.from("error.input")
				.transform("printerTransformer", "errorOutput")
				.handle(message -> System.out.println(message.getPayload())).get();
	}
}
