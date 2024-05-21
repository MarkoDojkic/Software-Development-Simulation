package dev.markodojkic.softwaredevelopmentsimulation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.jdi.request.InvalidRequestStateException;
import dev.markodojkic.softwaredevelopmentsimulation.model.*;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.*;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

@MessageEndpoint
public class ProjectManager {
	@Autowired
	@Qualifier("currentSprintEpic")
	private MessageChannel currentSprintEpic;

	@Autowired
	@Qualifier("inProgressEpic")
	private MessageChannel inProgressEpic;

	@Autowired
	@Qualifier("inProgressUserStory")
	private MessageChannel inProgressUserStory;

	private final Map<String, Boolean> currentSprintUserStories = new HashMap<>();
	private final Map<String, Boolean> userStoriesTechnicalTasks = new HashMap<>();

	@ServiceActivator(inputChannel = "epicsInput")
	public void assignEpics(List<Epic> epicList){
		epicList.forEach(epic -> new Thread(() -> currentSprintEpic.send(MessageBuilder.withPayload(epic).build())).start());
	}

	@Splitter(inputChannel = "currentSprintEpic", outputChannel = "currentSprintUserStories")
	public List<UserStory> generateUserStories(Epic epic){
		List<UserStory> userStoryList = epic.getUserStories();
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), userStoryList.get(0).getCreatedOn());
		AtomicReference<String> output = new AtomicReference<>("");

		output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getReporter().getDisplayName(), epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().plusSeconds(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
		output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on EPIC: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), epic.getCreatedOn().plusSeconds(10+25).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
		output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m started sprint for EPIC: \033[3m\033[1m%s\033[21m\033[24m \033[23m ◴ %s$",
				userStoryList.get(0).getReporter().getDisplayName(), Math.abs(userStoryList.get(0).getEpicId().hashCode()), userStoryList.get(0).getCreatedOn().plusSeconds(artificialOffsetSeconds).minusSeconds(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);

		userStoryList.forEach(userStory -> {
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m created US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds+10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds+10+25).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			currentSprintUserStories.put(userStory.getId().concat("@").concat(epic.getId()), false);
		});

		Utilities.getIGateways().sendToJiraActivityStream(output.get().replaceFirst(".$", ""));

		new Thread(() -> inProgressEpic.send(MessageBuilder.withPayload(epic).build())).start();
		return epic.getUserStories();
	}

	@Splitter(inputChannel = "currentSprintUserStories", outputChannel = "toDoTechnicalTasks")
	public List<TechnicalTask> generateTechnicalTasks(UserStory userStory){
		new Thread(() -> inProgressUserStory.send(MessageBuilder.withPayload(userStory).build())).start();
		return userStory.getTechnicalTasks();
	}

	@Router(inputChannel = "toDoTechnicalTasks", sendTimeout = "100000", ignoreSendFailures = "false", autoStartup = "true", resolutionRequired = "true")
	public String routeTasksBasedOnUrgency(TechnicalTask technicalTask){
		userStoriesTechnicalTasks.put(technicalTask.getId().concat("@").concat(technicalTask.getUserStoryId()), false);
		return String.format("%sTechnicalTaskQueue", technicalTask.getPriority().name().toLowerCase(Locale.ROOT));
	}

	@ServiceActivator(inputChannel = "doneEpics")
	public void printDoneEpic(Epic epic){
		assert(epic != null); //Needed if output channel isn't provided
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on EPIC: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDone\033[21m\033[24m ◴ %s$",
				epic.getAssignee().getDisplayName(), epic.getId(), epic.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", "")); // 9 - STRIKE-THROUGH, 29 - RESET STRIKE-THROUGH
	}

	@ServiceActivator(inputChannel = "doneSprintUserStories")
	public void updateUserStoryStatus(UserStory userStory){
		assert(userStory != null);
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m logged '%.0fh' on US: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				userStory.getAssignee().getDisplayName(), Math.ceil((double) (UUID.randomUUID().hashCode() % 1000) / userStory.getAssignee().getExperienceCoefficient()), userStory.getId(), userStory.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on US: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
				userStory.getAssignee().getDisplayName(), userStory.getId(), userStory.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));

		currentSprintUserStories.put(userStory.getId().concat("@").concat(userStory.getEpicId()), true);
	}

	@ServiceActivator(inputChannel = "doneTechnicalTasks")
	public void updateTechnicalTaskStatus(TechnicalTask technicalTask){
		assert(technicalTask != null);
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m logged '%.0fh' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), Math.ceil((double) Math.abs(ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn())) / technicalTask.getAssignee().getExperienceCoefficient()),technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on TASK: \033[3m\033[1m\033[9m%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		userStoriesTechnicalTasks.put(technicalTask.getId().concat("@").concat(technicalTask.getUserStoryId()), true);
	}

	@ServiceActivator(inputChannel = "inProgressEpic", outputChannel="doneEpics", adviceChain = "retryAdvice")
	public Epic sendToDoneEpic(Epic epic) throws Exception {
		if(!markEpicAsDone(epic)) throw new InvalidRequestStateException("Not done yet - ".concat(epic.getId()));

		return epic;
	}

	@ServiceActivator(inputChannel="inProgressUserStory", outputChannel="doneSprintUserStories", adviceChain = "retryAdvice")
	public UserStory sendToDoneUserStory(UserStory userStory) throws Exception {
		if(!markUserStoryAsDone(userStory)) throw new InvalidRequestStateException("Not done yet - ".concat(userStory.getId()));

		return userStory;
	}

	public boolean markEpicAsDone(Epic epic){
		boolean isHashMapEmpty = currentSprintUserStories.isEmpty();
		boolean areUserStoryAddedToHashMap = currentSprintUserStories.entrySet().stream().anyMatch(value -> value.getKey().contains(Objects.requireNonNull(epic.getId())));
		boolean areAllUserStoriesMarkedAsDone = currentSprintUserStories.entrySet().stream().filter(value -> value.getKey().contains(epic.getId())).allMatch(value -> value.getValue().equals(Boolean.TRUE));

		return !isHashMapEmpty && areUserStoryAddedToHashMap && areAllUserStoriesMarkedAsDone;
	}

	public boolean markUserStoryAsDone(UserStory userStory){
		boolean isHashMapEmpty = userStoriesTechnicalTasks.isEmpty();
		boolean areTechnicalTasksAddedToHashMap = userStoriesTechnicalTasks.entrySet().stream().anyMatch(value -> value.getKey().contains(Objects.requireNonNull(userStory.getId()))
		);
		boolean areAllTechnicalTasksMarkedAsDone = userStoriesTechnicalTasks.entrySet().stream().filter(value -> value.getKey().contains(userStory.getId())).allMatch(value -> value.getValue().equals(Boolean.TRUE));

		return !isHashMapEmpty && areTechnicalTasksAddedToHashMap && areAllTechnicalTasksMarkedAsDone;
	}
}
