package dev.markodojkic.softwaredevelopmentsimulation.config;

import dev.markodojkic.softwaredevelopmentsimulation.util.EpicNotDoneException;
import dev.markodojkic.softwaredevelopmentsimulation.util.UserStoryNotDoneException;
import org.aopalliance.aop.Advice;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowDefinition;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.router.ErrorMessageExceptionTypeRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Configuration
@EnableIntegration
@IntegrationComponentScan(basePackages = "dev.markodojkic.softwaredevelopmentsimulation")
public class MiscellaneousConfig {
	private static final String CLIENT_ID = "be-client-" + UUID.randomUUID();

	@Value("${mqtt.serverURI}")
	private String serverURI;
	@Value("${mqtt.username}")
	private String username;
	@Value("${mqtt.password}")
	private String password;
	@Value("${mqtt.defaultTopic}")
	private String defaultTopic;

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {
		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setTrigger(new PeriodicTrigger(Duration.of(5, ChronoUnit.SECONDS)));
		return pollerMetadata;
	}

	@Bean(name = "retryAdvice")
	public Advice retryAdvice(){
		RequestHandlerRetryAdvice retryAdvice = new RequestHandlerRetryAdvice();
		retryAdvice.setRetryTemplate(new RetryTemplateBuilder().fixedBackoff(Duration.of(5, ChronoUnit.SECONDS)).infiniteRetry().build());
		return retryAdvice;
	}

	@Bean
	public IntegrationFlow controlBus() {
		return IntegrationFlowDefinition::controlBus; //Channel name is "controlBus.input"
	}

	@Bean
	public ErrorMessageExceptionTypeRouter exceptionTypeRouter(@Qualifier("errorChannel") MessageChannel errorChannel) {
		ErrorMessageExceptionTypeRouter router = new ErrorMessageExceptionTypeRouter();

		router.setChannelMapping(EpicNotDoneException.class.getName(), null);
		router.setChannelMapping(UserStoryNotDoneException.class.getName(), null);

		router.setDefaultOutputChannel(errorChannel); // Default channel for other exceptions

		return router;
	}

	@Bean
	public MqttPahoClientFactory mqttPahoClientFactory() {
		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
		mqttConnectOptions.setServerURIs(new String[]{serverURI});
		mqttConnectOptions.setUserName(username);
		mqttConnectOptions.setPassword(password.toCharArray());
		mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
		mqttConnectOptions.setAutomaticReconnect(true);
		mqttConnectOptions.setWill("information-printout-topic", "Session ended".getBytes(StandardCharsets.UTF_8), 1, false);
		mqttConnectOptions.setKeepAliveInterval(60);
		mqttConnectOptions.setCleanSession(false);
		DefaultMqttPahoClientFactory defaultMqttPahoClientFactory = new DefaultMqttPahoClientFactory();
		defaultMqttPahoClientFactory.setConnectionOptions(mqttConnectOptions);
		return defaultMqttPahoClientFactory;
	}

	@Bean
	public MqttPahoMessageHandler mqttPahoMessageHandler() {
		MqttPahoMessageHandler mqttPahoMessageHandler = new MqttPahoMessageHandler(CLIENT_ID, mqttPahoClientFactory());
		mqttPahoMessageHandler.setDefaultTopic(defaultTopic);
		mqttPahoMessageHandler.setAsync(true);
		mqttPahoMessageHandler.setDefaultRetained(false);
		mqttPahoMessageHandler.setDefaultQos(1);
		mqttPahoMessageHandler.setConverter(new DefaultPahoMessageConverter());
		return mqttPahoMessageHandler;
	}
}