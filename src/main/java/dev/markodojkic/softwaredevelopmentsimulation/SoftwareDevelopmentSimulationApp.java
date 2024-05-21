package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IPrinter;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.developers;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@ComponentScan
public class SoftwareDevelopmentSimulationApp
{


	public static void main(String[] args)
	{
		Utilities.setAbstractApplicationContext(new ClassPathXmlApplicationContext("/application.xml"));

		Utilities.setIPrinter(Utilities.getAbstractApplicationContext().getBean("IPrinter", IPrinter.class));
		var projectOwner = Utilities.getAbstractApplicationContext().getBean("projectOwner", ProjectOwner.class);

		iPrinter.sendToInfo("""
				Welcome to Software development simulator
				@Copyright(Marko Dojkić 2024)$Please wait patiently while software architect generates 15 epics!""");

		List<Epic> epicList = new ArrayList<>();
		AtomicReference<String> jiraEpicCreatedOutput = new AtomicReference<>("");

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
			jiraEpicCreatedOutput.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m created \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$", //1 -BOLD, 21 - RESET BOLD / ADDS UNDERLINE, 24 - RESET UNDERLINE, 3 - ITALIC, 23 - RESET ITALIC
					epic.getReporter().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), epic.getCreatedOn().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			System.out.println(colorize(String.format("+ Generated EPIC #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(90)));
		}

		iPrinter.sendToInfo("""
				All epics are created
				Let's now simulate development cycle for all 15 epics!""");

		Utilities.getIPrinter().sendToJiraActivityStream(jiraEpicCreatedOutput.get().replaceFirst(".$", ""));

		projectOwner.generateEpics(epicList);

		//Application has 1 minute timeout - TODO: Create GUI with shutdown button

		Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);

		Utilities.getAbstractApplicationContext().close();

		//TODO: Refactor epics to pooling (one at the time) - currently first is use for testing
		//TODO: Correct JIRA activity stream timings
	}

	private static List<UserStory> generateUserStories(String epicId, User epicAssignee, int totalToGenerate){
		List<UserStory> userStoryList = new ArrayList<>();

		for (var i = 0; i < totalToGenerate; i++) {
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

			userStory.setTechnicalTasks(generateTechnicalTasks(userStory.getId(), userStory.getAssignee(),
					random.nextInt(userStory.getPriority().getUrgency() + 3, userStory.getPriority().getUrgency() + 8)));
			userStoryList.add(userStory);
			System.out.println(colorize(String.format("\t+ Generated USER STORY #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(124)));
		}

		return userStoryList;
	}

	private static List<TechnicalTask> generateTechnicalTasks(String userStoryId, User userStoryAssignee, int totalToGenerate){
		List<TechnicalTask> technicalTaskList = new ArrayList<>();

		for (var i = 0; i < totalToGenerate; i++) {
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