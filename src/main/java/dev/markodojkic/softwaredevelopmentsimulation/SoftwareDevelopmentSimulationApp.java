package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableConfigurationProperties
@ComponentScan(basePackages = "dev.markodojkic.softwaredevelopmentsimulation")
public class SoftwareDevelopmentSimulationApp
{
	public static void main(String[] args)
	{
		Utilities.setIGateways(new SpringApplication(SoftwareDevelopmentSimulationApp.class).run(args).getBean(IGateways.class));

		Utilities.generateRandomData(2,7);

		//TODO: Consider using priority queue to simulate epics that cannot be assigned at the moment due to overwork of all development teams
		//TODO: Correct JIRA activity stream timings

		//GUI PLANS - thymeleaf

		//TODO: Create GUI viewer for info, error and jira activity stream channels
		//TODO: Create GUI menu to generate n epics with configurable user stories and technical tasks count generation limits
		//TODO: Create GUI menu to generate n developers (with optional possibility to add more at runtime)
	}
}