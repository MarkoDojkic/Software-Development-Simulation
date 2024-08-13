package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import dev.markodojkic.softwaredevelopmentsimulation.web.MainController;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;

@WebMvcTest(MainController.class)
class MainControllerTests {
    @Autowired
    private MockMvc mockMvc;

    private static final ZonedDateTime now = ZonedDateTime.now();

    @BeforeAll
    public static void preSetup(){
        setupDataProvider(true);
    }

    @BeforeEach
    void setup() {
        new MockUp<Utilities>() {
            @Mock
            public Path getCurrentApplicationLogsPath() {
                return Paths.get("/mock/path");
            }
        };

        new MockUp<ZonedDateTime>(){
            @Mock
            public static ZonedDateTime now() {
                return now;
            }
        };

        new MockUp<Files>() {
            @Mock
            public String readString(Path path) {
                return "[]";
            }

            @Mock
            public static Path writeString(Path path, CharSequence csq, OpenOption... options)
                    throws IOException
            {
                return path;
            }
        };
    }

    @Test
    void whenShowFileContentsMethodIsCalled_exampleResponseEntityIsRetrieved() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs")
                        .param("filename", "testFile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void whenShowFileContentsMethodIsCalled_withNonExistingFilePath_FileNotFoundErrorIsThrown() throws Exception {
        new MockUp<Files>() {
            @Mock
            public String readString(Path path) throws IOException {
                throw new IOException("File not found");
            }
        };

        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs")
                        .param("filename", "nonExistentTestFile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error occurred while trying to read file: File not found"));
    }

    @Test
    void when_getRequestIsSentToRootEndpoint_indexPageIsRetrieved() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("developer"));
    }

    @Test
    void when_getPredefinedDataFoldersListEndpointIsCalled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/getPredefinedDataFoldersList"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void when_getPredefinedDataFoldersListEndpointIsCalled_withNonExistingFilePath_PathNotFoundErrorIsThrown() throws Exception {
        new MockUp<Files>() {
            @Mock
            public static Stream<Path> list(Path dir) throws IOException {
                throw new IOException("Path not found");
            }
        };

        mockMvc.perform(MockMvcRequestBuilders.get("/api/getPredefinedDataFoldersList"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error occurred while trying to get predefined data path folders list: Path not found"));
    }

    @Test
    void when_saveSessionDataEndpointIsCalled_savePredefinedData() throws Exception {
        String sessionDataJSON = "{\"key\":\"value\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveSessionData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionDataJSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Data successfully saved to folder '".concat(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))).concat("'")));
    }

    @Test
    void when_saveSessionDataEndpointIsCalled_savePredefinedData_withNonExistingFilePath_FileNotFoundErrorIsThrown() throws Exception {
        new MockUp<Files>() {
            @Mock
            public static Path writeString(Path path, CharSequence csq, OpenOption... options)
                    throws IOException {
                throw new IOException("Path not found for parent directory");
            }
        };

        String sessionDataJSON = "{\"key\":\"value\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/saveSessionData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessionDataJSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error occurred while trying to save session data into file: Path not found for parent directory"));
    }

    @Test
    void when_loadSessionDataEndpointIsCalled_loadPredefinedData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/loadSessionData")
                        .param("folder", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void when_loadSessionDataEndpointIsCalled_loadPredefinedData_withNonExistingFilePath_FileNotFoundErrorIsThrown() throws Exception {
        new MockUp<Files>() {
            @Mock
            public String readString(Path path) throws IOException {
                throw new IOException("Path not found for parent directory");
            }
        };

        mockMvc.perform(MockMvcRequestBuilders.get("/api/loadSessionData")
                        .param("folder", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))+"?"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error occurred while trying to load predefined data: Path not found for parent directory"));
    }
}