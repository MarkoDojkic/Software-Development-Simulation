package dev.markodojkic.softwaredevelopmentsimulation;

import dev.markodojkic.softwaredevelopmentsimulation.service.SoftwareArchitect;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SoftwareDevelopmentSimulationApp {
	public static void main(String[] args) {
		AbstractApplicationContext abstractApplicationContext = new ClassPathXmlApplicationContext("/softwareDevelopmentSimulation.xml");

		SoftwareArchitect projectArchitect = (SoftwareArchitect) abstractApplicationContext.getBean("software-architect");

		projectArchitect.generateProjects();

		abstractApplicationContext.close();
	}
}
