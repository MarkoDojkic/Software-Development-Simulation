package dev.markodojkic.softwaredevelopmentsimulation.test;

import com.google.common.util.concurrent.Uninterruptibles;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.SoftwareDevelopmentSimulationAppBaseTest;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MainControllerTests extends SoftwareDevelopmentSimulationAppBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenShowFileContentsMethodIsCalled_withNonExistingFilePath_FileNotFoundErrorIsThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs")
                        .param("filename", "nonExistentTestFile"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(content().string(String.format("Error occurred while trying to read file: %s%snonExistentTestFile.log", Utilities.getCurrentApplicationLogsPath().toAbsolutePath(), File.separator)));
    }

    @Test
    void when_getRequestIsSentToRootEndpoint_indexPageIsRetrieved() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("index")) // Adjusted view name
                .andExpect(MockMvcResultMatchers.model().attributeExists("technicalManager"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("selectedEpicDevelopmentTeam"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("developmentTeams"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("developmentTeamsSummary"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priorities"));
    }

    @Test
    void when_getPredefinedDataFoldersListEndpointIsCalled() throws Exception {
        when_saveSessionDataEndpointIsCalled_savePredefinedData();

        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/getPredefinedDataFoldersList").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void when_getPredefinedDataFoldersListEndpointIsCalled_withNonExistingFilePath_PathNotFoundErrorIsThrown() throws Exception {
        try {
            FileUtils.deleteDirectory(Utilities.getCurrentApplicationDataPath().toAbsolutePath().toFile());
        } catch (IOException ignore) { /* Ignored exception */ }
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getPredefinedDataFoldersList").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void when_saveSessionDataEndpointIsCalled_savePredefinedData() throws Exception {
        String sessionDataJSON = "{\"key\":\"value\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveSessionData")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(sessionDataJSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Data successfully saved to folder '2012-12-12 00-00-00'"));
    }

    @Test
    void when_saveSessionDataEndpointIsCalled_givenMockedException_exceptionIsCaught() throws Exception {
        new MockUp<Files>() {
            @Mock
            public static Path writeString(Path path, CharSequence csq, OpenOption... options)
                    throws IOException
            {
                throw new IOException("Test Exception");
            }
        };

        String sessionDataJSON = "{\"key\":\"value\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveSessionData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionDataJSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error occurred while trying to save session data into file: Test Exception"));
    }

    @Test
    void when_loadSessionDataEndpointIsCalled_loadPredefinedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/loadSessionData")
                        .param("folder", "2012-12-12 00-00-00"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("{\"key\":\"value\"}"));
    }

    @Test
    void when_loadSessionDataEndpointIsCalled_loadPredefinedData_withNonExistingFilePath_fileNotFoundErrorIsThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/loadSessionData")
                        .param("folder", "2012-12-12 06-06-06"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(content().string(String.format("Error occurred while trying to load predefined data: %s%sdevelopersData.json", Utilities.getCurrentApplicationDataPath().resolve("predefinedData").resolve("2012-12-12 06-06-06"), File.separator)));
    }

    @Test
    void when_loadInvalidPredefinedData_internalServerErrorIsThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/applicationFlowPredefined")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {
                                    "epicId": "TMTV-101",
                                    "epicName": "Calendar SPRINT-12500",
                                    "epicPriority": "CRITICAL",
                                    "selectedEpicDevelopmentTeam": "-8",
                                    "epicReporter": "-1",
                                    "epicAssignee": "3",
                                    "epicCreatedOn": "11.23.2024. 17:12:39",
                                    "epicDescription": "Create calendar microservice with basic functions"
                                  }
                                ]"""))
                .andExpect(status().isInternalServerError());
    }
}