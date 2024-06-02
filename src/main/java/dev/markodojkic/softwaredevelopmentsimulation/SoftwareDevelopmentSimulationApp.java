package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.iGateways;

@EnableConfigurationProperties
@SpringBootApplication
public class SoftwareDevelopmentSimulationApp
{
	public static void main(String[] args)
	{
		Utilities.setIGateways(new SpringApplication(SoftwareDevelopmentSimulationApp.class).run(args).getBean(IGateways.class));

		iGateways.sendToInfo("""
					Welcome to Software development simulator™
					Developed by Ⓒ Marko Dojkić 2024$Enjoy using our web application""");

		updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());
		//generateRandomTasks(3,16);

		//TODO: Correct JIRA activity stream timings

		//GUI PLANS - thymeleaf

		//TODO: Create GUI viewer for info, error and jira activity stream channels
		//TODO: Create GUI menu to generate n epics with configurable user stories and technical tasks count generation limits
		//TODO: Create GUI menu to generate n developers (with optional possibility to add more at runtime)
	}
}