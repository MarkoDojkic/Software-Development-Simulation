package dev.markodojkic.softwaredevelopmentsimulation;

import java.util.ArrayList;
import java.util.List;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Splitter;

@MessageEndpoint
public class ProjectManager {
	@Splitter(inputChannel = "epics", outputChannel = "user-stories")
	public List<UserStory> generateUserStories(List<Epic> epic){
		List<UserStory> generatedUSs = new ArrayList<>();

		epic.forEach(e -> generatedUSs.add(new UserStory(/*populate with random values*/)));

		return generatedUSs;
	}

	@Splitter(inputChannel = "user-stories", outputChannel = "technical-tasks")
	public List<TechnicalTask> generateTechnicalTasks(List<UserStory> userStories){
		List<TechnicalTask> generatedTTs = new ArrayList<>();

		userStories.forEach(us -> generatedTTs.add(new TechnicalTask(/*populate with random values*/)));

		return generatedTTs;
	}
}
