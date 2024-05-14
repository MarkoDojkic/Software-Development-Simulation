package dev.markodojkic.softwaredevelopmentsimulation.service;

import org.springframework.integration.annotation.Gateway;

public class ProjectManager {
	@Gateway(requestChannel = "user-stories", replyChannel = "console-output")
	public void generateTechnicalTasks(){
		//generate projects and output information to console output channel
	}
}
