package dev.markodojkic.softwaredevelopmentsimulation.service;

import org.springframework.integration.annotation.Gateway;

public class SoftwareArchitect {
	@Gateway(requestChannel = "epics", replyChannel = "console-output")
	public void generateProjects(){
		//generate projects and output information to console output channel
	}
}
