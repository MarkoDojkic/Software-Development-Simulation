package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import lombok.Setter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.diogonunes.jcolor.Ansi.colorize;

@Component
@Setter
public class Developer {
	private String trivialTaskTypicalResolutionTimeCoefficient;
	private String normalTaskTypicalResolutionTimeCoefficient;
	private String minorTaskTypicalResolutionTimeCoefficient;
	private String majorTaskTypicalResolutionTimeCoefficient;
	private String criticalTaskTypicalResolutionTimeCoefficient;
	private String blockerTaskTypicalResolutionTimeCoefficient;

	@ServiceActivator(inputChannel = "trivialTechnicalTask", outputChannel = "doneTechnicalTasks")
	public TechnicalTask trivialTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on trivial technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Utilities.simulatePause(Long.parseLong(trivialTaskTypicalResolutionTimeCoefficient)  / technicalTask.getAssignee().getExperienceCoefficient());
		System.out.println(colorize(String.format("###Developer %s finished working on trivial technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "normalTechnicalTask", outputChannel = "doneTechnicalTasks")
	public TechnicalTask normalTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on normal technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Utilities.simulatePause(Long.parseLong(normalTaskTypicalResolutionTimeCoefficient)  / technicalTask.getAssignee().getExperienceCoefficient());
		System.out.println(colorize(String.format("###Developer %s finished working on normal technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "minorTechnicalTask", outputChannel = "doneTechnicalTasks")
	public TechnicalTask minorTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on minor technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Utilities.simulatePause(Long.parseLong(minorTaskTypicalResolutionTimeCoefficient) / technicalTask.getAssignee().getExperienceCoefficient());
		System.out.println(colorize(String.format("###Developer %s finished working on minor technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "majorTechnicalTask", outputChannel = "doneTechnicalTasks")
	public TechnicalTask majorTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on major technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Utilities.simulatePause(Long.parseLong(majorTaskTypicalResolutionTimeCoefficient) / technicalTask.getAssignee().getExperienceCoefficient());
		System.out.println(colorize(String.format("###Developer %s finished working on major technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "criticalTechnicalTask", outputChannel = "doneTechnicalTasks")
	public TechnicalTask criticalTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on critical technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Utilities.simulatePause(technicalTask.getAssignee().getExperienceCoefficient() / Long.parseLong(criticalTaskTypicalResolutionTimeCoefficient));
		System.out.println(colorize(String.format("###Developer %s finished working on critical technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "blockerTechnicalTask", outputChannel = "doneTechnicalTasks")
	public TechnicalTask blockerTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		Utilities.getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on blocker technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Utilities.simulatePause(technicalTask.getAssignee().getExperienceCoefficient() / Long.parseLong(blockerTaskTypicalResolutionTimeCoefficient));
		System.out.println(colorize(String.format("###Developer %s finished working on blocker technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}
}
