package dev.markodojkic.softwaredevelopmentsimulation.web;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class MainController {
    @GetMapping(value = "/api/logs")
    public ResponseEntity<String> showFileContents(@RequestParam("filename") String filename) {
        try {
            return ResponseEntity.ok(Files.readString(Paths.get(Utilities.getCurrentApplicationLogsPath().resolve(filename.concat(".log")).toUri())));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        }
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
}