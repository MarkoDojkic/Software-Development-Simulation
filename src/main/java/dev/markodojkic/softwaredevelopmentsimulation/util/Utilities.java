package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.diogonunes.jcolor.Attribute;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.developers;

@UtilityClass
public class Utilities {
	public static final Random random = new Random();
	public static final Lorem lorem = LoremIpsum.getInstance();
	@Setter
	public static IGateways iGateways;

	public static <T> T getRandomElementFromList(List<T> list){
		return list.get(random.nextInt(list.size()));
	}
	public static void simulatePause(long time){
		Uninterruptibles.sleepUninterruptibly(time, TimeUnit.MILLISECONDS);
	}

	public static void generateRandomData(int epicCountDownLimit, int epicCountUpperLimit){
		iGateways.sendToInfo("""
				Welcome to Software development simulator
				@Copyright(Marko Dojkić 2024)$Please wait patiently while software architect generates 15 epics!""");

		List<Epic> epicList = new ArrayList<>();
		AtomicReference<String> jiraEpicCreatedOutput = new AtomicReference<>("");
		int totalEpicsCount = random.nextInt(epicCountDownLimit,epicCountUpperLimit);
		User technicalManager = developers.get(0);
		List<List<User>> developerTeams = Lists.partition(developers.stream().skip(1).toList(), (developers.size() - 1) / totalEpicsCount);

		for (var i = 0; i < totalEpicsCount; i++) {
			System.out.println(colorize(String.format("* Generating EPIC #%d", i), Attribute.TEXT_COLOR(232), Attribute.BACK_COLOR(90)));
			Utilities.simulatePause(200);
			String boardName = lorem.getWords(1).toUpperCase(Locale.ROOT);
			Epic epic = Epic.builder()
					.name(lorem.getTitle(3, 6))
					.description(lorem.getParagraphs(5, 15))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.reporter(developerTeams.get(i).stream()
							.filter(dev -> dev.getUserType() == UserType.SENIOR_DEVELOPER).findFirst().orElse(technicalManager))
					.assignee(getRandomElementFromList(developerTeams.get(i)))
					.createdOn(ZonedDateTime.now())
					.userStories(new ArrayList<>())
					.build();

			epic.setId(String.format("%s-%s", boardName, Math.round((float) Math.abs(epic.hashCode()) /100)));
			epic.setUserStories(generateUserStories(epic.getId(), epic.getAssignee(), developerTeams.get(i), boardName,
					random.nextInt(epic.getPriority().getUrgency() + 1, epic.getPriority().getUrgency() + 3)));
			epicList.add(epic);
			jiraEpicCreatedOutput.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m created EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$", //1 -BOLD, 21 - RESET BOLD / ADDS UNDERLINE, 24 - RESET UNDERLINE, 3 - ITALIC, 23 - RESET ITALIC
					epic.getReporter().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			System.out.println(colorize(String.format("+ Generated EPIC #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(90)));
		}

		iGateways.sendToInfo("""
				All epics are created
				Let's now simulate development cycle for all 15 epics!""");

		iGateways.sendToJiraActivityStream(jiraEpicCreatedOutput.get().replaceFirst(".$", ""));

		iGateways.generateEpics(epicList);
	}

	private List<UserStory> generateUserStories(String epicId, User epicAssignee, List<User> developerTeam, String boardName, int totalToGenerate){
		List<UserStory> userStoryList = new ArrayList<>();

		for (var i = 0; i < totalToGenerate; i++) {
			System.out.println(colorize(String.format("\t* Generating USER STORY #%d", i), Attribute.TEXT_COLOR(232), Attribute.BACK_COLOR(124)));
			Utilities.simulatePause(100);
			UserStory userStory = UserStory.builder()
					.name(lorem.getTitle(2, 4))
					.description(lorem.getParagraphs(2, 6))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.reporter(epicAssignee)
					.assignee(getRandomElementFromList(developerTeam))
					.epicId(epicId)
					.createdOn(ZonedDateTime.now())
					.technicalTasks(new ArrayList<>())
					.build();
			int totalTechnicalTasks = random.nextInt(userStory.getPriority().getUrgency() + 3, userStory.getPriority().getUrgency() + 8);

			userStory.setId(String.format("%s-%s", boardName, Long.parseLong(epicId.split(boardName.concat("-"))[1]+1)+ (long) i *totalTechnicalTasks));

			userStory.setTechnicalTasks(generateTechnicalTasks(userStory.getId(), userStory.getAssignee(), developerTeam, boardName,
					random.nextInt(userStory.getPriority().getUrgency() + 3, userStory.getPriority().getUrgency() + 8)));
			userStoryList.add(userStory);
			System.out.println(colorize(String.format("\t+ Generated USER STORY #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(124)));
		}

		return userStoryList;
	}

	private List<TechnicalTask> generateTechnicalTasks(String userStoryId, User userStoryAssignee, List<User> developerTeam, String boardName, int totalToGenerate){
		List<TechnicalTask> technicalTaskList = new ArrayList<>();

		for (var i = 0; i < totalToGenerate; i++) {
			System.out.println(colorize(String.format("\t\t* Generating TECHNICAL TASK #%d", i), Attribute.BACK_COLOR(244), Attribute.TEXT_COLOR(0)));
			Utilities.simulatePause(50);
			technicalTaskList.add(TechnicalTask.builder()
					.name(lorem.getTitle(2, 4))
					.description(lorem.getParagraphs(2, 6))
					.priority(Priority.values()[random.nextInt(Priority.values().length)])
					.reporter(userStoryAssignee)
					.assignee(getRandomElementFromList(developerTeam))
					.userStoryId(userStoryId)
					.id(String.format("%s-%s", boardName, Long.parseLong(userStoryId.split(boardName.concat("-"))[1]+1)+i))
					.createdOn(ZonedDateTime.now())
					.build());
			System.out.println(colorize(String.format("\t\t+ Generated TECHNICAL TASK #%d", i), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(244)));
		}

		return technicalTaskList;
	}
}
