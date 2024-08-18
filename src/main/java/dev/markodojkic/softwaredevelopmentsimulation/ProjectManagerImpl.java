package dev.markodojkic.softwaredevelopmentsimulation;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.Streams;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.model.*;
import dev.markodojkic.softwaredevelopmentsimulation.util.EpicNotDoneException;
import dev.markodojkic.softwaredevelopmentsimulation.util.UserStoryNotDoneException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.*;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@MessageEndpoint
public class ProjectManagerImpl {
	private static final Logger logger = Logger.getLogger(ProjectManagerImpl.class.getName());

	private final MessageChannel controlBusInput;
	private final MessageChannel epicInput;
	private final MessageChannel inProgressEpic;
	private final MessageChannel inProgressUserStory;

	@Autowired
	public ProjectManagerImpl(@Qualifier("controlBus.input") MessageChannel controlBusInput, @Qualifier("epicMessage.input") MessageChannel epicInput, @Qualifier("inProgressEpic.intermediate") MessageChannel inProgressEpic, @Qualifier("inProgressUserStory.intermediate") MessageChannel inProgressUserStory) {
		this.controlBusInput = controlBusInput;
		this.epicInput = epicInput;
		this.inProgressEpic = inProgressEpic;
		this.inProgressUserStory = inProgressUserStory;
	}

	private final Map<String, Boolean> currentSprintUserStoriesMap = new HashMap<>();
	private final Map<String, Boolean> userStoriesTechnicalTasksMap = new HashMap<>();

