package dev.markodojkic.softwaredevelopmentsimulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Files;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@EnableConfigurationProperties
@SpringBootApplication
public class SoftwareDevelopmentSimulationApp
{
    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = new SpringApplication(SoftwareDevelopmentSimulationApp.class).run(args);
        setIGateways(configurableApplicationContext.getBean(IGateways.class));
        setObjectMapper(configurableApplicationContext.getBean(ObjectMapper.class));

        setupDataProvider();
        updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());

        try {
            getIGateways().sendToInfo(String.format("Welcome to Software development simulator™ Developed by Ⓒ Marko Dojkić 2024%nSize occupied by application user data is: %.2f KB%nI hope you will enjoy using my spring integration web-based application", (double) Files.size(getCurrentApplicationDataPath()) / 1024));
        } catch (IOException e) {
            getIGateways().sendToInfo(String.format("Welcome to Software development simulator™ Developed by Ⓒ Marko Dojkić 2024%nSize occupied by application user data is: %.2f KB%nI hope you will enjoy using my spring integration web-based application", 0.00));
        }
    }
}