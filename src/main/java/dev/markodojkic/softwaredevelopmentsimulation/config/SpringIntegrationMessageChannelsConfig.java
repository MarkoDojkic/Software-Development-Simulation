package dev.markodojkic.softwaredevelopmentsimulation.config;

import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.logging.Level;
import java.util.logging.Logger;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@Configuration
@SuppressWarnings("unchecked")
public class SpringIntegrationMessageChannelsConfig {
	private static final Logger logger = Logger.getLogger(SpringIntegrationMessageChannelsConfig.class.getName());

	@Bean(name = "errorChannel")
	public MessageChannel errorChannel() {
		return new DirectChannel();
	}

	@Bean(name = "errorChannel.mqtt.input")
	public MessageChannel errorChannelMQTTInput() {
		return new DirectChannel();
	}

	@Bean(name = "errorChannel.logFile.input")
	public MessageChannel errorChannelLogFileInput() {
		return new DirectChannel();
	}

	@Bean(name = "information.input")
	public MessageChannel informationInput() {
		return new DirectChannel();
	}

	@Bean(name = "information.mqtt.input")
	public MessageChannel informationMQTTInput() {
		return new DirectChannel();
	}

	@Bean(name = "information.logFile.input")
	public MessageChannel informationLogFileInput() {
		return new DirectChannel();
	}

	@Bean(name = "jiraActivityStream.input")
	public MessageChannel jiraActivityStreamInput() {
		return new DirectChannel();
	}

	@Bean(name = "jiraActivityStream.mqtt.input")
	public MessageChannel jiraActivityStreamMQTTInput() {
		return new DirectChannel();
	}

	@Bean(name = "jiraActivityStream.logFile.input")
	public MessageChannel jiraActivityStreamLogFileInput() {
		return new DirectChannel();
	}

	@Bean(name = "epicMessage.input")
	public MessageChannel epicInput(){
		PriorityChannel epicInputPriorityChannel = new PriorityChannel(1000, (message1, message2) -> {
            Message<BaseTask> baseTaskMessage1 = (Message<BaseTask>) message1; //Unchecked cast is acceptable
            Message<BaseTask> baseTaskMessage2 = (Message<BaseTask>) message2;
			return (getAvailableDevelopmentTeamIds().size() == 1 && !getCurrentDevelopmentTeamsSetup().get(getAvailableDevelopmentTeamIds().peekFirst()).contains(baseTaskMessage1.getPayload().getAssignee())) ? 0 : Integer.compare(baseTaskMessage2.getPayload().getPriority().getUrgency(), baseTaskMessage1.getPayload().getPriority().getUrgency());
			/* Here we compare epics by its urgency, but leave order if task is from currently busy development team so that others can be processed first (again they are already compared in previous iterations)
				> Example input: URBANITAS-11021256, OMITTANTUR-9682007, ELIT-12429313, POSSE-4356246, DOLORUM-11966156, JUSTO-11562991
				< Expected and actual output:
					DOLORUM-11966156 (Dev team 1, Comparator value - urgency 4)
					ELIT-12429313 (Dev team 0, Comparator value - urgency 3)
					POSSE-4356246 (Dev team 1, Comparator value - urgency 3)
					OMITTANTUR-9682007 (Dev team 0, Comparator value - urgency 2)
					URBANITAS-11021256 (Dev team 1, Comparator value - urgency 2)
					JUSTO-11562991 (Dev team 0, Comparator value 1)
				Note: Dev team order shows alternating pattern here is by coincidence
			 */
        });
		epicInputPriorityChannel.setDatatypes(Epic.class);

		return epicInputPriorityChannel;
	}

	@Bean(name = "assignEpicFlow")
	public IntegrationFlow assignEpicFlow(){
		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setMaxMessagesPerPoll(1);

		return IntegrationFlow.from(epicInput()).handle(message -> {
			logger.log(Level.FINE, "Processing epic with id {0}", ((Epic) message.getPayload()).getId());
			if(getAvailableDevelopmentTeamIds().isEmpty()) {
				logger.log(Level.FINE, "Stopping assignEpicFlow due to none of development teams being available");
				epicInput().send(message); //Resend message before stopping since it can`t be processed
				controlBusInput().send(MessageBuilder.withPayload("@assignEpicFlow.stop()").build());
			} else {
				new Thread(() -> currentSprintEpic().send(MessageBuilder.withPayload(message.getPayload()).setHeader(ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER, getAvailableDevelopmentTeamIds().pollFirst()).build())).start();
			}
		}, sourcePoolingChannelAdapter -> sourcePoolingChannelAdapter.poller(pollerMetadata)).get();
	}

	@Bean(name = "controlBus.input")
	public MessageChannel controlBusInput() {
		return new DirectChannel();
	}

	@Bean(name = "currentSprintEpic.input")
	public MessageChannel currentSprintEpic(){
		DirectChannel currentSprintEpicChannel = new DirectChannel();
		currentSprintEpicChannel.setDatatypes(Epic.class);
		return currentSprintEpicChannel;
	}

	@Bean(name = "inProgressEpic.intermediate")
	public MessageChannel inProgressEpic(){
		DirectChannel inProgressEpicChannel = new DirectChannel();
		inProgressEpicChannel.setDatatypes(Epic.class);
		return inProgressEpicChannel;
	}

	@Bean(name = "doneEpics.output")
	public MessageChannel doneEpics(){
		DirectChannel doneEpicsChannel = new DirectChannel();
		doneEpicsChannel.setDatatypes(Epic.class);
		return doneEpicsChannel;
	}

