package dev.markodojkic.softwaredevelopmentsimulation.config;

import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import org.aopalliance.aop.Advice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Configuration
@EnableIntegration
@IntegrationComponentScan(basePackages = "dev.markodojkic.softwaredevelopmentsimulation")
public class Config {
	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {
		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setTrigger(new PeriodicTrigger(Duration.of(1000, ChronoUnit.MILLIS)));
		return pollerMetadata;
	}

	@Bean(name = "retryAdvice")
	Advice retryAdvice(){
		return RetryInterceptorBuilder.stateless().maxAttempts(100).backOffPolicy(new FixedBackOffPolicy()).build();
	}

	@Bean(name = "information.input")
	MessageChannel informationInput(){
		return new DirectChannel();
	}

	@Bean(name = "epicsMessage.input")
	MessageChannel epicInput(){
		PriorityChannel epicInputPriorityChannel = new PriorityChannel(0, new Comparator<Message<?>>() {
			@Override
			public int compare(Message<?> message1, Message<?> message2) {
				Message<BaseTask> baseTaskMessage1 = (Message<BaseTask>) message1;
				Message<BaseTask> baseTaskMessage2 = (Message<BaseTask>) message2;
				return Integer.compare(baseTaskMessage1.getPayload().getPriority().getUrgency(), baseTaskMessage2.getPayload().getPriority().getUrgency());
			}
		});
		epicInputPriorityChannel.setDatatypes(Epic.class);

		return epicInputPriorityChannel;
	}

	@Bean(name = "currentSprintEpic.input")
	MessageChannel currentSprintEpic(){
		DirectChannel currentSprintEpicChannel = new DirectChannel();;
		currentSprintEpicChannel.setDatatypes(Epic.class);
		return currentSprintEpicChannel;
	}

	@Bean(name = "inProgressEpic.intermediate")
	MessageChannel inProgressEpic(){
		DirectChannel inProgressEpicChannel = new DirectChannel();;
		inProgressEpicChannel.setDatatypes(Epic.class);
		return inProgressEpicChannel;
	}

	@Bean(name = "doneEpics.output")
	MessageChannel doneEpics(){
		DirectChannel doneEpicsChannel = new DirectChannel();;
		doneEpicsChannel.setDatatypes(Epic.class);
		return doneEpicsChannel;
	}

	@Bean(name = "inProgressUserStory.input")
	MessageChannel inProgressUserStory(){
		DirectChannel inProgressUserStoryChannel = new DirectChannel();;
		inProgressUserStoryChannel.setDatatypes(UserStory.class);
		return inProgressUserStoryChannel;
	}

	@Bean(name = "currentSprintUserStories.intermediate")
	MessageChannel currentSprintUserStories(){
		DirectChannel currentSprintUserStoriesChannel = new DirectChannel();;
		currentSprintUserStoriesChannel.setDatatypes(UserStory.class);
		return currentSprintUserStoriesChannel;
	}

	@Bean(name = "doneSprintUserStories.output")
	MessageChannel doneSprintUserStories(){
		DirectChannel doneSprintUserStoriesChannel = new DirectChannel();;
		doneSprintUserStoriesChannel.setDatatypes(UserStory.class);
		return doneSprintUserStoriesChannel;
	}

	@Bean(name = "toDoTechnicalTasks.input")
	MessageChannel toDoTechnicalTasks(){
		DirectChannel toDoTechnicalTasksChannel = new DirectChannel();;
		toDoTechnicalTasksChannel.setDatatypes(TechnicalTask.class);
		return toDoTechnicalTasksChannel;
	}

	@Bean(name = "trivialTechnicalTaskQueue.input")
	MessageChannel trivialTechnicalTaskQueue(){
		QueueChannel trivialTechnicalTaskQueueChannel = new QueueChannel(8);
		trivialTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return trivialTechnicalTaskQueueChannel;
	}

