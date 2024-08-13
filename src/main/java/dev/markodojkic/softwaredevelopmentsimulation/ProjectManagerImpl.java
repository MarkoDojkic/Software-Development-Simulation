package dev.markodojkic.softwaredevelopmentsimulation;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Streams;
import dev.markodojkic.softwaredevelopmentsimulation.model.*;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.util.EpicNotDoneException;
import dev.markodojkic.softwaredevelopmentsimulation.util.UserStoryNotDoneException;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.*;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@MessageEndpoint
public class ProjectManagerImpl {
	private static final Logger logger = Logger.getLogger(ProjectManagerImpl.class.getName());

	private final MessageChannel controlBusInput;
	private final MessageChannel inProgressEpic;
	private final MessageChannel inProgressUserStory;

	@Autowired
	public ProjectManagerImpl(@Qualifier("controlBus.input") MessageChannel controlBusInput, @Qualifier("inProgressEpic.intermediate") MessageChannel inProgressEpic, @Qualifier("inProgressUserStory.intermediate") MessageChannel inProgressUserStory) {
		this.controlBusInput = controlBusInput;
		this.inProgressEpic = inProgressEpic;
		this.inProgressUserStory = inProgressUserStory;
	}

	private final Map<String, Boolean> currentSprintUserStoriesMap = new HashMap<>();
	private final Map<String, Boolean> userStoriesTechnicalTasksMap = new HashMap<>();

