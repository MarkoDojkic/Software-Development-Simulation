package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.diogonunes.jcolor.Ansi.colorize;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@MessageEndpoint
public class DeveloperImpl {
	private static final Logger logger = Logger.getLogger(DeveloperImpl.class.getName());
	public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
	public static final String TEST = "test";

	@ServiceActivator(inputChannel = "trivialTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask trivialTaskHandler(TechnicalTask technicalTask) {
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		Uninterruptibles.sleepUninterruptibly(
				(long)
						(((technicalTask.getReporter().getExperienceCoefficient() + technicalTask.getAssignee().getExperienceCoefficient()) / 2.0)
								* ((technicalTask.getReporter().getDeveloperType().getSeniorityCoefficient() + technicalTask.getAssignee().getDeveloperType().getSeniorityCoefficient()) / 2.0)
								* technicalTask.getPriority().getResolutionTimeCoefficient()
								* (6 - technicalTask.getPriority().getUrgency()) / 5.0
								* 0.01
								+ 1500)
				, System.getProperty(SPRING_PROFILES_ACTIVE, "").equals(TEST) ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("%n</> Developer %s finished working on trivial technical task %s </>", technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "normalTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask normalTaskHandler(TechnicalTask technicalTask) {
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		Uninterruptibles.sleepUninterruptibly(
				(long)
						(((technicalTask.getReporter().getExperienceCoefficient() + technicalTask.getAssignee().getExperienceCoefficient()) / 2.0)
								* ((technicalTask.getReporter().getDeveloperType().getSeniorityCoefficient() + technicalTask.getAssignee().getDeveloperType().getSeniorityCoefficient()) / 2.0)
								* technicalTask.getPriority().getResolutionTimeCoefficient()
								* (6 - technicalTask.getPriority().getUrgency()) / 5.0
								* 0.02
								+ 1500)
				, System.getProperty(SPRING_PROFILES_ACTIVE, "").equals(TEST) ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("%n</> Developer %s finished working on normal technical task %s </>", technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "minorTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask minorTaskHandler(TechnicalTask technicalTask) {
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		Uninterruptibles.sleepUninterruptibly(
				(long)
						(((technicalTask.getReporter().getExperienceCoefficient() + technicalTask.getAssignee().getExperienceCoefficient()) / 2.0)
								* ((technicalTask.getReporter().getDeveloperType().getSeniorityCoefficient() + technicalTask.getAssignee().getDeveloperType().getSeniorityCoefficient()) / 2.0)
								* technicalTask.getPriority().getResolutionTimeCoefficient()
								* (6 - technicalTask.getPriority().getUrgency()) / 5.0
								* 0.03
								+ 1500)
				, System.getProperty(SPRING_PROFILES_ACTIVE, "").equals(TEST) ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("%n</> Developer %s finished working on minor technical task %s </>", technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "majorTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask majorTaskHandler(TechnicalTask technicalTask) {
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		Uninterruptibles.sleepUninterruptibly(
				(long)
						(((technicalTask.getReporter().getExperienceCoefficient() + technicalTask.getAssignee().getExperienceCoefficient()) / 2.0)
								* ((technicalTask.getReporter().getDeveloperType().getSeniorityCoefficient() + technicalTask.getAssignee().getDeveloperType().getSeniorityCoefficient()) / 2.0)
								* technicalTask.getPriority().getResolutionTimeCoefficient()
								* (6 - technicalTask.getPriority().getUrgency()) / 5.0
								* 0.04
								+ 1500)
				, System.getProperty(SPRING_PROFILES_ACTIVE, "").equals(TEST) ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("%n</> Developer %s finished working on major technical task %s </>", technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "criticalTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask criticalTaskHandler(TechnicalTask technicalTask) {
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		Uninterruptibles.sleepUninterruptibly(
				(long)
						(((technicalTask.getReporter().getExperienceCoefficient() + technicalTask.getAssignee().getExperienceCoefficient()) / 2.0)
								* ((technicalTask.getReporter().getDeveloperType().getSeniorityCoefficient() + technicalTask.getAssignee().getDeveloperType().getSeniorityCoefficient()) / 2.0)
								* technicalTask.getPriority().getResolutionTimeCoefficient()
								* (6 - technicalTask.getPriority().getUrgency()) / 5.0
								* 0.05
								+ 1500)
				, System.getProperty(SPRING_PROFILES_ACTIVE, "").equals(TEST) ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("%n</> Developer %s finished working on critical technical task %s </>", technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}

	@ServiceActivator(inputChannel = "blockerTechnicalTask.intermediate", outputChannel = "doneTechnicalTasks.output")
	public TechnicalTask blockerTaskHandler(TechnicalTask technicalTask) {
		sendToJiraSetStatusToInProgressInfo(technicalTask);
		Uninterruptibles.sleepUninterruptibly(
				(long)
						(((technicalTask.getReporter().getExperienceCoefficient() + technicalTask.getAssignee().getExperienceCoefficient()) / 2.0)
								* ((technicalTask.getReporter().getDeveloperType().getSeniorityCoefficient() + technicalTask.getAssignee().getDeveloperType().getSeniorityCoefficient()) / 2.0)
								* technicalTask.getPriority().getResolutionTimeCoefficient()
								* (6 - technicalTask.getPriority().getUrgency()) / 5.0
								* 0.06
								+ 1500)
				, System.getProperty(SPRING_PROFILES_ACTIVE, "").equals(TEST) ? TimeUnit.NANOSECONDS : TimeUnit.MILLISECONDS);
		logger.log(Level.INFO, () -> colorize(String.format("%n</> Developer %s finished working on blocker technical task %s </>", technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0), Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		return technicalTask;
	}


	private void sendToJiraSetStatusToInProgressInfo(TechnicalTask technicalTask) {
		getIGateways().sendToJiraActivityStream(String.format("\033[1m%s\033[21m\033[24m changed the status to In progress on TASK: \033[3m\033[1m%s\033[21m\033[24m - %s\033[23m ◴ %s$",
				technicalTask.getAssignee().getDisplayName(), technicalTask.getId(), technicalTask.getName(), ZonedDateTime.now().format(DATE_TIME_FORMATTER)).replaceFirst(".$", ""));
	}
}