	@Bean(name = "trivialTechnicalTask.intermediate")
	@BridgeFrom(value = "trivialTechnicalTaskQueue.input", poller = @Poller(fixedRate = "800"))
	MessageChannel trivialTechnicalTask(){
		DirectChannel trivialTechnicalTaskChannel = new DirectChannel();
		trivialTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return trivialTechnicalTaskChannel;
	}

	@Bean(name = "normalTechnicalTaskQueue.input")
	MessageChannel normalTechnicalTaskQueue(){
		QueueChannel normalTechnicalTaskQueueChannel = new QueueChannel(6);
		normalTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return normalTechnicalTaskQueueChannel;
	}

	@Bean(name = "normalTechnicalTask.intermediate")
	@BridgeFrom(value = "normalTechnicalTaskQueue.input", poller = @Poller(fixedRate = "600"))
	MessageChannel normalTechnicalTask(){
		DirectChannel normalTechnicalTaskChannel = new DirectChannel();
		normalTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return normalTechnicalTaskChannel;
	}

	@Bean(name = "minorTechnicalTaskQueue.input")
	MessageChannel minorTechnicalTaskQueue(){
		QueueChannel minorTechnicalTaskQueueChannel = new QueueChannel(4);
		minorTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return minorTechnicalTaskQueueChannel;
	}

	@Bean(name = "minorTechnicalTask.intermediate")
	@BridgeFrom(value = "minorTechnicalTaskQueue.input", poller = @Poller(fixedRate = "400"))
	MessageChannel minorTechnicalTask(){
		DirectChannel minorTechnicalTaskChannel = new DirectChannel();
		minorTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return minorTechnicalTaskChannel;
	}

	@Bean(name = "majorTechnicalTaskQueue.input")
	MessageChannel majorTechnicalTaskQueue(){
		QueueChannel majorTechnicalTaskQueueChannel = new QueueChannel(2);
		majorTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return majorTechnicalTaskQueueChannel;
	}

	@Bean(name = "majorTechnicalTask.intermediate")
	@BridgeFrom(value = "majorTechnicalTaskQueue.input", poller = @Poller(fixedRate = "200"))
	MessageChannel majorTechnicalTask(){
		DirectChannel majorTechnicalTaskChannel = new DirectChannel();
		majorTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return majorTechnicalTaskChannel;
	}

	@Bean(name = "criticalTechnicalTaskQueue.input")
	MessageChannel criticalTechnicalTaskQueue(){
		QueueChannel criticalTechnicalTaskQueueChannel = new QueueChannel(1);
		criticalTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return criticalTechnicalTaskQueueChannel;
	}

	@Bean(name = "criticalTechnicalTask.intermediate")
	@BridgeFrom(value = "criticalTechnicalTaskQueue.input", poller = @Poller(fixedRate = "100"))
	MessageChannel criticalTechnicalTask(){
		DirectChannel criticalTechnicalTaskChannel = new DirectChannel();
		criticalTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return criticalTechnicalTaskChannel;
	}

	@Bean(name = "blockerTechnicalTaskQueue.input")
	MessageChannel blockerTechnicalTaskQueue(){
		QueueChannel blockerTechnicalTaskQueueChannel = new QueueChannel(1);
		blockerTechnicalTaskQueueChannel.setDatatypes(TechnicalTask.class);
		return blockerTechnicalTaskQueueChannel;
	}

	@Bean(name = "blockerTechnicalTask.intermediate")
	@BridgeFrom(value = "blockerTechnicalTaskQueue.input", poller = @Poller(fixedRate = "100"))
	MessageChannel blockerTechnicalTask(){
		DirectChannel blockerTechnicalTaskChannel = new DirectChannel();
		blockerTechnicalTaskChannel.setDatatypes(TechnicalTask.class);
		return blockerTechnicalTaskChannel;
	}

	@Bean(name = "doneTechnicalTasks.output")
	MessageChannel doneTechnicalTasks(){
		DirectChannel doneTechnicalTasksChannel = new DirectChannel();;
		doneTechnicalTasksChannel.setDatatypes(TechnicalTask.class);
		return doneTechnicalTasksChannel;
	}
}
