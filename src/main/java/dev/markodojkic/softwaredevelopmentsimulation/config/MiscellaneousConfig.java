package dev.markodojkic.softwaredevelopmentsimulation.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableIntegration
@IntegrationComponentScan(basePackages = "dev.markodojkic.softwaredevelopmentsimulation")
public class MiscellaneousConfig {
	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {
		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setTrigger(new PeriodicTrigger(Duration.of(1000, ChronoUnit.MILLIS)));
		return pollerMetadata;
	}

	@Bean(name = "retryAdvice")
	public Advice retryAdvice(){
		FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
		fixedBackOffPolicy.setBackOffPeriod(450L);
		return RetryInterceptorBuilder.stateless().maxAttempts(100).backOffPolicy(fixedBackOffPolicy).build();
	}

	@Bean
	public CachingConnectionFactory rabbitConnectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setHost("localhost");
		cachingConnectionFactory.setUsername("guest");
		cachingConnectionFactory.setPassword("guest");
		cachingConnectionFactory.setPublisherReturns(true);
		return cachingConnectionFactory;
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory());
		rabbitTemplate.setMandatory(true);
		return rabbitTemplate;
	}

	//TODO: Intercept queue read message and flush to log file
}