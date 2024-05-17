package dev.markodojkic.softwaredevelopmentsimulation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.*;
import org.springframework.integration.dsl.AggregatorSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@MessageEndpoint
public class ProjectManager {
	@Autowired
	private IntegrationFlowContext flowContext;

	@Splitter(inputChannel = "epics", outputChannel = "inProgressEpic")
	public Epic temp(List<Epic> epic){
		return epic.get(0);
	}//temp solution use pooler

	@Splitter(inputChannel = "inProgressEpic", outputChannel = "currentSprintUserStories")
	public List<UserStory> generateUserStories(Epic epic){
		return epic.getUserStories();
	}

	@Splitter(inputChannel = "currentSprintUserStories", outputChannel = "toDoTechnicalTasks")
	public List<TechnicalTask> generateTechnicalTasks(UserStory userStory){
		return userStory.getTechnicalTasks();
	}

	@Router(inputChannel = "toDoTechnicalTasks")
	public String routeTasksBasedOnUrgency(TechnicalTask technicalTask){
		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		return String.format("%sTechnicalTaskQueue", technicalTask.getPriority().name().toLowerCase(Locale.ROOT));
	}

	/*@ServiceActivator(inputChannel = "inProgressEpic", outputChannel = "doneEpics")
	public Epic markEpicAsDone(Epic epic){
		Map<String, Boolean> epicUserStories = epic.getUserStories().stream().collect(Collectors.toMap(BaseTask::getId, value -> false));

		this.flowContext.registration(IntegrationFlow.from("doneSprintUserStories")
				.handle(message -> {
					UserStory userStory = (UserStory) message.getPayload();
					if(userStory.getEpicId().equals(epic.getId())) epicUserStories.put(userStory.getId(), true);
				}).get());

		do Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
		while (!epicUserStories.values().stream().allMatch(value -> value.equals(Boolean.TRUE) ));

		return epic;
	}

	@ServiceActivator(inputChannel = "currentSprintUserStories", outputChannel = "doneSprintUserStories")
	public UserStory markUserStoryAsDone(UserStory userStory){
		Map<String, Boolean> epicTechnicalTasks = userStory.getTechnicalTasks().stream().collect(Collectors.toMap(BaseTask::getId, value -> false));

		this.flowContext.registration(IntegrationFlow.from("doneTechnicalTask")
				.handle(message -> {
					TechnicalTask technicalTask = (TechnicalTask) message.getPayload();
					if(technicalTask.getUserStoryId().equals(userStory.getId())) epicTechnicalTasks.put(technicalTask.getId(), true);
				}).get());

		do Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
		while (!epicTechnicalTasks.values().stream().allMatch(value -> value.equals(Boolean.TRUE) ));

		return userStory;
	}*/
}
