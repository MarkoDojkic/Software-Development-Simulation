package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;

@EnableConfigurationProperties
@SpringBootApplication
public class SoftwareDevelopmentSimulationApp
{
	public static void main(String[] args)
	{
		Utilities.setIGateways(new SpringApplication(SoftwareDevelopmentSimulationApp.class).run(args).getBean(IGateways.class));

		Utilities.getIGateways().sendToInfo("""
					Welcome to Software development simulator™
					Developed by Ⓒ Marko Dojkić 2024$Enjoy using our web application""");

		setupDataProvider(false);
		updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());

		//TODO: Correct JIRA activity stream timings

		//GUI PLANS - thymeleaf

		//TODO: Update GUI menu to create custom epics, user stories and technical tasks
	}
}