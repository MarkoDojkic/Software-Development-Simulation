package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IPrinter;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.developers;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@ComponentScan
public class SoftwareDevelopmentSimulationApp
{
	public static void main(String[] args)
	{
		AbstractApplicationContext abstractApplicationContext = new ClassPathXmlApplicationContext("/application.xml");

		Utilities.setiPrinter((IPrinter) abstractApplicationContext.getBean("IPrinter"));
		var projectOwner = (ProjectOwner) abstractApplicationContext.getBean("projectOwner");

		iPrinter.sendToInfo("""
				Welcome to Software development simulator
				@Copyright(Marko Dojkić 2024)$Please wait patiently while software architect generates 15 epics!""");

		List<Epic> epicList = new ArrayList<>();

		for (var i = 0; i < random.nextInt(5,15); i++) {
			System.out.println(colorize(String.format("* Generating EPIC #%d", i), Attribute.TEXT_COLOR(232), Attribute.BACK_COLOR(90)));
			Utilities.simulatePause(200);
			Epic epic = Epic.builder()
					.name(lorem.getTitle(3, 6))
					.description(lorem.getParagraphs(5, 15))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.reporter(getRandomElementFromList(developers.stream()
							.filter(dev -> dev.getUserType() == UserType.SENIOR_DEVELOPER).toList()))
					.assignee(getRandomElementFromList(developers))
					.id(UUID.randomUUID().toString())
					.createdOn(ZonedDateTime.now())
					.userStories(new ArrayList<>())
					.build();

			epic.setUserStories(generateUserStories(epic.getId(), epic.getAssignee(),
					random.nextInt(epic.getPriority().getUrgency() + 1, epic.getPriority().getUrgency() + 3)));
			epicList.add(epic);
			System.out.println(colorize(String.format("+ Generated EPIC #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(90)));
		}

		iPrinter.sendToInfo("""
				All epics are created
				Let's now simulate development cycle for all 15 epics!""");

		projectOwner.generateEpics(epicList);

		abstractApplicationContext.close();

		//TODO: Refactor epics to pooling (one at the time) - currently first is use for testing
		//TODO: Move epic to done when currentSprintUserStories channel is empty
		//TODO: Move userStories to done when technicalTasks channel is empty
		//TODO: Correct JIRA activity stream timings
	}

	private static List<UserStory> generateUserStories(String epicId, User epicAssignee, int number){
		List<UserStory> userStoryList = new ArrayList<>();

		for (var i = 0; i < number; i++) {
			System.out.println(colorize(String.format("\t* Generating USER STORY #%d", i), Attribute.TEXT_COLOR(232), Attribute.BACK_COLOR(124)));
			Utilities.simulatePause(100);
			UserStory userStory = UserStory.builder()
					.name(lorem.getTitle(2, 4))
					.description(lorem.getParagraphs(2, 6))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.reporter(epicAssignee)
					.assignee(getRandomElementFromList(developers))
					.epicId(epicId)
					.id(UUID.randomUUID().toString())
					.createdOn(ZonedDateTime.now())
					.technicalTasks(new ArrayList<>())
					.build();

			userStory.setTechnicalTasks(generatTechnicalTasks(userStory.getId(), userStory.getAssignee(),
					random.nextInt(userStory.getPriority().getUrgency() + 3, userStory.getPriority().getUrgency() + 8)));
			userStoryList.add(userStory);
			System.out.println(colorize(String.format("\t+ Generated USER STORY #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(124)));
		}

		return userStoryList;
	}

	private static List<TechnicalTask> generatTechnicalTasks(String userStoryId, User userStoryAssignee, int number){
		List<TechnicalTask> technicalTaskList = new ArrayList<>();

		for (var i = 0; i < number; i++) {
			System.out.println(colorize(String.format("\t\t* Generating TECHNICAL TASK #%d", i), Attribute.BACK_COLOR(244), Attribute.TEXT_COLOR(0)));
			Utilities.simulatePause(50);
			technicalTaskList.add(TechnicalTask.builder()
					.name(lorem.getTitle(2, 4))
					.description(lorem.getParagraphs(2, 6))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.reporter(userStoryAssignee)
					.assignee(getRandomElementFromList(developers))
					.userStoryId(userStoryId)
					.id(UUID.randomUUID().toString())
					.createdOn(ZonedDateTime.now())
					.build());
			System.out.println(colorize(String.format("\t\t+ Generated TECHNICAL TASK #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(244)));
		}

		return technicalTaskList;
	}
}