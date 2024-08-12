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
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;

@WebMvcTest(MainController.class)
class MainControllerTests {
    @Autowired
    private MockMvc mockMvc;

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
    }

    @Test
    void whenShowFileContentsMethodIsCalled_exampleResponseEntityIsRetrieved() throws Exception {
        new MockUp<Files>() {
            @Mock
            public String readString(Path path) {
                return "Mock file contents";
            }
        };

        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs")
                        .param("filename", "testFile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Mock file contents"));
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
}