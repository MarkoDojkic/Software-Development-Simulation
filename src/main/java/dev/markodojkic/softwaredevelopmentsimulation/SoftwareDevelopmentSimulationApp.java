package dev.markodojkic.softwaredevelopmentsimulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;

@EnableConfigurationProperties
@SpringBootApplication
public class SoftwareDevelopmentSimulationApp
{
    public static void main(String[] args)
    {
        ConfigurableApplicationContext configurableApplicationContext = new SpringApplication(SoftwareDevelopmentSimulationApp.class).run(args);
        Utilities.setIGateways(configurableApplicationContext.getBean(IGateways.class));
        Utilities.setObjectMapper(configurableApplicationContext.getBean(ObjectMapper.class));

        setupDataProvider(false);
        updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());

        Utilities.getIGateways().sendToInfo("Welcome to Software development simulator™ Developed by Ⓒ Marko Dojkić 2024$I hope you will enjoy using my spring integration web based application");

        //TODO: Correct JIRA activity stream timings
        //TODO: Fix issue with carousel that button (i.e. carousel item is below scroll container when scrolled and thus not interactable)
        //TODO: Add Edit logic for predefined epics, user stories and technical tasks (moving user stories and technical task is possibility)
        //TODO: Write tests for complete application flow, predefined and randomized data flow saving and loading
    }
}