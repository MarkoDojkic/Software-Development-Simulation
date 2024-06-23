package dev.markodojkic.softwaredevelopmentsimulation.config;

import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
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

import java.util.logging.Level;
import java.util.logging.Logger;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.IN_PROGRESS_EPICS_COUNT;

@Configuration
@SuppressWarnings("unchecked")
public class SpringIntegrationMessageChannelsConfig {
	private static final Logger logger = Logger.getLogger(SpringIntegrationMessageChannelsConfig.class.getName());

	@Bean(name = "errorChannel")
	public DirectChannel errorChannel() {
		return new DirectChannel();
	}

	@Bean(name = "information.input")
	public DirectChannel informationInput() {
		return new DirectChannel();
	}

	@Bean(name = "jiraActivityStream.input")
	public DirectChannel jiraActivityStreamInput() {
		return new DirectChannel();
	}

	@Bean(name = "information.mqtt.input")
	public DirectChannel informationMQTTInput() {
		return new DirectChannel();
	}

	@Bean(name = "error.mqtt.input")
	public DirectChannel errorMQTTInput() {
		return new DirectChannel();
	}

	@Bean(name = "jiraActivityStream.mqtt.input")
	public DirectChannel jiraActivityStreamMQTTInput() {
		return new DirectChannel();
	}

	@Bean(name = "epicMessage.input")
	public PriorityChannel epicInput(){
		PriorityChannel epicInputPriorityChannel = new PriorityChannel(0, (message1, message2) -> {
            Message<BaseTask> baseTaskMessage1 = (Message<BaseTask>) message1;
            Message<BaseTask> baseTaskMessage2 = (Message<BaseTask>) message2;
            return Integer.compare(baseTaskMessage2.getPayload().getPriority().getUrgency(), baseTaskMessage1.getPayload().getPriority().getUrgency());
        });
		epicInputPriorityChannel.setDatatypes(Epic.class);

		return epicInputPriorityChannel;
	}

	@Bean(name = "assignEpicFlow")
	public IntegrationFlow assignEpicFlow(){
		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setMaxMessagesPerPoll(1);
		return IntegrationFlow.from("epicMessage.input").handle(message -> {
			logger.log(Level.INFO, "{0} arrived - Current count: {1}", new String[]{((Epic) message.getPayload()).getId(), String.valueOf(IN_PROGRESS_EPICS_COUNT.incrementAndGet())});
			new Thread(() -> currentSprintEpic().send(MessageBuilder.withPayload(message.getPayload()).setHeader(ASSIGNED_DEVELOPMENT_TEAM_POSITION_NUMBER, DataProvider.getAvailableDevelopmentTeamIds().pop()).build())).start();
			if(IN_PROGRESS_EPICS_COUNT.get() == Utilities.getTotalDevelopmentTeamsPresent()) controlBusInput().send(MessageBuilder.withPayload("@assignEpicFlow.stop()").build());
		}, sourcePoolingChannelAdapter -> sourcePoolingChannelAdapter.poller(pollerMetadata)).get();
	}

	@Bean(name = "controlBus.input")
	public DirectChannel controlBusInput() {
		return new DirectChannel();
	}

	@Bean(name = "currentSprintEpic.input")
	public DirectChannel currentSprintEpic(){
		DirectChannel currentSprintEpicChannel = new DirectChannel();
		currentSprintEpicChannel.setDatatypes(Epic.class);
		return currentSprintEpicChannel;
	}

	@Bean(name = "inProgressEpic.intermediate")
	public DirectChannel inProgressEpic(){
		DirectChannel inProgressEpicChannel = new DirectChannel();
		inProgressEpicChannel.setDatatypes(Epic.class);
		return inProgressEpicChannel;
	}

	@Bean(name = "doneEpics.output")
	public DirectChannel doneEpics(){
		DirectChannel doneEpicsChannel = new DirectChannel();
		doneEpicsChannel.setDatatypes(Epic.class);
		return doneEpicsChannel;
	}

	@Bean(name = "inProgressUserStory.intermediate")
	public DirectChannel inProgressUserStory(){
		DirectChannel inProgressUserStoryChannel = new DirectChannel();
		inProgressUserStoryChannel.setDatatypes(UserStory.class);
		return inProgressUserStoryChannel;
	}

	@Bean(name = "currentSprintUserStories.preIntermediate")
	public DirectChannel currentSprintUserStories(){
		DirectChannel currentSprintUserStoriesChannel = new DirectChannel();
		currentSprintUserStoriesChannel.setDatatypes(UserStory.class);
		return currentSprintUserStoriesChannel;
	}

	@Bean(name = "doneSprintUserStories.output")
	public DirectChannel doneSprintUserStories(){
		DirectChannel doneSprintUserStoriesChannel = new DirectChannel();
		doneSprintUserStoriesChannel.setDatatypes(UserStory.class);
		return doneSprintUserStoriesChannel;
	}

