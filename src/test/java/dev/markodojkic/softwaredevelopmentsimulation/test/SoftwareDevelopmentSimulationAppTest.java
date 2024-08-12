package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.DeveloperImpl;
import dev.markodojkic.softwaredevelopmentsimulation.ProjectManagerImpl;
import dev.markodojkic.softwaredevelopmentsimulation.config.MiscellaneousConfig;
import dev.markodojkic.softwaredevelopmentsimulation.config.SpringIntegrationMessageChannelsConfig;
import dev.markodojkic.softwaredevelopmentsimulation.flow.FileHandlingFlow;
import dev.markodojkic.softwaredevelopmentsimulation.flow.MQTTFlow;
import dev.markodojkic.softwaredevelopmentsimulation.flow.PrintoutFlow;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.TestConfig;
import dev.markodojkic.softwaredevelopmentsimulation.transformer.PrinterTransformer;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import io.moquette.broker.Server;
import io.moquette.broker.config.MemoryConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = { MiscellaneousConfig.class, TestConfig.class, SpringIntegrationMessageChannelsConfig.class, MQTTFlow.class, PrintoutFlow.class, FileHandlingFlow.class, PrinterTransformer.class, DeveloperImpl.class, ProjectManagerImpl.class })
@ExtendWith(MockitoExtension.class)
class SoftwareDevelopmentSimulationAppTest {
	@Autowired
	@Qualifier(value = "information.mqtt.input")
	private MessageChannel informationMQTTInput;

	@Autowired
	@Qualifier(value = "epicMessage.input")
	private MessageChannel epicMessageInput;

    @Autowired
    @Qualifier("IGateways")
	private IGateways iGateways;

	private static Server mqttServer;


	@BeforeAll
	public static void preSetup() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("port", "21681");
		properties.setProperty("host", "0.0.0.0");
		properties.setProperty("password_file", ""); //No password
		properties.setProperty("allow_anonymous", "true");
		properties.setProperty("authenticator_class", "");
		properties.setProperty("authorizator_class", "");
		properties.setProperty("netty.mqtt.message_size", "32368");

		MemoryConfig memoryConfig = new MemoryConfig(properties);
		mqttServer = new Server();
		mqttServer.startServer(memoryConfig); //In memory MQTT server
		setupDataProvider(true);
	}

	@AfterAll
	public static void tearDown() {
		mqttServer.stopServer();
	}

	@BeforeEach
	public void setup() {
		assertNotNull(iGateways);
		Utilities.setIGateways(iGateways);
		assertNotNull(Utilities.getIGateways());
		setupDataProvider(true);
		updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());
	}

	@Test
	void whenSendInfoMessageViaGateway_InformationInputChannelReceiveMessageWithSentPayload() {
		assertNotNull(informationMQTTInput);

		CountDownLatch interceptorLatch = new CountDownLatch(1);

		List<String> infoMessages = new ArrayList<>();

		((DirectChannel) informationMQTTInput).addInterceptor(new ExecutorChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				infoMessages.add(message.getPayload().toString());
				interceptorLatch.countDown();
				return ExecutorChannelInterceptor.super.preSend(message, channel);
			}
		});

		String message = "Welcome to Software development simulator™ Developed by Ⓒ Marko Dojkić 2024$I hope you will enjoy using my spring integration web based application";

		Utilities.getIGateways().sendToInfo(message);

		// Wait for interceptors to process messages
		try {
			assertTrue(interceptorLatch.await(10, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for interceptors to complete", e);
		}

		assertEquals(1, infoMessages.size());

		assertEquals("/*\t- INFORMATION -\n\s\s* " +
				message.replace("$", String.format("%n***%s%n", "-".repeat(80))).replace("\n", "\n\s\s* ").replace("* ***", "\r") +
				"\n\t- INFORMATION - */", infoMessages.getFirst().replaceAll("\u001B\\[[;\\d]*m", "").trim());

		((DirectChannel) informationMQTTInput).removeInterceptor(0);
	}

	@Test
	void when_generateRandomTasks_epicsAreCorrectlyCreated() {
		assertNotNull(epicMessageInput);

		CountDownLatch interceptorLatch = new CountDownLatch(4);
		int epicCountDownLimit = 4;
		int epicCountUpperLimit = 5;

		List<Epic> epics = new ArrayList<>();

		((PriorityChannel) epicMessageInput).addInterceptor(new ExecutorChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				epics.add((Epic) message.getPayload());
				interceptorLatch.countDown();
				return ExecutorChannelInterceptor.super.preSend(message, channel);
				//Here preSend is used since this is pollable channel, so I can get all epics even whether they are polled or not
			}
		});

		Utilities.generateRandomTasks(epicCountDownLimit, epicCountUpperLimit);

		try {
			assertTrue(interceptorLatch.await(10, TimeUnit.MILLISECONDS));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for interceptors to complete", e);
		}

		assertTrue(epics.size() >= epicCountDownLimit && epics.size() < epicCountUpperLimit);

		for (Epic epic : epics) {
			List<UserStory> userStories = epic.getUserStories();
			assertNotNull(epic.getUserStories());
            assertFalse(epic.getUserStories().isEmpty());

			for (UserStory userStory : userStories) {
				assertNotNull(userStory.getTechnicalTasks());
				assertFalse(userStory.getTechnicalTasks().isEmpty());
			}
		}

		((PriorityChannel) epicMessageInput).removeInterceptor(0);
	}
}