package dev.markodojkic.softwaredevelopmentsimulation.config;

import org.aopalliance.aop.Advice;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Configuration
@EnableIntegration
@IntegrationComponentScan(basePackages = "dev.markodojkic.softwaredevelopmentsimulation")
public class MiscellaneousConfig {
	public static final String CLIENT_ID = "fe-client_" + UUID.randomUUID();

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
	public MqttPahoClientFactory rabbitMQConnectionFactory() {
		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
		mqttConnectOptions.setServerURIs(new String[]{"ws://localhost:15675/ws"});
		mqttConnectOptions.setUserName("guest");
		mqttConnectOptions.setPassword("guest".toCharArray());
		mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
		mqttConnectOptions.setAutomaticReconnect(true);
		mqttConnectOptions.setWill("information-printout-topic", "Session ended".getBytes(StandardCharsets.UTF_8), 1, false);
		mqttConnectOptions.setKeepAliveInterval(3600);

		DefaultMqttPahoClientFactory defaultMqttPahoClientFactory = new DefaultMqttPahoClientFactory();
		defaultMqttPahoClientFactory.setConnectionOptions(mqttConnectOptions);
		return defaultMqttPahoClientFactory;
	}

	@Bean
	public MqttPahoMessageHandler rabbitMQMessageHandler() {
		MqttPahoMessageHandler mqttPahoMessageHandler = new MqttPahoMessageHandler(CLIENT_ID, rabbitMQConnectionFactory());
		mqttPahoMessageHandler.setDefaultTopic("default-queue");
		mqttPahoMessageHandler.setAsync(true);
		mqttPahoMessageHandler.setDefaultRetained(false);
		mqttPahoMessageHandler.setDefaultQos(1);
		mqttPahoMessageHandler.setConverter(new DefaultPahoMessageConverter());
		return mqttPahoMessageHandler;
	}

	//TODO: Intercept queue read message and flush to log file
}