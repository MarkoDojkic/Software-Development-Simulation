package dev.markodojkic.softwaredevelopmentsimulation.util;

import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class BaseTaskPriorityComparator implements Comparator<Message<BaseTask>> {
	@Override
	public int compare(Message<BaseTask> taskMessage1, Message<BaseTask> taskMessage2) {
		return Integer.compare(taskMessage2.getPayload().getPriority().getUrgency(), taskMessage1.getPayload().getPriority().getUrgency());
	}
}
