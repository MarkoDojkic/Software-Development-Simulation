package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.web.DevelopersPageController;
import mockit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DevelopersPageController.class)
class DevelopersPageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        DataProvider.setupDataProvider(true);
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

        mockMvc.perform(post("/developers")
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
                .andExpect(view().name("/developers::editingDeveloperForm"))
                .andExpect(model().attributeExists("developmentTeams"))
                .andExpect(model().attribute("developmentTeamIndex", 0))
                .andExpect(model().attribute("developerIndex", 0));
    }

    @Test
    void when_editDeveloperMethodIsCalled_providedUserReplacesProvidedDevelopmentInProvidedDevelopmentTeam() throws Exception {
        Developer existingUser = DataProvider.getCurrentDevelopmentTeamsSetup().getFirst().getFirst();
        existingUser.setExperienceCoefficient((long) 2.45);

        mockMvc.perform(patch("/developers")
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
        mockMvc.perform(delete("/developers")
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

        mockMvc.perform(put("/developers")
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
                .andExpect(status().isOk())
                .andExpect(view().name("/developers"))
                .andExpect(model().attributeExists("developmentTeams"));
    }
}