	@Splitter(inputChannel = "currentSprintEpic.input", outputChannel = "currentSprintUserStories.preIntermediate")
	public List<UserStory> assignUserStoriesAndPrepareTechnicalTasks(Message<Epic> epicMessage){
		Epic epic = epicMessage.getPayload();
		List<Developer> assignedDevelopmentTeam = getCurrentDevelopmentTeamsSetup().get((Integer) epicMessage.getHeaders().getOrDefault(ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER, 0));
		if(epic.getReporter() == null) epic.setReporter(getTechnicalManager());
		if(epic.getAssignee() == null) epic.setAssignee(assignedDevelopmentTeam.getFirst());
		else {
			if(!assignedDevelopmentTeam.contains(epic.getAssignee())) { //In case of predefined list data, we check if we assigned correct development team
				epicInput.send(epicMessage);
				return List.of();
			}
		}
		AtomicReference<String> jiraAccumulatedOutput = new AtomicReference<>("");

		jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getReporter().getDisplayName(), epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().plusSeconds(SECURE_RANDOM.nextInt(10,25)).format(DATE_TIME_FORMATTER)));

		epic.setUserStories(Streams.mapWithIndex(epic.getUserStories().stream(), (userStory, userStoryIndex) -> {
			if(userStory.getReporter() == null) userStory.setReporter(epic.getAssignee());
			if(userStory.getAssignee() == null) userStory.setAssignee(assignedDevelopmentTeam.get(userStoryIndex == 0 ? 0 : (((int) userStoryIndex) + assignedDevelopmentTeam.size()) % (assignedDevelopmentTeam.size())));
			jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m created US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(SECURE_RANDOM.nextInt(25,35)).format(DATE_TIME_FORMATTER)).concat(jiraAccumulatedOutput.get()));
			jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(SECURE_RANDOM.nextInt(35,45)).format(DATE_TIME_FORMATTER)).concat(jiraAccumulatedOutput.get()));
			currentSprintUserStoriesMap.put(userStory.getId().concat("@").concat(epic.getId()), false);
			userStory.setTechnicalTasks(Streams.mapWithIndex(userStory.getTechnicalTasks().stream(), (technicalTask,technicalTaskIndex) -> {
				if(technicalTask.getReporter() == null) technicalTask.setReporter(userStory.getAssignee());
				if(technicalTask.getAssignee() == null) technicalTask.setAssignee(assignedDevelopmentTeam.get(technicalTaskIndex == 0 ? 0 : (((int) technicalTaskIndex) + assignedDevelopmentTeam.size()) % (assignedDevelopmentTeam.size())));
				jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
						technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(SECURE_RANDOM.nextInt(45, 55)).format(DATE_TIME_FORMATTER)).concat(jiraAccumulatedOutput.get()));
				jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
						technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(SECURE_RANDOM.nextInt(55, 60)).format(DATE_TIME_FORMATTER)).concat(jiraAccumulatedOutput.get()));
				return technicalTask;
			}).toList());
			return userStory;
		}).toList());

		jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m started sprint for EPIC: \033[3m\033[1m%s\033[21m\033[24m \033[23m ◴ %s$",
				epic.getAssignee().getDisplayName(), String.valueOf(epic.getId().hashCode()).replaceFirst("-", ""), ZonedDateTime.now().plusSeconds(SECURE_RANDOM.nextInt(5, 10)).format(DATE_TIME_FORMATTER)).concat(jiraAccumulatedOutput.get()));
		jiraAccumulatedOutput.set(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).concat(jiraAccumulatedOutput.get()));

		getIGateways().sendToJiraActivityStream(jiraAccumulatedOutput.get().replaceFirst(".$", ""));

		new Thread(() -> inProgressEpic.send(epicMessage)).start();

		if(getTotalEpicsCount() != -1) {
			addEpicForSaving(epic);
			setTotalEpicsCount(getTotalEpicsCount() - 1);
			if(getTotalEpicsCount() == 0) saveEpics();
		}

		return epic.getUserStories();
	}

	@Splitter(inputChannel = "currentSprintUserStories.preIntermediate", outputChannel = "toDoTechnicalTasks.input")
	public List<TechnicalTask> assignTechnicalTasks(UserStory userStory){
		new Thread(() -> {
			getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).replaceFirst(".$", ""));
			inProgressUserStory.send(MessageBuilder.withPayload(userStory).build());
		}).start();
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
			getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on EPIC: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDone\033[21m\033[24m ◴ %s$",
					epicMessage.getPayload().getAssignee().getDisplayName(), epicMessage.getPayload().getId(), epicMessage.getPayload().getName(), ZonedDateTime.now().plusSeconds(SECURE_RANDOM.nextInt(10, 20)).format(DATE_TIME_FORMATTER)).replaceFirst(".$", "")); // 9 - STRIKE-THROUGH, 29 - RESET STRIKE-THROUGH
			logger.log(Level.INFO, "{0} finished - Current count: {1}", new String[]{epicMessage.getPayload().getId(), String.valueOf(IN_PROGRESS_EPICS_COUNT.decrementAndGet())});
			getAvailableDevelopmentTeamIds().push(epicMessage.getHeaders().get(ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER, Integer.class));
			if(IN_PROGRESS_EPICS_COUNT.get() < getTotalDevelopmentTeamsPresent()) controlBusInput.send(MessageBuilder.withPayload("@assignEpicFlow.start()").build());
		}
	}

	@ServiceActivator(inputChannel = "doneSprintUserStories.output")
	public void updateUserStoryStatus(UserStory userStory){
		if(userStory != null) {
			getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on US: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
					userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), ZonedDateTime.now().plusSeconds(SECURE_RANDOM.nextInt(10)).format(DATE_TIME_FORMATTER)).concat(String.format("\033[1m%s\033[21m\033[24m logged '%dh' on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), calculateTotalLoggedTimeInHours(userStory.getReporter(), userStory.getAssignee(), userStory.getPriority()) / 10, userStory.getId(), userStory.getName(), ZonedDateTime.now().plusSeconds(SECURE_RANDOM.nextInt(25
							,45)).format(DATE_TIME_FORMATTER))).replaceFirst(".$", ""));

			currentSprintUserStoriesMap.put(userStory.getId().concat("@").concat(userStory.getEpicId()), true);
		}
	}

	@ServiceActivator(inputChannel = "doneTechnicalTasks.output")
	public void updateTechnicalTaskStatus(TechnicalTask technicalTask){
		if(technicalTask != null) {
			getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on TASK: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().plusSeconds(SECURE_RANDOM.nextInt(10)).format(DATE_TIME_FORMATTER)).concat(String.format("\033[1m%s\033[21m\033[24m logged '%dh' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					technicalTask.getAssignee().getDisplayName(), calculateTotalLoggedTimeInHours(technicalTask.getReporter(), technicalTask.getAssignee(), technicalTask.getPriority()), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER))).replaceFirst(".$", Strings.EMPTY));

			userStoriesTechnicalTasksMap.put(technicalTask.getId().concat("@").concat(technicalTask.getUserStoryId()), true);
		}
	}

	private boolean markEpicAsDone(Epic epic){
		return !currentSprintUserStoriesMap.isEmpty() && currentSprintUserStoriesMap.entrySet().stream().anyMatch(value -> value.getKey().contains(Objects.requireNonNull(epic.getId()))) && currentSprintUserStoriesMap.entrySet().stream().filter(value -> value.getKey().contains(epic.getId())).allMatch(value -> value.getValue().equals(Boolean.TRUE));
	}

	private boolean markUserStoryAsDone(UserStory userStory){
		return !userStoriesTechnicalTasksMap.isEmpty() && userStoriesTechnicalTasksMap.entrySet().stream().anyMatch(value -> value.getKey().contains(Objects.requireNonNull(userStory.getId()))) && userStoriesTechnicalTasksMap.entrySet().stream().filter(value -> value.getKey().contains(userStory.getId())).allMatch(value -> value.getValue().equals(Boolean.TRUE));
	}

	private int calculateTotalLoggedTimeInHours(Developer reporter, Developer assignee, Priority taskPriority){
		double reporterExpertise = reporter.getExperienceCoefficient() * reporter.getDeveloperType().getSeniorityCoefficient();
		double assigneeExpertise = assignee.getExperienceCoefficient() * assignee.getDeveloperType().getSeniorityCoefficient();
		double averageExpertise = (reporterExpertise + assigneeExpertise) / 2.0;

		return (int) Math.round(Math.clamp(1.0, 240.0 * (taskPriority.getResolutionTimeCoefficient() / averageExpertise) * (1.0 / (1 + taskPriority.getUrgency())), 240.0));
	}
}