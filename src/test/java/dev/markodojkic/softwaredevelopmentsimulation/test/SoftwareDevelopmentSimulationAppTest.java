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
import java.util.Locale;
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

		CountDownLatch informationMQTTInputLatch = new CountDownLatch(1);

		List<String> infoMessages = new ArrayList<>();

		((DirectChannel) informationMQTTInput).addInterceptor(new ExecutorChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				infoMessages.add(message.getPayload().toString());
				informationMQTTInputLatch.countDown();
				return ExecutorChannelInterceptor.super.preSend(message, channel);
			}
		});

		String message = "Welcome to Software development simulator™ Developed by Ⓒ Marko Dojkić 2024$I hope you will enjoy using my spring integration web based application";

		Utilities.getIGateways().sendToInfo(message);

		// Wait for interceptors to process messages
		try {
			informationMQTTInputLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			fail("Interrupted while waiting for interceptors to complete", e);
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
		assertNotNull(doneEpicsOutput);

		CountDownLatch epicMessageInputLatch = new CountDownLatch(4);
		CountDownLatch epicMessageDoneLatch = new CountDownLatch(4);
		int epicCountDownLimit = 4;
		int epicCountUpperLimit = 4;

		List<Epic> epics = new ArrayList<>();

		((PriorityChannel) epicMessageInput).addInterceptor(new ExecutorChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				epics.add((Epic) message.getPayload());
				epicMessageInputLatch.countDown();
				return ExecutorChannelInterceptor.super.preSend(message, channel);
			}
		});

		mockMvc.perform(post("/api/applicationFlowRandomized").param("save", "false").param("min", String.valueOf(epicCountDownLimit)).param("max", String.valueOf(epicCountUpperLimit))).andExpect(status().is2xxSuccessful());

		try {
			epicMessageInputLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for interceptors to complete", e);
		}

		assertTrue(epics.size() >= epicCountDownLimit && epics.size() <= epicCountUpperLimit);

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

		((DirectChannel) doneEpicsOutput).addInterceptor(new ExecutorChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				epicMessageDoneLatch.countDown();
				return ExecutorChannelInterceptor.super.preSend(message, channel);
			}
		});

		try {
			epicMessageDoneLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for interceptors to complete", e);
		}

		((DirectChannel) doneEpicsOutput).removeInterceptor(0);

		assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("informationChannel.log")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("jiraActivityStreamChannel.log")).isEmpty());

		assertFalse(Files.exists(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00-00-00").resolve("sessionData.json")));
		assertFalse(Files.exists(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00-00-00").resolve("developersData.json")));

		System.setProperty("os.name", originalOsName);
	}

	@Test
	void when_generateRandomEpicsWithSave_epicsAreCorrectlyCreatedAndSaved() throws Exception {
		Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
		String originalOsName = System.getProperty("os.name");
		System.setProperty("os.name", "generic");
		assertNotNull(doneEpicsOutput);

		CountDownLatch epicMessageDoneLatch = new CountDownLatch(1);
		int epicCountDownLimit = 1;
		int epicCountUpperLimit = 1;

		mockMvc.perform(post("/api/applicationFlowRandomized").param("save", "true").param("min", String.valueOf(epicCountDownLimit)).param("max", String.valueOf(epicCountUpperLimit))).andExpect(status().is2xxSuccessful());

		((DirectChannel) doneEpicsOutput).addInterceptor(new ExecutorChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				epicMessageDoneLatch.countDown();
				return ExecutorChannelInterceptor.super.preSend(message, channel);
			}
		});

		try {
			epicMessageDoneLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for interceptors to complete", e);
		}

		assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("informationChannel.log")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationLogsPath().resolve("jiraActivityStreamChannel.log")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00-00-00").resolve("sessionData.json")).isEmpty());
		assertFalse(Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00-00-00").resolve("developersData.json")).isEmpty());

		System.setProperty("os.name", originalOsName);
	}

	@Test
	void when_predefinedDataIsUsed_correctApplicationFlowIsExpected() {
        try {
			assertNotNull(epicMessageInput);
			assertNotNull(doneEpicsOutput);
			assertNotNull(doneSprintUserStoriesOutput);
			assertNotNull(doneTechnicalTasksOutput);

			CountDownLatch epicsDoneInterceptorLatch = new CountDownLatch(1);
			List<Epic> epicsInput = new ArrayList<>();
			List<Epic> epicsDone = new ArrayList<>();
			List<UserStory> userStoriesDone = new ArrayList<>();
			List<TechnicalTask> technicalTasksDone = new ArrayList<>();

			((PriorityChannel) epicMessageInput).addInterceptor(new ExecutorChannelInterceptor() {
				@Override
				public Message<?> preSend(Message<?> message, MessageChannel channel) {
					epicsInput.add((Epic) message.getPayload());
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
					return ExecutorChannelInterceptor.super.preSend(message, channel);
				}
			});

			((DirectChannel) doneTechnicalTasksOutput).addInterceptor(new ExecutorChannelInterceptor() {
				@Override
				public Message<?> preSend(Message<?> message, MessageChannel channel) {
					technicalTasksDone.add((TechnicalTask) message.getPayload());
					return ExecutorChannelInterceptor.super.preSend(message, channel);
				}
			});

			replaceDevelopmentTeamsSetup(getObjectMapper().readValue(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testDevelopersData.json")).toURI())), new TypeReference<>() {}));

			mockMvc.perform(MockMvcRequestBuilders.post("/api/applicationFlowPredefined").content(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testSessionData.json")).toURI())))).andExpect(status().is2xxSuccessful());

			try {
				epicsDoneInterceptorLatch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Interrupted while waiting for interceptors to complete", e);
			}

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
					.andExpect(MockMvcResultMatchers.content().string("Data successfully saved to folder '2012-12-12 00-00-00'"));

			assertEquals(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testSessionData.json")).toURI())), Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00-00-00").resolve("sessionData.json")));
			assertEquals(Files.readString(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("testDevelopersData.json")).toURI())), Files.readString(getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 00-00-00").resolve("developersData.json")));
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

		mockMvc.perform(post("/api/applicationFlowRandomized").param("save", "false").param("min", String.valueOf(0)).param("max", String.valueOf(0))).andExpect(status().is2xxSuccessful());
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

		Uninterruptibles.sleepUninterruptibly(15, TimeUnit.SECONDS);

		String expectedWindows = """
				[38;5;196m/*\t- !ERROR! -
				  !-- Some error message
				\t - !ERROR! - */\u001B[0m%$
				""".trim();
		String expectedOther = """
				[38;5;196m/*	- !ERROR! -[0m
				[38;5;196m  !-- Some error message[0m
				[38;5;196m	 - !ERROR! - */[0m%$
				""".trim();

		assertEquals(System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("windows") ? expectedWindows : expectedOther, Files.readString(getCurrentApplicationLogsPath().resolve("errorChannel.log")).trim());
	}
}