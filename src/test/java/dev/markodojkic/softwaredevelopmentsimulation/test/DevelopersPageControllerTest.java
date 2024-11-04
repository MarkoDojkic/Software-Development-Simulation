package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.SoftwareDevelopmentSimulationAppBaseTest;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.web.DeveloperControllerAdvice;
import mockit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.getPlaceOfBirthBasedUMCNPoliticalRegionCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DevelopersPageControllerTest extends SoftwareDevelopmentSimulationAppBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private DeveloperControllerAdvice developerControllerAdvice;

    @BeforeEach
    @Override
    public void setup() {
        new MockUp<DataProvider>() {
            @Mock
            public List<List<Developer>> getCurrentDevelopmentTeamsSetup() {
                return List.of(
                        List.of(new Developer("Test Developer", "1102986715356", DeveloperType.INTERN_DEVELOPER, false, (long) 1.25),
                                new Developer("Test Developer 2", "2212998651440", DeveloperType.JUNIOR_DEVELOPER, false, (long) 2.25),
                                new Developer("Test Developer 3", "3009001750100", DeveloperType.INTERN_DEVELOPER, true, (long) 1.25)
                        )
                );
            }
        };
    }

    @Test
    void when_addDeveloperMethodIsCalled_providedUserIsAddedToProvidedDevelopmentTeam() throws Exception {
        Developer newUser = new Developer("Test Developer 4", "", DeveloperType.MEDIOCRE_DEVELOPER, true, (long) 4.55);

        mockMvc.perform(post("/api/insertDeveloper")
                        .flashAttr("formDeveloperPlaceholder", newUser)
                        .flashAttr("selectedDevelopmentTeamIndex", 0))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/developers"));

        new Verifications() {{
            DataProvider.addDeveloper(0, newUser);
            times = 1;
        }};
    }

    @Test
    void when_getRequestIsSentToDevelopersEditEndpoint_editingDeveloperFormViewIsReturned() throws Exception {
        mockMvc.perform(get("/developers/edit")
                        .param("developmentTeamIndex", "0")
                        .param("developerIndex", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("developersPage :: editingDeveloperForm"))
                .andExpect(model().attributeExists("developmentTeams"))
                .andExpect(model().attributeExists("developerTypes"))
                .andExpect(model().attributeExists("formEditDeveloperPlaceholder"))
                .andExpect(model().attribute("developmentTeamIndex", 0))
                .andExpect(model().attribute("developerIndex", 0));
    }

    @Test
    void when_editDeveloperMethodIsCalled_providedUserReplacesProvidedDevelopmentInProvidedDevelopmentTeam() throws Exception {
        Developer existingUser = DataProvider.getCurrentDevelopmentTeamsSetup().getFirst().getFirst();
        existingUser.setExperienceCoefficient((long) 2.45);

        mockMvc.perform(patch("/api/modifyDeveloper")
                        .flashAttr("formEditDeveloperPlaceholder", existingUser)
                        .flashAttr("editDeveloperSelectedDevelopmentTeamIndex", 0)
                        .flashAttr("developerIndex", 0)
                        .flashAttr("previousDevelopmentTeamIndex", 0))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/developers"));

        new Verifications() {{
            DataProvider.editDeveloper(0, 0, 0, existingUser);
            times = 1;
        }};
    }

    @Test
    void when_removeDeveloperMethodIsCalled_providedUserIsRemovedFromProvidedDevelopmentTeam() throws Exception {
        mockMvc.perform(delete("/api/deleteDeveloper")
                        .param("developmentTeamIndex", "0")
                        .param("developerIndex", "0"))
                .andExpect(status().isOk());

        new Verifications() {{
            DataProvider.removeDeveloper(0, 0);
            times = 1;
        }};
    }

    @Test
    void when_updateDevelopmentTeamsSetup_currentDevelopmentTeamsSetupIsPopulated_withReplacementOfOldValues() throws Exception {
        DevelopmentTeamCreationParameters parameters = new DevelopmentTeamCreationParameters();
        parameters.setRetainOld(true);

        mockMvc.perform(put("/api/recreateDevelopmentTeams")
                        .flashAttr("parameters", parameters))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/developers"));

        new Verifications() {{
            DataProvider.updateDevelopmentTeamsSetup(parameters);
            times = 1;
        }};
    }

    @Test
    void when_getRequestIsSentToDevelopersEndpoint_developersPageViewIsReturned() throws Exception {
        mockMvc.perform(get("/developers"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("developersPage"))
                .andExpect(model().attributeExists("developmentTeams"))
                .andExpect(model().attributeExists("developmentTeamBackgroundColors"))
                .andExpect(model().attributeExists("developmentTeamForegroundColors"))
                .andExpect(model().attributeExists("developerTypes"))
                .andExpect(model().attributeExists("formDeveloperPlaceholder"))
                .andExpect(model().attributeExists("formEditDeveloperPlaceholder"))
                .andExpect(model().attributeDoesNotExist("developerIndex"))
                .andExpect(model().attributeDoesNotExist("developmentTeamIndex"));
    }

    @Test
    void when_nullValuesAreProvidedForDeveloper_defaultDataIsPopulatedUsingAdvice() {
        // Section 1: whenYugoslavianUMCNIsEmpty_thenItIsGenerated
        Developer developer = new Developer();  // Assuming Developer is your model class
        developerControllerAdvice = new DeveloperControllerAdvice();  // Your controller advice instance

        // Act
        Developer updatedDeveloper = developerControllerAdvice.overrideDeveloperFields(developer);

        // Assert
        assertNotNull(updatedDeveloper.getYugoslavianUMCN(), "Yugoslavian UMCN should be generated");

        // Section 2: whenIdIsEmpty_thenItIsGeneratedFromYugoslavianUMCN
        String mockYugoslavianUMCN = "1234567890123";
        developer.setYugoslavianUMCN(mockYugoslavianUMCN);

        // Act
        developer.setId(null);
        updatedDeveloper = developerControllerAdvice.overrideDeveloperFields(developer);

        // Assert
        UUID expectedUUID = UUID.nameUUIDFromBytes(mockYugoslavianUMCN.getBytes(StandardCharsets.UTF_8));
        assertEquals(expectedUUID.toString(), updatedDeveloper.getId(), "ID should be generated from Yugoslavian UMCN");

        // Section 3: whenPlaceOfBirthIsEmpty_thenItIsSetBasedOnRegionCode
        mockYugoslavianUMCN = "1234567012345";  // Region code 01
        developer.setYugoslavianUMCN(mockYugoslavianUMCN);
        developer.setPlaceOfBirth(null);

        // Act
        updatedDeveloper = developerControllerAdvice.overrideDeveloperFields(developer);

        // Assert
        String expectedPlaceOfBirth = getPlaceOfBirthBasedUMCNPoliticalRegionCode(1);  // Region code from UMCN
        assertEquals(expectedPlaceOfBirth, updatedDeveloper.getPlaceOfBirth(), "Place of birth should be set based on UMCN's region code");
    }

}