package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IPrinter;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@MessageEndpoint
public class JiraActivityStream {
	@ServiceActivator(inputChannel = "epics", outputChannel = "epics") //Temporary message consumption
	public List<Epic> printCreatedEpics(List<Epic> epicList){
		AtomicReference<String> output = new AtomicReference<>("");
		epicList.forEach(epic -> output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m created \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$", //1 -BOLD, 21 - REMOVE BOLD but ADDS UNDERLINE, 24 - REMOVE UNDERLINE, 3 - ITALIC, 23 - REMOVE ITALIC
				epic.getReporter().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), epic.getCreatedOn().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat));
		Utilities.getIPrinter().sendToInfo(output.get().replaceFirst(".$", ""));
		return epicList;
	}

	@ServiceActivator(inputChannel = "inProgressEpic", outputChannel = "inProgressEpic")
	public Epic printInProgressEpic(Epic epic){
		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$", 
				epic.getReporter().getDisplayName(), epic.getAssignee().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), epic.getCreatedOn().plusSeconds(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on \033[3m\033[1mEPIC-%s\033[21m\033[24m - %s\033[23m ◴ %s$", 
				epic.getAssignee().getDisplayName(), Math.abs(epic.getId().hashCode()), epic.getName(), epic.getCreatedOn().plusSeconds(10+25).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		return epic;
	}

	@ServiceActivator(inputChannel = "currentSprintUserStories", outputChannel = "currentSprintUserStories")
	public List<UserStory> printInProgressEpic(List<UserStory> userStoryList){
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), userStoryList.get(0).getCreatedOn());

		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m started sprint for \033[3m\033[1mEPIC-%s\033[21m\033[24m \033[23m ◴ %s$",
				userStoryList.get(0).getReporter().getDisplayName(), Math.abs(userStoryList.get(0).getEpicId().hashCode()), userStoryList.get(0).getCreatedOn().plusSeconds(artificialOffsetSeconds).minusSeconds(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));

		AtomicReference<String> output = new AtomicReference<>("");
		userStoryList.forEach(userStory -> {
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m created \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getReporter().getDisplayName(), userStory.getAssignee().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds+10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
			output.accumulateAndGet(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on \033[3m\033[1mUS-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
					userStory.getAssignee().getDisplayName(), Math.abs(userStory.getId().hashCode()), userStory.getName(), userStory.getCreatedOn().plusSeconds(artificialOffsetSeconds+10+25).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))), String::concat);
		});

		Utilities.getIPrinter().sendToInfo(output.get().replaceFirst(".$", ""));
		return userStoryList;
	}

	@ServiceActivator(inputChannel = "toDoTechnicalTasks", outputChannel = "toDoTechnicalTasks")
	public TechnicalTask printToDoTechnicalTask(TechnicalTask technicalTask){
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m created \033[3m\033[1mTASK-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));

		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on \033[3m\033[1mTASK-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds+10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "doneTechnicalTask", outputChannel = "infoChannel")
	public String printDoneTechnicalTask(TechnicalTask technicalTask){
		Utilities.getIPrinter().sendToInfo(String.format("\033[1m%s\033[21m\033[24m logged '%sh' on \033[3m\033[1mTASK-%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), Math.abs(ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn())),Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		return String.format("\033[1m%s\033[21m\033[24m changed the status to Done on \033[3m\033[1mTASK-%s\033[21m\033[24m - %s\033[23m with resolution \033[1mDone\033[21m\033[24m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), Math.abs(technicalTask.getId().hashCode()), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", "");
	}
}
