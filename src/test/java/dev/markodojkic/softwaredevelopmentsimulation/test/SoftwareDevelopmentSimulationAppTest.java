package dev.markodojkic.softwaredevelopmentsimulation.test;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.DeveloperImpl;
import dev.markodojkic.softwaredevelopmentsimulation.ProjectManagerImpl;
import dev.markodojkic.softwaredevelopmentsimulation.config.MiscellaneousConfig;
import dev.markodojkic.softwaredevelopmentsimulation.config.SpringIntegrationMessageChannelsConfig;
import dev.markodojkic.softwaredevelopmentsimulation.flow.AMQPFlow;
import dev.markodojkic.softwaredevelopmentsimulation.flow.PrintoutFlow;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.TestConfig;
import dev.markodojkic.softwaredevelopmentsimulation.transformer.PrinterTransformer;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.test.context.ContextConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = { MiscellaneousConfig.class, SpringIntegrationMessageChannelsConfig.class, TestConfig.class, AMQPFlow.class, PrintoutFlow.class, PrinterTransformer.class, DeveloperImpl.class, ProjectManagerImpl.class })
class SoftwareDevelopmentSimulationAppTest {
	@Autowired
	@Qualifier(value = "information.input")
	private DirectChannel informationInput;

	@Autowired
	@Qualifier(value = "epicMessage.input")
	private PriorityChannel epicMessageInput;

    @Autowired
    @Qualifier("IGateways")
	private IGateways iGateways;

	private final ByteArrayOutputStream soutContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream serrContent = new ByteArrayOutputStream();
	private final PrintStream originalSOut = System.out;
	private final PrintStream originalSErr = System.err;

	@Autowired
	private MockConnectionFactory mockConnectionFactory;

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
		assertNotNull(informationInput);

		System.setOut(new PrintStream(soutContent));
		System.setErr(new PrintStream(serrContent));

		MessageHandler messageHandler = message -> assertEquals("TEST PASSED", message.getPayload());

		informationInput.subscribe(messageHandler);

		Utilities.getIGateways().sendToInfo("TEST PASSED");

		System.setOut(originalSOut);
		System.setErr(originalSErr);
		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
		informationInput.unsubscribe(messageHandler);
	}

	@Test
	void when_generateRandomTasks_epicsAreCorrectlyCreated() {
		assertNotNull(epicMessageInput);

		// Define parameters for testing
		int epicCountDownLimit = 2; // Example lower limit of epic count
		int epicCountUpperLimit = 5; // Example upper limit of epic count

		// Get the total number of epics generated
		List<Epic> epics = new ArrayList<>();

		epicMessageInput.addInterceptor(new ChannelInterceptor() {
			@Override
			public Message<?> postReceive(Message<?> message, MessageChannel channel) {
				epics.add((Epic) message.getPayload());
				return ChannelInterceptor.super.postReceive(message, channel);
			}
		});

		// Call the method to generate random tasks
		Utilities.generateRandomTasks(epicCountDownLimit, epicCountUpperLimit);

		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

		// Test that the total number of generated epics is within the specified limits
		assertTrue(epics.size() >= epicCountDownLimit && epics.size() <= epicCountUpperLimit);

		// Test each generated epic
		for (Epic epic : epics) {
			// Test user stories for each epic
			List<UserStory> userStories = epic.getUserStories();
			assertNotNull(epic.getUserStories());
            assertFalse(epic.getUserStories().isEmpty());

			// Test each user story
			for (UserStory userStory : userStories) {
				assertNotNull(userStory.getTechnicalTasks());
				assertFalse(userStory.getTechnicalTasks().isEmpty());
			}
		}

		epicMessageInput.removeInterceptor(0);
	}
}