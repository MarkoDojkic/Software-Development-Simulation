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
	@Qualifier("inProgressEpic")
	private MessageChannel inProgressEpic;

	@Autowired
	@Qualifier("inProgressUserStory")
	private MessageChannel inProgressUserStory;

	private final Map<String, Boolean> currentSprintUserStories = new HashMap<>();
	private final Map<String, Boolean> userStoriesTechnicalTasks = new HashMap<>();

	@Splitter(inputChannel = "epicsInput", outputChannel = "currentSprintEpic") //temp solution, should be pooled one message at the time
	public List<Epic> temp(List<Epic> epic){
		return epic;
	}

	@Splitter(inputChannel = "currentSprintEpic", outputChannel = "currentSprintUserStories")
	public List<UserStory> generateUserStories(Epic epic){
		List<UserStory> userStoryList = epic.getUserStories();
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), userStoryList.get(0).getCreatedOn());
		AtomicReference<String> output = new AtomicReference<>("");

		output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getReporter().getDisplayName(), epic.getAssignee().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), epic.getCreatedOn().plusSeconds(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
		output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				epic.getAssignee().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), epic.getCreatedOn().plusSeconds(10+25).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
		output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m started sprint for \033[3m\033[1mEPIC-%s\033[21m\033[24m \033[23m ◴ %s$",
				userStoryList.get(0).getReporter().getDisplayName(), Math.abs(userStoryList.get(0).getEpicId().hashCode()), userStoryList.get(0).getCreatedOn().plusSeconds(artificialOffsetSeconds).minusSeconds(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);

		userStoryList.forEach(userStory -> {
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m created \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getAssignee().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds+10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds+10+25).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			currentSprintUserStories.put(userStory.getId().concat("@").concat(epic.getId()), false);
		});

		Utilities.getIPrinter().sendToJiraActivityStream(output.get().replaceFirst(".$", ""));

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
		Utilities.getIPrinter().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on \033[3m\033[1m\033[9mEPIC-%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDone\033[21m\033[24m ◴ %s$",
				epic.getAssignee().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", "")); // 9 - STRIKE-THROUGH, 29 - RESET STRIKE-THROUGH
	}

	@ServiceActivator(inputChannel = "doneSprintUserStories")
	public void updateUserStoryStatus(UserStory userStory){
		assert(userStory != null);
		Utilities.getIPrinter().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m logged '%.0fh' on \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				userStory.getAssignee().getDisplayName(), Math.ceil((double) (Math.abs(userStory.getId().hashCode()) % 100) / userStory.getAssignee().getExperienceCoefficient()), Math.abs(userStory.getId().hashCode()), userStory.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on \033[3m\033[1m\033[9mUS-%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
				userStory.getAssignee().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));

		currentSprintUserStories.put(userStory.getId().concat("@").concat(userStory.getEpicId()), true);
	}

	@ServiceActivator(inputChannel = "doneTechnicalTasks")
	public void updateTechnicalTaskStatus(TechnicalTask technicalTask){
		assert(technicalTask != null);
		Utilities.getIPrinter().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m logged '%.0fh' on \033[3m\033[1mTASK-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), Math.ceil((double) Math.abs(ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn())) / technicalTask.getAssignee().getExperienceCoefficient()),Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the status to Done on \033[3m\033[1m\033[9mTASK-%s\033[21m\033[24m\033[29m - %s\033[23m with resolution \033[1mDONE\033[21m\033[24m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
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
