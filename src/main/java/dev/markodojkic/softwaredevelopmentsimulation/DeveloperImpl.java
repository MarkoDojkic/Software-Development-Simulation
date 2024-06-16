package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import jakarta.annotation.PostConstruct;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.diogonunes.jcolor.Ansi.colorize;

@MessageEndpoint
public class DeveloperImpl {
	private static final Logger logger = Logger.getLogger(DeveloperImpl.class.getName());
	private static final String CREATED_TASK_FORMAT = "\033[1m%s\033[21m\033[24m created TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$";
	private static final String CHANGED_THE_ASSIGNEE_TO_FORMAT = "\033[1m%s\033[21m\033[24m changed the Assignee to '\033[1m%s\033[21m\033[24m' on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$";
	private static final String CHANGED_STATUS_TO_IN_PROGRESS_FORMAT = "\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	
	private Long trivialTaskTypicalResolutionTimeCoefficient;
	private Long normalTaskTypicalResolutionTimeCoefficient;
	private Long minorTaskTypicalResolutionTimeCoefficient;
	private Long majorTaskTypicalResolutionTimeCoefficient;
	private Long criticalTaskTypicalResolutionTimeCoefficient;
	private Long blockerTaskTypicalResolutionTimeCoefficient;

	private long getArtificialOffsetInSeconds(Temporal createdTaskOn){
		return ChronoUnit.SECONDS.between(ZonedDateTime.now(), createdTaskOn);
	}

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
		sendToJiraCreatedTaskInfo(technicalTask);
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s started working on trivial technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(trivialTaskTypicalResolutionTimeCoefficient  / technicalTask.getAssignee().getExperienceCoefficient(), TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s finished working on trivial technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	private static void sendToJiraSetStatusToInProgressInfo(TechnicalTask technicalTask) {
		Utilities.getIGateways().sendToJiraActivityStream(String.format(CHANGED_STATUS_TO_IN_PROGRESS_FORMAT,
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).replaceFirst(".$", ""));
	}

	private void sendToJiraCreatedTaskInfo(TechnicalTask technicalTask) {
		Utilities.getIGateways().sendToJiraActivityStream(String.format(CREATED_TASK_FORMAT,
				technicalTask.getReporter().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(getArtificialOffsetInSeconds(technicalTask.getCreatedOn())).format(DATE_TIME_FORMATTER)).concat(String.format(CHANGED_THE_ASSIGNEE_TO_FORMAT,
				technicalTask.getReporter().getDisplayName(), technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), technicalTask.getCreatedOn().plusSeconds(getArtificialOffsetInSeconds(technicalTask.getCreatedOn()) + 10).format(DATE_TIME_FORMATTER))).replaceFirst(".$", ""));
	}

	@ServiceActivator(inputChannel = "normalTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask normalTaskHandler(TechnicalTask technicalTask) {
		sendToJiraCreatedTaskInfo(technicalTask);
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s started working on normal technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / normalTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s finished working on normal technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "minorTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask minorTaskHandler(TechnicalTask technicalTask) {
		sendToJiraCreatedTaskInfo(technicalTask);
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s started working on minor technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / minorTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s finished working on minor technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "majorTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask majorTaskHandler(TechnicalTask technicalTask) {
		sendToJiraCreatedTaskInfo(technicalTask);
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s started working on major technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / majorTaskTypicalResolutionTimeCoefficient , TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s finished working on major technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "criticalTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask criticalTaskHandler(TechnicalTask technicalTask) {
		sendToJiraCreatedTaskInfo(technicalTask);
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s started working on critical technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / criticalTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s finished working on critical technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "blockerTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask blockerTaskHandler(TechnicalTask technicalTask) {
		sendToJiraCreatedTaskInfo(technicalTask);
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s started working on blocker technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		Uninterruptibles.sleepUninterruptibly(technicalTask.getAssignee().getExperienceCoefficient() / blockerTaskTypicalResolutionTimeCoefficient, TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("###Developer %s finished working on blocker technical task %s",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}
}
