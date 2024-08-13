package dev.markodojkic.softwaredevelopmentsimulation.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.PREDEFINED_DATA;

@RestController
public class MainController {
    private final ObjectMapper objectMapper;

    @Autowired
    public MainController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("technicalManager", DataProvider.getTechnicalManager());
        modelAndView.addObject("selectedEpicDevelopmentTeam", null);
        modelAndView.addObject("developmentTeams", DataProvider.getCurrentDevelopmentTeamsSetup());
        modelAndView.addObject("developmentTeamsSummary", DataProvider.getCurrentDevelopmentTeamsSetup().stream().map(developmentTeam -> String.format("%d (%.2f)", developmentTeam.size(), developmentTeam.stream().mapToDouble(developer -> (developer.getExperienceCoefficient() * developer.getDeveloperType().getSeniorityCoefficient() - 1) / (73.5 - 1) * (12 - 1) + 1).sum() / developmentTeam.size())).toList());
        modelAndView.addObject("priorities", Priority.values());

        return modelAndView;
    }

    @GetMapping(value = "/api/logs")
    public ResponseEntity<String> showLogFileContents(@RequestParam("filename") String filename) {
        try {
            return ResponseEntity.ok(Files.readString(Paths.get(Utilities.getCurrentApplicationLogsPath().resolve(filename.concat(".log")).toUri())));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to read file: " + e.getMessage());
        }
    }

    @GetMapping(value = "/api/getPredefinedDataFoldersList")
    public ResponseEntity<Object> getPredefinedDataFoldersList() {
        try (Stream<Path> paths = Files.list(Utilities.getCurrentApplicationDataPath().resolve(PREDEFINED_DATA))) {
            List<String> folders = paths
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString).toList();

            return ResponseEntity.ok(folders);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to get predefined data path folders list: " + e.getMessage());
        }
    }

    @PostMapping(value = "/api/saveSessionData")
    public ResponseEntity<String> saveCurrentPredefinedData(@RequestBody String sessionDataJSON){
        try {
            String folderName = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
            Path parentDirectory = Utilities.getCurrentApplicationDataPath().resolve(PREDEFINED_DATA);

            if(!Files.exists(parentDirectory)) Files.createDirectories(parentDirectory);

            Files.createDirectories(parentDirectory.resolve(folderName));

            Files.writeString(parentDirectory.resolve(folderName.concat("/sessionData.json")), sessionDataJSON);
            Files.writeString(parentDirectory.resolve(folderName.concat("/developersData.json")), objectMapper.writeValueAsString(DataProvider.getCurrentDevelopmentTeamsSetup()));

            return ResponseEntity.ok("Data successfully saved to folder '".concat(folderName).concat("'"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to save session data into file: " + e.getMessage());
        }
    }

    @GetMapping(value = "/api/loadSessionData")
    public ResponseEntity<String> loadCurrentPredefinedData(@RequestParam("folder") String folder) {
        try {
            DataProvider.replaceDevelopmentTeamsSetup(objectMapper.readValue(Files.readString(Utilities.getCurrentApplicationDataPath().resolve(PREDEFINED_DATA).resolve(folder.concat("/developersData.json"))), new TypeReference<>() {
            }));

            return ResponseEntity.ok(Files.readString(Utilities.getCurrentApplicationDataPath().resolve(PREDEFINED_DATA).resolve(folder.concat("/sessionData.json"))));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while trying to load predefined data: " + e.getMessage());
        }
    }
}