	@Splitter(inputChannel = "currentSprintEpic.input", outputChannel = "currentSprintUserStories.preIntermediate")
	public List<UserStory> assignUserStoriesAndPrepareTechnicalTasks(Message<Epic> epicMessage){
		Epic epic = epicMessage.getPayload();
		List<Developer> assignedDevelopmentTeams = DataProvider.getCurrentDevelopmentTeamsSetup().get((Integer) epicMessage.getHeaders().getOrDefault(ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER, 0));
		epic.setAssignee(assignedDevelopmentTeams.getFirst());
		List<UserStory> userStoryList = epic.getUserStories();
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), userStoryList.getFirst().getCreatedOn());
		AtomicReference<String> jiraEpicCreatedOutput = new AtomicReference<>("");

		jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getReporter().getDisplayName(), epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().plusSeconds(10).format(DATE_TIME_FORMATTER)));
		jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().plusSeconds(35).format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));
		jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m started sprint for EPIC: \033[3m\033[1m%s\033[21m\033[24m \033[23m ◴ %s$",
				epic.getAssignee().getDisplayName(), String.valueOf(userStoryList.getFirst().getEpicId().hashCode()).replaceFirst("-", ""), userStoryList.getFirst().getCreatedOn().plusSeconds(artificialOffsetSeconds).minusSeconds(10).format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));

		epic.setUserStories(Streams.mapWithIndex(epic.getUserStories().stream(), (userStory, userStoryIndex) -> {
			userStory.setReporter(epic.getAssignee());
			userStory.setAssignee(assignedDevelopmentTeams.get(userStoryIndex == 0 ? 0 : (((int) userStoryIndex) + assignedDevelopmentTeams.size()) % (assignedDevelopmentTeams.size())));
			jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m created US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));
			jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));
			currentSprintUserStoriesMap.put(userStory.getId().concat("@").concat(epic.getId()), false);
			jiraEpicCreatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10 + 25).format(DATE_TIME_FORMATTER)).concat(jiraEpicCreatedOutput.get()));
			userStory.setTechnicalTasks(Streams.mapWithIndex(userStory.getTechnicalTasks().stream(), (technicalTask,technicalTaskIndex) -> {
				technicalTask.setReporter(userStory.getAssignee());
				technicalTask.setAssignee(assignedDevelopmentTeams.get(technicalTaskIndex == 0 ? 0 : (((int) technicalTaskIndex) + assignedDevelopmentTeams.size()) % (assignedDevelopmentTeams.size())));
				return technicalTask;
			}).toList());
			return userStory;
		}).toList());

		Utilities.getIGateways().sendToJiraActivityStream(jiraEpicCreatedOutput.get().replaceFirst(".$", ""));

		new Thread(() -> inProgressEpic.send(epicMessage)).start();

		if(Utilities.getTotalEpicsCount() != -1) {
			Utilities.addEpicForSaving(epic);
			Utilities.setTotalEpicsCount(Utilities.getTotalEpicsCount() - 1);
			if(Utilities.getTotalEpicsCount() == 0) Utilities.saveEpics();
		}

		return epic.getUserStories();
	}

	@Splitter(inputChannel = "currentSprintUserStories.preIntermediate", outputChannel = "toDoTechnicalTasks.input")
	public List<TechnicalTask> assignTechnicalTasks(UserStory userStory){
		new Thread(() -> inProgressUserStory.send(MessageBuilder.withPayload(userStory).build())).start();
		return userStory.getTechnicalTasks();
	}

	@Router(inputChannel = "toDoTechnicalTasks.input", sendTimeout = "100000", ignoreSendFailures = "false", autoStartup = "true", resolutionRequired = "true", suffix = "TechnicalTaskQueue.input")
	public String routeTechnicalTasksBasedOnUrgency(TechnicalTask technicalTask){
		userStoriesTechnicalTasksMap.put(technicalTask.getId().concat("@").concat(technicalTask.getUserStoryId()), false);
		return technicalTask.getPriority().name().toLowerCase(Locale.ROOT).split("\\.")[0];
	}

	@ServiceActivator(inputChannel = "inProgressEpic.intermediate", outputChannel="doneEpics.output", adviceChain = "retryAdvice")
	public Message<Epic> sendToDoneEpic(Message<Epic> epicMessage) throws EpicNotDoneException {
		if(!markEpicAsDone(epicMessage.getPayload())) throw new EpicNotDoneException(epicMessage.getPayload());

		return epicMessage;
	}

	@ServiceActivator(inputChannel="inProgressUserStory.intermediate", outputChannel="doneSprintUserStories.output", adviceChain = "retryAdvice")
	public UserStory sendToDoneUserStory(UserStory userStory) throws UserStoryNotDoneException {
		if(!markUserStoryAsDone(userStory)) throw new UserStoryNotDoneException(userStory);

		return userStory;
	}

	@ServiceActivator(inputChannel = "doneEpics.output")
	public void printDoneEpic(Message<Epic> epicMessage) {
		if(epicMessage != null) {
			Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on EPIC: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDone\033[21m\033[24m ◴ %s$",
					epicMessage.getPayload().getAssignee().getDisplayName(), epicMessage.getPayload().getId(), epicMessage.getPayload().getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).replaceFirst(".$", "")); // 9 - STRIKE-THROUGH, 29 - RESET STRIKE-THROUGH
			logger.log(Level.INFO, "{0} finished - Current count: {1}", new String[]{epicMessage.getPayload().getId(), String.valueOf(IN_PROGRESS_EPICS_COUNT.decrementAndGet())});
			DataProvider.getAvailableDevelopmentTeamIds().push(epicMessage.getHeaders().get(ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER, Integer.class));
			if(IN_PROGRESS_EPICS_COUNT.get() < Utilities.getTotalDevelopmentTeamsPresent()) controlBusInput.send(MessageBuilder.withPayload("@assignEpicFlow.start()").build());
		}
	}

	@ServiceActivator(inputChannel = "doneSprintUserStories.output")
	public void updateUserStoryStatus(UserStory userStory){
		if(userStory != null) {
			Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on US: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
					userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).concat(String.format("\033[1m%s\033[21m\033[24m logged '%.0fh' on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), Math.abs(Math.ceil((double) (UUID.nameUUIDFromBytes(userStory.getId().getBytes(StandardCharsets.UTF_8)).hashCode() % 1000) / userStory.getAssignee().getExperienceCoefficient())), userStory.getId(), userStory.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER))).replaceFirst(".$", ""));

			currentSprintUserStoriesMap.put(userStory.getId().concat("@").concat(userStory.getEpicId()), true);
		}
	}

	@ServiceActivator(inputChannel = "doneTechnicalTasks.output")
	public void updateTechnicalTaskStatus(TechnicalTask technicalTask){
		if(technicalTask != null) {
			Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on TASK: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).concat(String.format("\033[1m%s\033[21m\033[24m logged '%.0fh' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					technicalTask.getAssignee().getDisplayName(), Math.ceil((double) Math.abs(ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn())) / technicalTask.getAssignee().getExperienceCoefficient()), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER))).replaceFirst(".$", Strings.EMPTY));

			userStoriesTechnicalTasksMap.put(technicalTask.getId().concat("@").concat(technicalTask.getUserStoryId()), true);
		}
	}

	private boolean markEpicAsDone(Epic epic){
		return !currentSprintUserStoriesMap.isEmpty() && currentSprintUserStoriesMap.entrySet().stream().anyMatch(value -> value.getKey().contains(Objects.requireNonNull(epic.getId()))) && currentSprintUserStoriesMap.entrySet().stream().filter(value -> value.getKey().contains(epic.getId())).allMatch(value -> value.getValue().equals(Boolean.TRUE));
	}

	private boolean markUserStoryAsDone(UserStory userStory){
		return !userStoriesTechnicalTasksMap.isEmpty() && userStoriesTechnicalTasksMap.entrySet().stream().anyMatch(value -> value.getKey().contains(Objects.requireNonNull(userStory.getId()))) && userStoriesTechnicalTasksMap.entrySet().stream().filter(value -> value.getKey().contains(userStory.getId())).allMatch(value -> value.getValue().equals(Boolean.TRUE));
	}
}