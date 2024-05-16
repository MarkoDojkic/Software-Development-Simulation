package dev.markodojkic.softwaredevelopmentsimulation;

import java.util.List;
import java.util.Locale;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.Splitter;

@MessageEndpoint
public class ProjectManager {
	@Splitter(inputChannel = "epics", outputChannel = "currentSprintUserStories")
	public List<UserStory> generateUserStories(List<Epic> epic){
		return epic.get(0).getUserStories();
	}

	@Splitter(inputChannel = "currentSprintUserStories", outputChannel = "toDoTechnicalTasks")
	public List<TechnicalTask> generateTechnicalTasks(UserStory userStory){
		return userStory.getTechnicalTasks();
	}

	@Router(inputChannel = "toDoTechnicalTasks")
	public String routeTasksBasedOnUrgency(TechnicalTask technicalTask){
		return String.format("%sTechnicalTaskQueue", technicalTask.getPriority().name().toLowerCase(Locale.ROOT));
	}
}
