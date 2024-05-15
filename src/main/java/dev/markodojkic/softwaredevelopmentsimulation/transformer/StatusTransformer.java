package dev.markodojkic.softwaredevelopmentsimulation.transformer;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import lombok.Setter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;

@MessageEndpoint
@Setter
public class StatusTransformer {
	private String taskToPlannedTransferBaseTimeMilliseconds;

	@Transformer(inputChannel = "planned", outputChannel = "current-sprint")
	public BaseTask plannedToCurrentSprint(BaseTask baseTask){
		try {
			Thread.sleep(Utilities.strToLong(Double.parseDouble(taskToPlannedTransferBaseTimeMilliseconds)*baseTask.getPriority().getTypicalResolutionTimeCoefficient()));
			baseTask.setStatus(Status.IN_PROGRESS);
			return baseTask;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
			//Error print and return to planned
		}

	}
}
