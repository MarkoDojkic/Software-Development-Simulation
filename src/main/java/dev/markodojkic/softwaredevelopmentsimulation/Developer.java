package dev.markodojkic.softwaredevelopmentsimulation;

import com.diogonunes.jcolor.Attribute;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import lombok.Setter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Splitter;
import org.springframework.stereotype.Component;

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

	@ServiceActivator(inputChannel = "trivialTechnicalTask", outputChannel = "doneTechnicalTask")
	public TechnicalTask trivialTaskHandler(TechnicalTask technicalTask){
		try {
			System.out.println(colorize(String.format("###Developer %s started working on trivial technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			Thread.sleep(technicalTask.getAssignee().getExperienceCoefficient()
					/ Long.parseLong(trivialTaskTypicalResolutionTimeCoefficient));
			System.out.println(colorize(String.format("###Developer %s finished working on technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return technicalTask;
	}

	@ServiceActivator(inputChannel = "normalTechnicalTask", outputChannel = "doneTechnicalTask")
	public TechnicalTask normalTaskHandler(TechnicalTask technicalTask){
		try {
			System.out.println(colorize(String.format("###Developer %s started working on normal technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			Thread.sleep(technicalTask.getAssignee().getExperienceCoefficient()
					/ Long.parseLong(normalTaskTypicalResolutionTimeCoefficient));
			System.out.println(colorize(String.format("###Developer %s finished working on normal technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			return technicalTask;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	@ServiceActivator(inputChannel = "minorTechnicalTask", outputChannel = "doneTechnicalTask")
	public TechnicalTask minorTaskHandler(TechnicalTask technicalTask){
		try {
			System.out.println(colorize(String.format("###Developer %s started working on minor technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			Thread.sleep(technicalTask.getAssignee().getExperienceCoefficient()
					/ Long.parseLong(minorTaskTypicalResolutionTimeCoefficient));
			System.out.println(colorize(String.format("###Developer %s finished working on minor technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			return technicalTask;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	@ServiceActivator(inputChannel = "majorTechnicalTask", outputChannel = "doneTechnicalTask")
	public TechnicalTask majorTaskHandler(TechnicalTask technicalTask){
		try {
			System.out.println(colorize(String.format("###Developer %s started working on major technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			Thread.sleep(technicalTask.getAssignee().getExperienceCoefficient()
					/ Long.parseLong(majorTaskTypicalResolutionTimeCoefficient));
			System.out.println(colorize(String.format("###Developer %s finished working on major technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			return technicalTask;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	@ServiceActivator(inputChannel = "criticalTechnicalTask", outputChannel = "doneTechnicalTask")
	public TechnicalTask criticalTaskHandler(TechnicalTask technicalTask){
		try {
			System.out.println(colorize(String.format("###Developer %s started working on critical technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			Thread.sleep(technicalTask.getAssignee().getExperienceCoefficient()
					/ Long.parseLong(criticalTaskTypicalResolutionTimeCoefficient));
			System.out.println(colorize(String.format("###Developer %s finished working on critical technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			return technicalTask;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	@ServiceActivator(inputChannel = "blockerTechnicalTask", outputChannel = "doneTechnicalTask")
	public TechnicalTask blockerTaskHandler(TechnicalTask technicalTask){
		try {
			System.out.println(colorize(String.format("###Developer %s started working on blocker technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			Thread.sleep(technicalTask.getAssignee().getExperienceCoefficient()
					/ Long.parseLong(blockerTaskTypicalResolutionTimeCoefficient));
			System.out.println(colorize(String.format("###Developer %s finished working on blocker technical task %s",
					technicalTask.getAssignee().getDisplayName(), technicalTask.getId()), Attribute.TEXT_COLOR(0),Attribute.BACK_COLOR(technicalTask.getPriority().getAnsiColorCode())));
			return technicalTask;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
}
