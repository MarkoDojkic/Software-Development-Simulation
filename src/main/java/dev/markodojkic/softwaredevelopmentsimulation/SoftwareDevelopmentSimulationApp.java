package dev.markodojkic.softwaredevelopmentsimulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.interfaces.IGateways;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.updateDevelopmentTeamsSetup;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@EnableConfigurationProperties
@SpringBootApplication
public class SoftwareDevelopmentSimulationApp
{
    public static void main(String[] args)
    {
        ConfigurableApplicationContext configurableApplicationContext = new SpringApplication(SoftwareDevelopmentSimulationApp.class).run(args);
        setIGateways(configurableApplicationContext.getBean(IGateways.class));
        setObjectMapper(configurableApplicationContext.getBean(ObjectMapper.class));

        setupDataProvider(false);
        updateDevelopmentTeamsSetup(new DevelopmentTeamCreationParameters());

        getIGateways().sendToInfo("Welcome to Software development simulator™ Developed by Ⓒ Marko Dojkić 2024$I hope you will enjoy using my spring integration web based application");

        //TODO: Fix issue with carousel (i.e. carousel item is below scroll container when scrolled and thus not interactable) - problem is only with shoelace instance, tried others but they won`t work
        //TODO: Write tests for complete application flow, predefined and randomized data flow saving and loading
    }
}