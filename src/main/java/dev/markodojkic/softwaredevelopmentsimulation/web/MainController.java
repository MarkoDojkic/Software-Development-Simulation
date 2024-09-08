package dev.markodojkic.softwaredevelopmentsimulation.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@RestController
public class MainController {
    private final ObjectMapper objectMapper;

    @Autowired
    public MainController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "This method generates the ModelAndView for the index page")
    @ApiResponse(responseCode = "200", description = "Returns ModelAndView for index page with needed data")
    @GetMapping(value = "/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("technicalManager", getTechnicalManager());
        modelAndView.addObject("selectedEpicDevelopmentTeam", null);
        modelAndView.addObject("developmentTeams", getCurrentDevelopmentTeamsSetup());
        modelAndView.addObject("developmentTeamsSummary", getCurrentDevelopmentTeamsSetup().stream().map(developmentTeam -> String.format("%d (%.2f)", developmentTeam.size(), developmentTeam.stream().mapToDouble(developer -> (developer.getExperienceCoefficient() * developer.getDeveloperType().getSeniorityCoefficient() - 1) / (73.5 - 1) * (12 - 1) + 1).sum() / developmentTeam.size())).toList());
        modelAndView.addObject("priorities", Priority.values());

        return modelAndView;
    }

    @Operation(summary = "This method returns content of log files (information, jiraActivity and error logs)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns content of provided log file"),
            @ApiResponse(responseCode = "500", description = "In case of any error (namely non existent file path), internal server error is received as response with exception message")
    })
    @GetMapping(value = "/api/logs")
    public ResponseEntity<String> showLogFileContents(@RequestParam("filename") String filename) {
        try {
            return ResponseEntity.ok(Files.readString(Paths.get(getCurrentApplicationLogsPath().resolve(filename.concat(".log")).toUri())));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to read file: " + e.getMessage());
        }
    }

    @Operation(summary = "This method returns list of predefined data folders, so user can choose adequate session data to load")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns list of folder names in predefined data path"),
            @ApiResponse(responseCode = "500", description = "In case of any error (namely non existent folder path), internal server error is received as response with exception message")
    })
    @GetMapping(value = "/api/getPredefinedDataFoldersList", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPredefinedDataFoldersList() {
        try (Stream<Path> paths = Files.list(getCurrentApplicationDataPath().resolve(PREDEFINED_DATA))) {
            List<String> folders = paths
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString).toList();

            return ResponseEntity.ok(String.join(",", folders));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN)
                    .body("Error occurred while trying to get predefined data path folders list: " + e.getMessage());
        }
    }

    @Operation(summary = "This method saves current session and development team setup data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information of successful data save is provided in message along with its path"),
            @ApiResponse(responseCode = "500", description = "In case of any error (namely non existent folder path), internal server error is received as response with exception message")
    })
    @PostMapping(value = "/api/saveSessionData")
    public ResponseEntity<String> saveCurrentPredefinedData(@RequestBody String sessionDataJSON){
        try {
            String folderName = System.getProperty("spring.profiles.active", "default").equals("test") ? "2012-12-12 00:00:00" : ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
            Path parentDirectory = getCurrentApplicationDataPath().resolve(PREDEFINED_DATA);

            Files.createDirectories(parentDirectory.resolve(folderName));

            Files.writeString(parentDirectory.resolve(folderName.concat("/sessionData.json")), sessionDataJSON);
            Files.writeString(parentDirectory.resolve(folderName.concat("/developersData.json")), objectMapper.writeValueAsString(getCurrentDevelopmentTeamsSetup()));

            return ResponseEntity.ok("Data successfully saved to folder '".concat(folderName).concat("'"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to save session data into file: " + e.getMessage());
        }
    }

    @Operation(summary = "This method loads wanted session and development team setup data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requested session data file content is retrieved as response and current development team setup is replaced with request developer data file content"),
            @ApiResponse(responseCode = "500", description = "In case of any error (namely non existent folder path), internal server error is received as response with exception message")
    })
    @GetMapping(value = "/api/loadSessionData")
    public ResponseEntity<String> loadCurrentPredefinedData(@RequestParam("folder") String folder) {
        try {
            replaceDevelopmentTeamsSetup(objectMapper.readValue(Files.readString(getCurrentApplicationDataPath().resolve(PREDEFINED_DATA).resolve(folder.concat("/developersData.json"))), new TypeReference<>() {
            }));

            return ResponseEntity.ok(Files.readString(getCurrentApplicationDataPath().resolve(PREDEFINED_DATA).resolve(folder.concat("/sessionData.json"))));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to load predefined data: " + e.getMessage());
        }
    }

    @Operation(summary = "This method starts application using predefined data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application flow is started and success message is received as response"),
            @ApiResponse(responseCode = "500", description = "In case of any error (namely invalid predefined epics data), internal server error is received as response with exception message")
    })
    @PostMapping(value = "/api/applicationFlowPredefined")
    public ResponseEntity<String> applicationFlowPredefined(@RequestBody String predefinedData){
        try {
            loadPredefinedTasks(objectMapper.readValue(predefinedData, new TypeReference<>() {
            }));
            return ResponseEntity.ok("Successfully started application flow with predefined data");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying start application flow with predefined data: " + e.getMessage());
        }
    }

    @Operation(summary = "This method starts application using randomized data given adequate parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application flow is started given adequate parameters"),
            @ApiResponse(responseCode = "500", description = "In case of any error (namely invalid parameters are provided), internal server error is received as response with exception message")
    })
    @PostMapping(value = "/api/applicationFlowRandomized")
    public ModelAndView applicationFlowRandomized(@RequestParam(name = "save", defaultValue = "false", required = false) boolean save, @RequestParam("min") int min, @RequestParam("max") int max){
        generateRandomEpics(save, min, max);
        return null; //This call is used in async matter so no redirection is needed
    }
}