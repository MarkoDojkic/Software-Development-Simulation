package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.util.IPrinter;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.developers;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

public class SoftwareDevelopmentSimulationApp {
	public static void main(String[] args) {
		AbstractApplicationContext abstractApplicationContext = new ClassPathXmlApplicationContext("/application.xml");

		ProjectOwner projectOwner = (ProjectOwner) abstractApplicationContext.getBean("projectOwner");
		IPrinter iPrinter = (IPrinter) abstractApplicationContext.getBean("IPrinter");

		iPrinter.sendToInfo("""
            Welcome to Software development simulator
            @Copyright(Marko Dojkić 2024)$Please wait patiently while software architect generates 15 epics!""");

		List<Epic> epicList = new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			epicList.add(Epic.builder()
				.name(lorem.getTitle(3, 6))
				.description(lorem.getParagraphs(5, 15))
				.priority(Priority.values()[random.nextInt(Priority.values().length)])
				.status(Status.TO_DO)
				.reporter(getRandomElementFromList(developers.stream().filter(dev -> dev.getUserType().equals(UserType.SENIOR_DEVELOPER)).collect(Collectors.toList())))
				.assignee(getRandomElementFromList(developers))
				.id(UUID.randomUUID().toString())
				.userStories(new ArrayList<>())
				.build()
			);
		}

		iPrinter.sendToError(epicList.stream().map(e -> "Generated epic: " + e.toString() + "$").collect(Collectors.joining()));

		iPrinter.sendToInfo("""
            All epics are created
            Let's now enjoy while our development time creating
            Please wait patiently while software architect generates 15 epics!""");

		//projectOwner.generateEpics(epicList);

		abstractApplicationContext.close();
	}
}
