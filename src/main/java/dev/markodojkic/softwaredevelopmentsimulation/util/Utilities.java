package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Uninterruptibles;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.nio.file.Files;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;

@UtilityClass
public class Utilities {
	private static final Logger logger = Logger.getLogger(Utilities.class.getName());
	private static final String STRING_FORMAT = "%s-%s";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");
	public static final String ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER = "assignedDevelopmentTeamPositionNumber";
	public static final SecureRandom SECURE_RANDOM = new SecureRandom();
	public static final Lorem LOREM = LoremIpsum.getInstance();
	public static final AtomicInteger IN_PROGRESS_EPICS_COUNT = new AtomicInteger(0);
	public static final String PREDEFINED_DATA = "predefinedData";

	@Getter
	private static final Path currentApplicationDataPath;
	@Getter
	private static final Path currentApplicationLogsPath;
	@Getter
	@Setter
	private static IGateways iGateways;
	@Getter
	private static int totalDevelopmentTeamsPresent;
	@Getter
	@Setter
	private static int totalEpicsCount;
	@Getter
	@Setter
	private static List<Epic> epicsForSaving;
	@Getter
	@Setter
	private static ObjectMapper objectMapper;

	static {
		boolean isTesting = System.getProperty("spring.profiles.active", "default").equals("test");

		currentApplicationDataPath = isTesting ? Paths.get("src/test/resources", "dev.markodojkic.software_development_simulation.testing_data") : Paths.get(System.getProperty("user.home"), "dev.markodojkic", "software_development_simulation", "1.4.0");

		currentApplicationLogsPath = Paths.get(String.valueOf(currentApplicationDataPath), "logs", isTesting ? "2012-12-12 00-00-00" : ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")));

		if(!Files.exists(currentApplicationDataPath)){
            try {
				Files.createDirectories(currentApplicationDataPath);
				Files.createDirectories(currentApplicationLogsPath);
            } catch (IOException e) {
                throw new ExceptionInInitializerError(e);
            }
		}
	}

    public static void loadPredefinedTasks(List<Epic> predefinedEpics){
		AtomicReference<String> jiraEpicCreatedOutput = new AtomicReference<>(Strings.EMPTY);
		getAvailableDevelopmentTeamIds().clear();
		predefinedEpics.forEach(epic -> {
			int epicDevelopmentTeamId = IntStream.range(0, getCurrentDevelopmentTeamsSetup().size())
					.filter(i -> getCurrentDevelopmentTeamsSetup().get(i).stream()
							.anyMatch(d -> Objects.equals(d.getId(), epic.getAssignee().getId())))
					.findFirst().orElse(0);
			if(!getAvailableDevelopmentTeamIds().contains(epicDevelopmentTeamId)) {
				getAvailableDevelopmentTeamIds().addLast(epicDevelopmentTeamId);
				logger.log(Level.FINE, "Adding new development team {0} to queue", epicDevelopmentTeamId);
			}
			jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m created EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					epic.getReporter().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));
		});

		totalDevelopmentTeamsPresent = getAvailableDevelopmentTeamIds().size();

		iGateways.sendToInfo("""
				All epics are created. Total developerTeams available: {0}
				Let`s now simulate development cycle for all {1} epics!"""
				.replace("{0}", Integer.toString(totalDevelopmentTeamsPresent))
				.replace("{1}", Integer.toString(predefinedEpics.size())));

		iGateways.sendToJiraActivityStream(jiraEpicCreatedOutput.get().replaceFirst(".$", ""));

		predefinedEpics.forEach(epic -> {
			logger.log(Level.FINE, "Found TODO Epic {0}", epic.getId());
			iGateways.generateEpic(epic);
		});
	}

	public static void generateRandomEpics(boolean save, int epicCountDownLimit, int epicCountUpperLimit){
		List<Epic> epicList = new ArrayList<>();
		AtomicReference<String> jiraEpicCreatedOutput = new AtomicReference<>(Strings.EMPTY);
		totalEpicsCount = epicCountDownLimit == epicCountUpperLimit ? epicCountDownLimit : SECURE_RANDOM.nextInt(epicCountUpperLimit - epicCountDownLimit + 1) + epicCountDownLimit;

		getAvailableDevelopmentTeamIds().addAll(IntStream.rangeClosed(0, getCurrentDevelopmentTeamsSetup().size() - 1).boxed().collect(Collectors.toCollection(ArrayList::new)));
		totalDevelopmentTeamsPresent = getCurrentDevelopmentTeamsSetup().size();

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

		epicsForSaving = save ? new ArrayList<>() : null;

		Collections.shuffle(getAvailableDevelopmentTeamIds());
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
					.createdOn(ZonedDateTime.now().plusSeconds(1))
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
					.createdOn(ZonedDateTime.now().plusSeconds(2))
					.build());
			logger.log(Level.INFO, () -> colorize(String.format("\t\t+ Generated TECHNICAL TASK #%d", finalI), Attribute.TEXT_COLOR(118), Attribute.BACK_COLOR(244)));
		}

		return technicalTaskList;
	}

	public static void addEpicForSaving(Epic epic) {
		epicsForSaving.add(epic);
	}

	public static void saveEpics(){
		try {
			String folderName = System.getProperty("spring.profiles.active", "default").equals("test") ? "2012-12-12 00-00-00" : ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
			Path parentDirectory = Utilities.getCurrentApplicationDataPath().resolve(PREDEFINED_DATA);

			Files.createDirectories(parentDirectory.resolve(folderName));

			Files.writeString(parentDirectory.resolve(folderName.concat("/sessionData.json")), objectMapper.writeValueAsString(epicsForSaving));
			Files.writeString(parentDirectory.resolve(folderName.concat("/developersData.json")), objectMapper.writeValueAsString(getCurrentDevelopmentTeamsSetup()));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}

