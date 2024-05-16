package dev.markodojkic.softwaredevelopmentsimulation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Splitter;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.developers;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.getRandomElementFromList;

@MessageEndpoint(value = "projectManager")
public class ProjectManager {
	@Splitter(inputChannel = "epics", outputChannel = "user-stories")
	public List<UserStory> generateUserStories(List<Epic> epic){
		return epic.stream().flatMap(e -> e.getUserStories().stream()).toList();
	}

	@Splitter(inputChannel = "user-stories", outputChannel = "technical-tasks")
	public List<TechnicalTask> generateTechnicalTasks(List<UserStory> userStories){
		return userStories.stream().flatMap(us -> us.getTechnicalTasks().stream()).toList();
	}
}
