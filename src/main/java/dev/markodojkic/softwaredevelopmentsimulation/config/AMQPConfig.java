package dev.markodojkic.softwaredevelopmentsimulation.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfig {
	@Bean
	public TopicExchange topicExchange(){
		return new TopicExchange("amq.topic");
	}

	@Bean
	public Queue informationPrintoutQueue() {
		return new Queue("information-printout-queue");
	}

	@Bean
	public Queue jiraActivityStreamPrintoutQueue() {
		return new Queue("java-activity-stream-printout-queue");
	}

	@Bean
	public Queue errorPrintoutQueue() {
		return new Queue("error-printout-queue");
	}

	@Bean
	public Binding informationPrintoutQueuegBinding() {
		return BindingBuilder.bind(informationPrintoutQueue()).to(topicExchange()).with("infoOutput");
	}

	@Bean
	public Binding jiraActivityStreamPrintoutQueueBinding() {
		return BindingBuilder.bind(jiraActivityStreamPrintoutQueue()).to(topicExchange()).with("jiraActivityStreamOutput");
	}

	@Bean
	public Binding errorPrintoutQueueBinding() {
		return BindingBuilder.bind(errorPrintoutQueue()).to(topicExchange()).with("errorOutput");
	}
}