package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import jakarta.annotation.PostConstruct;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.iGateways;

@MessageEndpoint
public class Developer {
	private Long trivialTaskTypicalResolutionTimeCoefficient;
	private Long normalTaskTypicalResolutionTimeCoefficient;
	private Long minorTaskTypicalResolutionTimeCoefficient;
	private Long majorTaskTypicalResolutionTimeCoefficient;
	private Long criticalTaskTypicalResolutionTimeCoefficient;
	private Long blockerTaskTypicalResolutionTimeCoefficient;

	@PostConstruct
	public void init(){
		trivialTaskTypicalResolutionTimeCoefficient = 1600L;
		normalTaskTypicalResolutionTimeCoefficient = 1420L;
		minorTaskTypicalResolutionTimeCoefficient = 1240L;
		majorTaskTypicalResolutionTimeCoefficient = 860L;
		criticalTaskTypicalResolutionTimeCoefficient = 540L;
		blockerTaskTypicalResolutionTimeCoefficient = 220L;
	}

	@ServiceActivator(inputChannel = "trivialTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask trivialTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on trivial technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(trivialTaskTypicalResolutionTimeCoefficient  / technicalTask.getAssignee().getExperienceCoefficient(), TimeUnit.MILLISECONDS);
		System.out.println(colorize(String.format("###Developer %s finished working on trivial technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "normalTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask normalTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on normal technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / normalTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		System.out.println(colorize(String.format("###Developer %s finished working on normal technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "minorTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask minorTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on minor technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / minorTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		System.out.println(colorize(String.format("###Developer %s finished working on minor technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "majorTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask majorTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on major technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / majorTaskTypicalResolutionTimeCoefficient , TimeUnit.MILLISECONDS);
		System.out.println(colorize(String.format("###Developer %s finished working on major technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "criticalTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask criticalTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on critical technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / criticalTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		System.out.println(colorize(String.format("###Developer %s finished working on critical technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "blockerTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask blockerTaskHandler(TechnicalTask technicalTask) {
		long artificialOffsetSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), technicalTask.getCreatedOn());
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).concat(String.format("\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(artificialOffsetSeconds + 10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))).replaceFirst(".$", ""));
		iGateways.sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))).replaceFirst(".$", ""));
		System.out.println(colorize(String.format("###Developer %s started working on blocker technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / blockerTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		System.out.println(colorize(String.format("###Developer %s finished working on blocker technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}
}
