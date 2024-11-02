package dev.markodojkic.softwaredevelopmentsimulation.test.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.DeveloperImpl;
import dev.markodojkic.softwaredevelopmentsimulation.ProjectManagerImpl;
import dev.markodojkic.softwaredevelopmentsimulation.config.MiscellaneousConfig;
import dev.markodojkic.softwaredevelopmentsimulation.config.SpringIntegrationMessageChannelsConfig;
import dev.markodojkic.softwaredevelopmentsimulation.flow.FileHandlingFlow;
import dev.markodojkic.softwaredevelopmentsimulation.flow.MQTTFlow;
import dev.markodojkic.softwaredevelopmentsimulation.flow.PrintoutFlow;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.transformer.PrinterTransformer;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import dev.markodojkic.softwaredevelopmentsimulation.web.DevelopersPageController;
import dev.markodojkic.softwaredevelopmentsimulation.web.MainController;
import io.moquette.broker.Server;
import io.moquette.broker.config.MemoryConfig;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Enables MockMvc with full context
@ContextConfiguration(classes = { MiscellaneousConfig.class, TestConfig.class, SpringIntegrationMessageChannelsConfig.class, MQTTFlow.class, PrintoutFlow.class, FileHandlingFlow.class, PrinterTransformer.class, DeveloperImpl.class, ProjectManagerImpl.class, MainController.class, DevelopersPageController.class })
//@ExtendWith({MockitoExtension.class, GlobalSetupExtension.class})
public abstract class SoftwareDevelopmentSimulationAppBaseTest {
    private static Server mqttServer;

    @Autowired
    @Qualifier("IGateways")
    private IGateways iGateways;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void preSetup() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("port", "21681");
        properties.setProperty("host", "0.0.0.0");
        properties.setProperty("password_file", ""); //No password
        properties.setProperty("allow_anonymous", "true");
        properties.setProperty("netty.mqtt.message_size", "102400");

        MemoryConfig memoryConfig = new MemoryConfig(properties);
        mqttServer = new Server();
        mqttServer.startServer(memoryConfig); //In memory MQTT server
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mqttServer.stopServer();
        FileUtils.deleteDirectory(Utilities.getCurrentApplicationDataPath().toAbsolutePath().toFile());
    }

    @BeforeEach
    public void setup() throws IOException {
        assertNotNull(iGateways);
        Utilities.setIGateways(iGateways);
        assertNotNull(Utilities.getIGateways());
        Utilities.setObjectMapper(objectMapper);
        setupDataProvider();
        updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());

        Files.createDirectories(Utilities.getCurrentApplicationDataPath());
        Files.createDirectories(Utilities.getCurrentApplicationLogsPath());
    }

    @BeforeEach
    public void setUp() {
        // Common setup code here
        // e.g., initializing beans, setting up data
        setupDataProvider();
        updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());
    }
}
