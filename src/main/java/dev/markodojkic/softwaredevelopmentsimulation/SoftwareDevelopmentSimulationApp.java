package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IPrinter;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.developers;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@ComponentScan
public class SoftwareDevelopmentSimulationApp
{
	public static void main(String[] args)
	{
		AbstractApplicationContext abstractApplicationContext = new ClassPathXmlApplicationContext("/application.xml");

		var projectOwner = (ProjectOwner) abstractApplicationContext.getBean("projectOwner");
		var iPrinter = (IPrinter) abstractApplicationContext.getBean("IPrinter");

		iPrinter.sendToInfo("""
				Welcome to Software development simulator
				@Copyright(Marko Dojkić 2024)$Please wait patiently while software architect generates 15 epics!""");

		List<Epic> epicList = new ArrayList<>();

		for (var i = 0; i < random.nextInt(5,15); i++) {
			Epic epic = Epic.builder()
					.name(lorem.getTitle(3, 6))
					.description(lorem.getParagraphs(5, 15))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.status(Status.TO_DO)
					.reporter(getRandomElementFromList(developers.stream()
							.filter(dev -> dev.getUserType() == UserType.SENIOR_DEVELOPER).toList()))
					.assignee(getRandomElementFromList(developers))
					.id(UUID.randomUUID().toString())
					.userStories(new ArrayList<>())
					.build();


			epic.setUserStories(generateUserStories(epic.getId(), epic.getAssignee(),
					random.nextInt(epic.getPriority().getUrgency() + 2, 10 - epic.getPriority().getUrgency() + 2)));
			epicList.add(epic);
		}

		iPrinter.sendToInfo("""
				All epics are created
				Let's now simulate development cycle for all 15 epics!""");

		projectOwner.generateEpics(epicList);

		abstractApplicationContext.close();
	}

	private static List<UserStory> generateUserStories(String epicId, User epicAssignee, int number){
		List<UserStory> userStoryList = new ArrayList<>();

		for (var i = 0; i < number; i++) {
			UserStory userStory = UserStory.builder()
					.name(lorem.getTitle(2, 4))
					.description(lorem.getParagraphs(2, 6))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.status(Status.TO_DO)
					.reporter(epicAssignee)
					.assignee(getRandomElementFromList(developers))
					.epicId(epicId)
					.id(UUID.randomUUID().toString())
					.technicalTasks(new ArrayList<>())
					.build();


			userStory.setTechnicalTasks(generatTechnicalTasks(userStory.getId(), userStory.getAssignee(),
					random.nextInt(userStory.getPriority().getUrgency() + 2, 10 - userStory.getPriority().getUrgency() + 2)));
			userStoryList.add(userStory);
		}

		return userStoryList;
	}

	private static List<TechnicalTask> generatTechnicalTasks(String userStoryId, User userStoryAssignee, int number){
		List<TechnicalTask> technicalTaskList = new ArrayList<>();

		for (var i = 0; i < number; i++) {
			technicalTaskList.add(TechnicalTask.builder()
					.name(lorem.getTitle(2, 4))
					.description(lorem.getParagraphs(2, 6))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.status(Status.TO_DO)
					.reporter(userStoryAssignee)
					.assignee(getRandomElementFromList(developers))
					.userStoryId(userStoryId)
					.id(UUID.randomUUID().toString())
					.build());
		}

		return technicalTaskList;
	}
}
