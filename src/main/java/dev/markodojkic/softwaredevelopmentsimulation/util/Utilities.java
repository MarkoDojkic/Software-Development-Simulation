package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.diogonunes.jcolor.Attribute;
import com.google.common.util.concurrent.Uninterruptibles;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;

@UtilityClass
public class Utilities {
	private static final Logger logger = Logger.getLogger(Utilities.class.getName());
	public static final String STRING_FORMAT = "%s-%s";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	public static final String ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER = "assignedDevelopmentTeamPositionNumber";
	public static final SecureRandom SECURE_RANDOM = new SecureRandom();
	public static final Lorem LOREM = LoremIpsum.getInstance();
	public static final AtomicInteger IN_PROGRESS_EPICS_COUNT = new AtomicInteger(0);

	@Getter
	@Setter
	private static Path currentApplicationDataPath = Path.of("/");
	@Getter
	@Setter
	private static Path currentApplicationLogsPath = Path.of("/");
	@Getter
	@Setter
	private static IGateways iGateways;
	@Getter
	private static int totalDevelopmentTeamsPresent;

	static {
		ZonedDateTime now = ZonedDateTime.now();
		Path base = Paths.get(System.getProperty("user.home"),
				System.getProperty("app.groupId", "dev.markodojkic"),
				System.getProperty("app.artifactId", "software_development_simulation"),
				System.getProperty("app.version", "0.0.0-TESTING"));

		Utilities.setCurrentApplicationDataPath(base);
		Utilities.setCurrentApplicationLogsPath(Paths.get(String.valueOf(base),
				"logs",
				now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
				now.format(DateTimeFormatter.ofPattern("HH.mm.ss"))));
	}

	public static void generateRandomTasks(int epicCountDownLimit, int epicCountUpperLimit){
		List<Epic> epicList = new ArrayList<>();
		AtomicReference<String> jiraEpicCreatedOutput = new AtomicReference<>(Strings.EMPTY);
		int totalEpicsCount = SECURE_RANDOM.nextInt(epicCountDownLimit,epicCountUpperLimit);

		totalDevelopmentTeamsPresent = currentDevelopmentTeamsSetup.size();

		for (var i = 0; i < totalEpicsCount; i++) {
			int finalI = i;
			logger.log(Level.INFO, () -> colorize(String.format("* Generating EPIC #%d", finalI), Attribute.TEXT_COLOR(232), Attribute.BACK_COLOR(90)));
			Uninterruptibles.sleepUninterruptibly(10, TimeUnit.MILLISECONDS);
			String boardName = LOREM.getWords(1).toUpperCase(Locale.ROOT);
			Epic epic = Epic.builder()
					.name(LOREM.getTitle(3, 6))
					.description(LOREM.getWords(3, 6))
					.priority(Priority.values()[SECURE_RANDOM.nextInt(Priority.values().length)])
					.reporter(getTechnicalManager())
					.createdOn(ZonedDateTime.now())
					.userStories(new ArrayList<>())
					.build();

			epic.setId(String.format(STRING_FORMAT, boardName, Math.abs(Math.round((float) epic.hashCode() /100))));
			epic.setUserStories(generateUserStories(epic.getId(), boardName,
					SECURE_RANDOM.nextInt(epic.getPriority().getUrgency() + 1, epic.getPriority().getUrgency() + 3)));
			epicList.add(epic);
			//1 -BOLD, 21 - RESET BOLD / ADDS UNDERLINE, 24 - RESET UNDERLINE, 3 - ITALIC, 23 - RESET ITALIC
				jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m created EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					epic.getReporter().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));
			logger.log(Level.INFO, () -> colorize(String.format("+ Generated EPIC #%d", finalI), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(90)));
		}

		iGateways.sendToInfo("""
				All epics are created. Total developerTeams available: {0}
				Let`s now simulate development cycle for all {1} epics!"""
				.replace("{0}", Integer.toString(totalDevelopmentTeamsPresent))
				.replace("{1}", Integer.toString(totalEpicsCount)));

		iGateways.sendToJiraActivityStream(jiraEpicCreatedOutput.get().replaceFirst(".$", ""));

		epicList.forEach(epic -> iGateways.generateEpic(epic));
	}

	private List<UserStory> generateUserStories(String epicId, String boardName, int totalToGenerate){
		List<UserStory> userStoryList = new ArrayList<>();

		for (var i = 0; i < totalToGenerate; i++) {
			int finalI = i;
			logger.log(Level.INFO, () -> colorize(String.format("\t* Generating USER STORY #%d", finalI), Attribute.TEXT_COLOR(232), Attribute.BACK_COLOR(124)));
			Uninterruptibles.sleepUninterruptibly(25, TimeUnit.MILLISECONDS);
			UserStory userStory = UserStory.builder()
					.name(LOREM.getTitle(2, 4))
					.description(LOREM.getWords(2, 4))
					.priority(Priority.values()[SECURE_RANDOM.nextInt(Priority.values().length)])
					.epicId(epicId)
					.createdOn(ZonedDateTime.now())
					.technicalTasks(new ArrayList<>())
					.build();
			int totalTechnicalTasks = SECURE_RANDOM.nextInt(userStory.getPriority().getUrgency() + 3, userStory.getPriority().getUrgency() + 8);

			userStory.setId(String.format(STRING_FORMAT, boardName, Long.parseLong(epicId.split(boardName.concat("-"))[1]+1)+ (long) i *totalTechnicalTasks));

			userStory.setTechnicalTasks(generateTechnicalTasks(userStory.getId(), boardName,
					SECURE_RANDOM.nextInt(userStory.getPriority().getUrgency() + 3, userStory.getPriority().getUrgency() + 8)));
			userStoryList.add(userStory);
			logger.log(Level.INFO, () -> colorize(String.format("\t+ Generated USER STORY #%d", finalI), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(124)));
		}

		return userStoryList;
	}

	private List<TechnicalTask> generateTechnicalTasks(String userStoryId, String boardName, int totalToGenerate){
		List<TechnicalTask> technicalTaskList = new ArrayList<>();

		for (var i = 0; i < totalToGenerate; i++) {
			int finalI = i;
			logger.log(Level.INFO, () -> colorize(String.format("\t\t* Generating TECHNICAL TASK #%d", finalI), Attribute.BACK_COLOR(244), Attribute.TEXT_COLOR(0)));
			Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
			technicalTaskList.add(TechnicalTask.builder()
					.name(LOREM.getWords(2, 4))
					.description(LOREM.getParagraphs(2, 4))
					.priority(Priority.values()[SECURE_RANDOM.nextInt(Priority.values().length)])
					.userStoryId(userStoryId)
					.id(String.format(STRING_FORMAT, boardName, Long.parseLong(userStoryId.split(boardName.concat("-"))[1]+1)+i))
					.createdOn(ZonedDateTime.now())
					.build());
			logger.log(Level.INFO, () -> colorize(String.format("\t\t+ Generated TECHNICAL TASK #%d", finalI), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(244)));
		}

		return technicalTaskList;
	}
}