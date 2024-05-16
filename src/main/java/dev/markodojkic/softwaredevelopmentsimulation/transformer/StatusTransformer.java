package dev.markodojkic.softwaredevelopmentsimulation.transformer;

import lombok.Setter;
import org.springframework.integration.annotation.MessageEndpoint;


@Setter
@MessageEndpoint(value = "StatusTransformer")
public class StatusTransformer {
	private String[] baseTimesMilliseconds;
}
