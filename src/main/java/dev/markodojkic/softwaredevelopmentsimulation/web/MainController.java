package dev.markodojkic.softwaredevelopmentsimulation.web;

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
        modelAndView.addObject("developer", DataProvider.getTechnicalManager());
        return modelAndView;
    }
}
