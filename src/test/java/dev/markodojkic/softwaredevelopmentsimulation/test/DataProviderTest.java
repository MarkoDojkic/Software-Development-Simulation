package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataProviderTest {
    @BeforeAll
    public static void preSetup(){
        setupDataProvider(true);
    }

    @Test
    void when_updateDevelopmentTeamsSetup_currentDevelopmentTeamsSetupIsPopulated_withReplacementOfOldValues() {
        DevelopmentTeamCreationParameters parameters = new DevelopmentTeamCreationParameters();
        updateDevelopmentTeamsSetup(parameters);

        List<List<Developer>> previousTeamSetup = getCurrentDevelopmentTeamsSetup();
        assertNotNull(previousTeamSetup);

        parameters.setRetainOld(false);
        updateDevelopmentTeamsSetup(parameters);
        assertNotNull(getCurrentDevelopmentTeamsSetup());
        assertNotEquals(previousTeamSetup, getCurrentDevelopmentTeamsSetup());
    }

    @Test
    void when_addDeveloperMethodIsCalled_providedUserIsAddedToProvidedDevelopmentTeam() {
        int developmentTeamIndex = 2;
        Developer developer = new Developer("Test Developer", Strings.EMPTY, DeveloperType.INTERN_DEVELOPER, false, (long) 1.25);
        List<Developer> originalTeam = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex);
        addDeveloper(developmentTeamIndex, developer);
        List<Developer> updatedTeam = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex);
        assertEquals(originalTeam.size() + 1, updatedTeam.size());
        assertTrue(updatedTeam.contains(developer));
    }

    @Test
    void when_editDeveloperMethodIsCalled_providedUserReplacesProvidedDevelopmentInProvidedDevelopmentTeam() {
        // Test editing developer information within the same team
        int developmentTeamIndex = 2;
        int developerIndex = 3;
        Developer newDeveloper = new Developer("Test Developer", "1406999368115", DeveloperType.INTERN_DEVELOPER, true, (long) 1.25);
        Developer originalDeveloper = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex);
        editDeveloper(developmentTeamIndex, developmentTeamIndex, developerIndex, newDeveloper);
        Developer updatedDeveloper = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex);
        assertEquals(newDeveloper, updatedDeveloper);
        assertNotEquals(originalDeveloper, updatedDeveloper);

        // Test moving developer to a different team
        int newDevelopmentTeamIndex = 1;
        int previousSize = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).size();
        Developer nextUser = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex+1);
        editDeveloper(newDevelopmentTeamIndex, developmentTeamIndex, developerIndex, newDeveloper);
        assertEquals(previousSize - 1, getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).size());
        assertEquals(nextUser, getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex));
        assertNotNull(getCurrentDevelopmentTeamsSetup().get(newDevelopmentTeamIndex));
    }

    @Test
    void when_removeDeveloperMethodIsCalled_providedUserIsRemovedFromProvidedDevelopmentTeam() {
        int developmentTeamIndex = 1;
        int developerIndex = 2;
        List<Developer> originalTeam = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex);
        removeDeveloper(developmentTeamIndex, developerIndex);
        List<Developer> updatedTeam = getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex);
        assertEquals(originalTeam.size() - 1, updatedTeam.size());
        assertFalse(updatedTeam.contains(originalTeam.get(developerIndex)));

        for(int i = 0; i < updatedTeam.size(); i++){
            removeDeveloper(developmentTeamIndex, 0);
        }

        assertFalse(getCurrentDevelopmentTeamsSetup().contains(updatedTeam));
    }

    @Test
    void when_getPlaceOfBirthBasedUMCNPoliticalRegionCode_itReturnsAppropriatePlaceOfBirth() {
        String placeOfBirth = getPlaceOfBirthBasedUMCNPoliticalRegionCode(32);
        assertTrue(placeOfBirth.equals("Croatia, Varaždin") || placeOfBirth.equals("Croatia, Međimurje region"));
    }
}