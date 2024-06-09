package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.Developer;
import dev.markodojkic.softwaredevelopmentsimulation.ProjectManager;
import dev.markodojkic.softwaredevelopmentsimulation.config.MiscellaneousConfig;
import dev.markodojkic.softwaredevelopmentsimulation.config.SpringIntegrationMessageChannelsConfig;
import dev.markodojkic.softwaredevelopmentsimulation.flow.PrintoutFlow;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.TestConfig;
import dev.markodojkic.softwaredevelopmentsimulation.transformer.PrinterTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MiscellaneousConfig.class, SpringIntegrationMessageChannelsConfig.class, TestConfig.class, PrintoutFlow.class, PrinterTransformer.class, Developer.class, ProjectManager.class })
public class SoftwareDevelopmentSimulationAppTest {
	@Autowired
	@Qualifier(value = "information.input")
	private DirectChannel informationInput;

    @Autowired
	private IGateways iGateways;

	private final ByteArrayOutputStream soutContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream serrContent = new ByteArrayOutputStream();
	private final PrintStream originalSOut = System.out;
	private final PrintStream originalSErr = System.err;

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(soutContent));
		System.setErr(new PrintStream(serrContent));
	}

	@After
	public void restoreStreams() {
		System.setOut(originalSOut);
		System.setErr(originalSErr);
	}

	@Test
	public void whenSendInfoMessageViaGateway_InformationInputChannelReceiveMessageWithSentPayload_and_ConsoleOutputIsCorrect() {
		informationInput.subscribe(message -> {
			assert(message.getPayload().equals("TEST PASSED"));
			assert(soutContent.toString().contains("""
                            \u001B[38;5;68m/*\t- INFORMATION -\u001B[0m
                            \u001B[38;5;68m  * TEST PASSED\u001B[0m
                            \u001B[38;5;68m\t- INFORMATION - */\u001B[0m"""));
		});
		iGateways.sendToInfo("TEST PASSED");
	}
}