	@Bean(name = "toDoTechnicalTasks.input")
	public DirectChannel toDoTechnicalTasks(){
		DirectChannel toDoTechnicalTasksChannel = new DirectChannel();
		toDoTechnicalTasksChannel.setDatatypes(TechnicalTask.class);
		return toDoTechnicalTasksChannel;
	}

	@Bean(name = "trivialTechnicalTaskQueue.input")
	public QueueChannel trivialTechnicalTaskQueue(){
		QueueChannel trivialTechnicalTaskQueueChannel = new QueueChannel(8);
		trivialTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return trivialTechnicalTaskQueueChannel;
	}

	@Bean(name = "trivialTechnicalTask.intermediate")
	@BridgeFrom(value = "trivialTechnicalTaskQueue.input", poller = @Poller(fixedRate = "800"))
	public DirectChannel trivialTechnicalTask(){
		DirectChannel trivialTechnicalTaskChannel = new DirectChannel();
		trivialTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return trivialTechnicalTaskChannel;
	}

	@Bean(name = "normalTechnicalTaskQueue.input")
	public QueueChannel normalTechnicalTaskQueue(){
		QueueChannel normalTechnicalTaskQueueChannel = new QueueChannel(6);
		normalTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return normalTechnicalTaskQueueChannel;
	}

	@Bean(name = "normalTechnicalTask.intermediate")
	@BridgeFrom(value = "normalTechnicalTaskQueue.input", poller = @Poller(fixedRate = "600"))
	public DirectChannel normalTechnicalTask(){
		DirectChannel normalTechnicalTaskChannel = new DirectChannel();
		normalTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return normalTechnicalTaskChannel;
	}

	@Bean(name = "minorTechnicalTaskQueue.input")
	public QueueChannel minorTechnicalTaskQueue(){
		QueueChannel minorTechnicalTaskQueueChannel = new QueueChannel(4);
		minorTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return minorTechnicalTaskQueueChannel;
	}

	@Bean(name = "minorTechnicalTask.intermediate")
	@BridgeFrom(value = "minorTechnicalTaskQueue.input", poller = @Poller(fixedRate = "400"))
	public DirectChannel minorTechnicalTask(){
		DirectChannel minorTechnicalTaskChannel = new DirectChannel();
		minorTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return minorTechnicalTaskChannel;
	}

	@Bean(name = "majorTechnicalTaskQueue.input")
	public QueueChannel majorTechnicalTaskQueue(){
		QueueChannel majorTechnicalTaskQueueChannel = new QueueChannel(2);
		majorTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return majorTechnicalTaskQueueChannel;
	}

	@Bean(name = "majorTechnicalTask.intermediate")
	@BridgeFrom(value = "majorTechnicalTaskQueue.input", poller = @Poller(fixedRate = "200"))
	public DirectChannel majorTechnicalTask(){
		DirectChannel majorTechnicalTaskChannel = new DirectChannel();
		majorTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return majorTechnicalTaskChannel;
	}

	@Bean(name = "criticalTechnicalTaskQueue.input")
	public QueueChannel criticalTechnicalTaskQueue(){
		QueueChannel criticalTechnicalTaskQueueChannel = new QueueChannel(1);
		criticalTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return criticalTechnicalTaskQueueChannel;
	}

	@Bean(name = "criticalTechnicalTask.intermediate")
	@BridgeFrom(value = "criticalTechnicalTaskQueue.input", poller = @Poller(fixedRate = "100"))
	public DirectChannel criticalTechnicalTask(){
		DirectChannel criticalTechnicalTaskChannel = new DirectChannel();
		criticalTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return criticalTechnicalTaskChannel;
	}

	@Bean(name = "blockerTechnicalTaskQueue.input")
	public QueueChannel blockerTechnicalTaskQueue(){
		QueueChannel blockerTechnicalTaskQueueChannel = new QueueChannel(1);
		blockerTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return blockerTechnicalTaskQueueChannel;
	}

	@Bean(name = "blockerTechnicalTask.intermediate")
	@BridgeFrom(value = "blockerTechnicalTaskQueue.input", poller = @Poller(fixedRate = "100"))
	public DirectChannel blockerTechnicalTask(){
		DirectChannel blockerTechnicalTaskChannel = new DirectChannel();
		blockerTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return blockerTechnicalTaskChannel;
	}

	@Bean(name = "doneTechnicalTasks.output")
	public DirectChannel doneTechnicalTasks(){
		DirectChannel doneTechnicalTasksChannel = new DirectChannel();
		doneTechnicalTasksChannel.setDatatypes(TechnicalTask.class);
		return doneTechnicalTasksChannel;
	}
}
