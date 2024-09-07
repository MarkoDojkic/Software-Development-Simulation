package dev.markodojkic.softwaredevelopmentsimulation.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.SoftwareDevelopmentSimulationAppBaseTest;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SoftwareDevelopmentSimulationAppTest extends SoftwareDevelopmentSimulationAppBaseTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	@Qualifier(value = "information.mqtt.input")
	private MessageChannel informationMQTTInput;

	@Autowired
	@Qualifier(value = "epicMessage.input")
	private MessageChannel epicMessageInput;

	@Autowired
	@Qualifier(value = "doneEpics.output")
	private MessageChannel doneEpicsOutput;

	@Autowired
	@Qualifier(value = "doneSprintUserStories.output")
	private MessageChannel doneSprintUserStoriesOutput;

	@Autowired
	@Qualifier(value = "doneTechnicalTasks.output")
	private MessageChannel doneTechnicalTasksOutput;

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
	void when_generateRandomEpics_epicsAreCorrectlyCreated() throws Exception {
		String originalOsName = System.getProperty("os.name");
		System.setProperty("os.name", "generic");
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
			}
		});

		mockMvc.perform(post("/api/applicationFlowRandomized").param("save", "true").param("min", String.valueOf(epicCountDownLimit)).param("max", String.valueOf(epicCountUpperLimit))).andExpect(status().is2xxSuccessful());

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

		Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);

		assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("informationChannel.log")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("jiraActivityStreamChannel.log")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00:00:00").resolve("sessionData.json")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00:00:00").resolve("developersData.json")).isEmpty());

		System.setProperty("os.name", originalOsName);
	}

	@Test
	void when_predefinedDataIsUsed_correctApplicationFlowIsExpected() {
        try {
			assertNotNull(epicMessageInput);
			assertNotNull(doneEpicsOutput);
			assertNotNull(doneSprintUserStoriesOutput);
			assertNotNull(doneTechnicalTasksOutput);

			CountDownLatch epicsInputInterceptorLatch = new CountDownLatch(1);
			List<Epic> epicsInput = new ArrayList<>();

			CountDownLatch epicsDoneInterceptorLatch = new CountDownLatch(1);
			List<Epic> epicsDone = new ArrayList<>();

			CountDownLatch userStoriesDoneInterceptorLatch = new CountDownLatch(2);
			List<UserStory> userStoriesDone = new ArrayList<>();

			CountDownLatch technicalTasksDoneInterceptorLatch = new CountDownLatch(3);
			List<TechnicalTask> technicalTasksDone = new ArrayList<>();

			((PriorityChannel) epicMessageInput).addInterceptor(new ExecutorChannelInterceptor() {
				@Override
				public Message<?> preSend(Message<?> message, MessageChannel channel) {
					epicsInput.add((Epic) message.getPayload());
					epicsInputInterceptorLatch.countDown();
					return ExecutorChannelInterceptor.super.preSend(message, channel);
				}
			});

			((DirectChannel) doneEpicsOutput).addInterceptor(new ExecutorChannelInterceptor() {
				@Override
				public Message<?> preSend(Message<?> message, MessageChannel channel) {
					epicsDone.add((Epic) message.getPayload());
					epicsDoneInterceptorLatch.countDown();
					return ExecutorChannelInterceptor.super.preSend(message, channel);
				}
			});

			((DirectChannel) doneSprintUserStoriesOutput).addInterceptor(new ExecutorChannelInterceptor() {
				@Override
				public Message<?> preSend(Message<?> message, MessageChannel channel) {
					userStoriesDone.add((UserStory) message.getPayload());
					userStoriesDoneInterceptorLatch.countDown();
					return ExecutorChannelInterceptor.super.preSend(message, channel);
				}
			});

			((DirectChannel) doneTechnicalTasksOutput).addInterceptor(new ExecutorChannelInterceptor() {
				@Override
				public Message<?> preSend(Message<?> message, MessageChannel channel) {
					technicalTasksDone.add((TechnicalTask) message.getPayload());
					technicalTasksDoneInterceptorLatch.countDown();
					return ExecutorChannelInterceptor.super.preSend(message, channel);
				}
			});

			replaceDevelopmentTeamsSetup(getObjectMapper().readValue(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testDevelopersData.json")).toURI())), new TypeReference<>() {}));

			mockMvc.perform(MockMvcRequestBuilders.post("/api/applicationFlowPredefined").content(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testSessionData.json")).toURI())))).andExpect(status().is2xxSuccessful());

			Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS); //Time needed to complete application flow with test data

			for (Epic epic : epicsInput) {
				List<UserStory> userStories = epic.getUserStories();
				assertNotNull(epic.getUserStories());
				assertTrue(epicsDone.contains(epic));

				for (UserStory userStory : userStories) {
					assertNotNull(userStory.getTechnicalTasks());
					assertTrue(userStoriesDone.contains(userStory));

					for(TechnicalTask task : userStory.getTechnicalTasks()) {
						assertTrue(technicalTasksDone.contains(task));
					}
				}
			}

			((PriorityChannel) epicMessageInput).removeInterceptor(0);
			((DirectChannel) doneEpicsOutput).removeInterceptor(0);
			((DirectChannel) doneSprintUserStoriesOutput).removeInterceptor(0);
			((DirectChannel) doneTechnicalTasksOutput).removeInterceptor(0);

			assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("informationChannel.log")).isEmpty());
			assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("jiraActivityStreamChannel.log")).isEmpty());

			mockMvc.perform(MockMvcRequestBuilders.post("/api/saveSessionData")
							.contentType(MediaType.APPLICATION_JSON)
							.content(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testSessionData.json")).toURI()))))
					.andExpect(status().is2xxSuccessful())
					.andExpect(MockMvcResultMatchers.content().string("Data successfully saved to folder '2012-12-12 00:00:00'"));


			assertEquals(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testSessionData.json")).toURI())), Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00:00:00").resolve("sessionData.json")));
			assertEquals(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testDevelopersData.json")).toURI())), Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00:00:00").resolve("developersData.json")));
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

	@Test
	void when_emptyRandomizedDataIsUsed_applicationFlowPassesWithoutError() throws Exception {
		DevelopmentTeamCreationParameters parameters = new DevelopmentTeamCreationParameters();
		parameters.setRetainOld(false);
		parameters.setFemaleDevelopersPercentage(80);
		parameters.setMinimalDevelopersCount(5);
		parameters.setMaximalDevelopersCount(6);
		parameters.setMinimalDevelopersInTeamCount(5);
		parameters.setMaximalDevelopersInTeamCount(6);

		mockMvc.perform(put("/api/recreateDevelopmentTeams")
						.flashAttr("parameters", parameters))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/developers"));

		mockMvc.perform(post("/api/applicationFlowRandomized").param("save", "true").param("min", String.valueOf(0)).param("max", String.valueOf(0))).andExpect(status().is2xxSuccessful());
	}

	@Test
	void when_emptyOrPartialPredefinedDataIsUsed_applicationFlowPassesWithoutError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/applicationFlowPredefined")
						.contentType(MediaType.APPLICATION_JSON)
						.content("[]"))
				.andExpect(status().is2xxSuccessful());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/applicationFlowPredefined")
						.contentType(MediaType.APPLICATION_JSON)
						.content("[" +
								"  {\"userStories\": []," +
								"    \"epicId\": \"TMTV-101\"," +
								"    \"epicName\": \"Calendar SPRINT-12500\"," +
								"    \"epicPriority\": \"CRITICAL\"," +
								"    \"selectedEpicDevelopmentTeam\": \"5\"," +
								"    \"epicReporter\": \"-1\"," +
								"    \"epicAssignee\": \"3\"," +
								"    \"epicCreatedOn\": \"11.08.2024. 17:12:39\"," +
								"    \"epicDescription\": \"Create calendar microservice with basic functions\"" +
								"  }" +
								"]"))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void when_errorTextIsPassedToErrorChannel_errorApplicationFlowIsTriggered() throws IOException {
		getIGateways().sendToError("Some error message");

		Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

		assertTrue(Files.readString(getCurrentApplicationLogsPath().resolve("errorChannel.log")).contains("""
                \u001B[38;5;196m/*\t- !ERROR! -\u001B[0m
                \u001B[38;5;196m  !-- Some error message\u001B[0m
                \u001B[38;5;196m\t - !ERROR! - */\u001B[0m%$
                """));
	}
}