	@Bean(name = "inProgressUserStory.intermediate")
	public MessageChannel inProgressUserStory(){
		DirectChannel inProgressUserStoryChannel = new DirectChannel();
		inProgressUserStoryChannel.setDatatypes(UserStory.class);
		return inProgressUserStoryChannel;
	}

	@Bean(name = "currentSprintUserStories.preIntermediate")
	public MessageChannel currentSprintUserStories(){
		DirectChannel currentSprintUserStoriesChannel = new DirectChannel();
		currentSprintUserStoriesChannel.setDatatypes(UserStory.class);
		return currentSprintUserStoriesChannel;
	}

	@Bean(name = "doneSprintUserStories.output")
	public MessageChannel doneSprintUserStories(){
		DirectChannel doneSprintUserStoriesChannel = new DirectChannel();
		doneSprintUserStoriesChannel.setDatatypes(UserStory.class);
		return doneSprintUserStoriesChannel;
	}

	@Bean(name = "toDoTechnicalTasks.input")
	public MessageChannel toDoTechnicalTasks(){
		DirectChannel toDoTechnicalTasksChannel = new DirectChannel();
		toDoTechnicalTasksChannel.setDatatypes(TechnicalTask.class);
		return toDoTechnicalTasksChannel;
	}

	@Bean(name = "trivialTechnicalTaskQueue.input")
	public MessageChannel trivialTechnicalTaskQueue(){
		QueueChannel trivialTechnicalTaskQueueChannel = new QueueChannel(8);
		trivialTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return trivialTechnicalTaskQueueChannel;
	}

	@Bean(name = "trivialTechnicalTask.intermediate")
	@BridgeFrom(value = "trivialTechnicalTaskQueue.input", poller = @Poller(fixedRate = "1800"))
	public MessageChannel trivialTechnicalTask(){
		DirectChannel trivialTechnicalTaskChannel = new DirectChannel();
		trivialTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return trivialTechnicalTaskChannel;
	}

	@Bean(name = "normalTechnicalTaskQueue.input")
	public MessageChannel normalTechnicalTaskQueue(){
		QueueChannel normalTechnicalTaskQueueChannel = new QueueChannel(6);
		normalTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return normalTechnicalTaskQueueChannel;
	}

	@Bean(name = "normalTechnicalTask.intermediate")
	@BridgeFrom(value = "normalTechnicalTaskQueue.input", poller = @Poller(fixedRate = "1600"))
	public MessageChannel normalTechnicalTask(){
		DirectChannel normalTechnicalTaskChannel = new DirectChannel();
		normalTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return normalTechnicalTaskChannel;
	}

	@Bean(name = "minorTechnicalTaskQueue.input")
	public MessageChannel minorTechnicalTaskQueue(){
		QueueChannel minorTechnicalTaskQueueChannel = new QueueChannel(4);
		minorTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return minorTechnicalTaskQueueChannel;
	}

	@Bean(name = "minorTechnicalTask.intermediate")
	@BridgeFrom(value = "minorTechnicalTaskQueue.input", poller = @Poller(fixedRate = "1400"))
	public MessageChannel minorTechnicalTask(){
		DirectChannel minorTechnicalTaskChannel = new DirectChannel();
		minorTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return minorTechnicalTaskChannel;
	}

	@Bean(name = "majorTechnicalTaskQueue.input")
	public MessageChannel majorTechnicalTaskQueue(){
		QueueChannel majorTechnicalTaskQueueChannel = new QueueChannel(2);
		majorTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return majorTechnicalTaskQueueChannel;
	}

	@Bean(name = "majorTechnicalTask.intermediate")
	@BridgeFrom(value = "majorTechnicalTaskQueue.input", poller = @Poller(fixedRate = "1200"))
	public MessageChannel majorTechnicalTask(){
		DirectChannel majorTechnicalTaskChannel = new DirectChannel();
		majorTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return majorTechnicalTaskChannel;
	}

	@Bean(name = "criticalTechnicalTaskQueue.input")
	public MessageChannel criticalTechnicalTaskQueue(){
		QueueChannel criticalTechnicalTaskQueueChannel = new QueueChannel(1);
		criticalTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return criticalTechnicalTaskQueueChannel;
	}

	@Bean(name = "criticalTechnicalTask.intermediate")
	@BridgeFrom(value = "criticalTechnicalTaskQueue.input", poller = @Poller(fixedRate = "1100"))
	public MessageChannel criticalTechnicalTask(){
		DirectChannel criticalTechnicalTaskChannel = new DirectChannel();
		criticalTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return criticalTechnicalTaskChannel;
	}

	@Bean(name = "blockerTechnicalTaskQueue.input")
	public MessageChannel blockerTechnicalTaskQueue(){
		QueueChannel blockerTechnicalTaskQueueChannel = new QueueChannel(1);
		blockerTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return blockerTechnicalTaskQueueChannel;
	}

	@Bean(name = "blockerTechnicalTask.intermediate")
	@BridgeFrom(value = "blockerTechnicalTaskQueue.input", poller = @Poller(fixedRate = "1100"))
	public MessageChannel blockerTechnicalTask(){
		DirectChannel blockerTechnicalTaskChannel = new DirectChannel();
		blockerTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return blockerTechnicalTaskChannel;
	}

	@Bean(name = "doneTechnicalTasks.output")
	public MessageChannel doneTechnicalTasks(){
		DirectChannel doneTechnicalTasksChannel = new DirectChannel();
		doneTechnicalTasksChannel.setDatatypes(TechnicalTask.class);
		return doneTechnicalTasksChannel;
	}
}