package dev.markodojkic.softwaredevelopmentsimulation.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.generateRandomEpics;
import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.loadPredefinedTasks;

@Controller
public class DevelopersPageController {
	private static final String REDIRECT_DEVELOPERS = "redirect:/developers";

	private final ObjectMapper objectMapper;

	@Autowired
	public DevelopersPageController(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@GetMapping("/developers")
	public ModelAndView getDevelopersPage(){
		List<String> backgroundColors = getCurrentDevelopmentTeamsSetup().stream().map(developmentTeam -> getBackgroundColor(developmentTeam.getFirst().getDisplayName())).toList();
		ModelAndView developersPage = new ModelAndView("/developers");

		developersPage.addObject("developmentTeams", getCurrentDevelopmentTeamsSetup());
		developersPage.addObject("developmentTeamBackgroundColors", backgroundColors);
		developersPage.addObject("developmentTeamForegroundColors", backgroundColors.stream().map(this::getForegroundColor).toList());
		developersPage.addObject("developerTypes", DeveloperType.values());
		developersPage.addObject("formDeveloperPlaceholder", new Developer());
		developersPage.addObject("formEditDeveloperPlaceholder", new Developer());
		developersPage.addObject("developerIndex");
		developersPage.addObject("developmentTeamIndex");

		return developersPage;
	}

	@GetMapping("/test")
	public ModelAndView gettest(){
		return new ModelAndView("/test");
	}

	@GetMapping(value = "/developers/edit")
	public ModelAndView getEditingDeveloperForm(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		ModelAndView editingDeveloperForm = new ModelAndView("/developers::editingDeveloperForm"); //Warning is false positive: View is thymeleaf fragment contained in developers.html file

		editingDeveloperForm.addObject("developmentTeams", getCurrentDevelopmentTeamsSetup());
		editingDeveloperForm.addObject("developmentTeamIndex", developmentTeamIndex);
		editingDeveloperForm.addObject("developerIndex", developerIndex);
		editingDeveloperForm.addObject("developerTypes", DeveloperType.values());
		editingDeveloperForm.addObject("formEditDeveloperPlaceholder", getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex));

		return editingDeveloperForm;
	}

	@PutMapping(value = "/api/recreateDevelopmentTeams")
	public String recreateDevelopmentTeams(@ModelAttribute(name = "parameters") DevelopmentTeamCreationParameters parameters){
		updateDevelopmentTeamsSetup(parameters);
		return REDIRECT_DEVELOPERS;
	}


	@PostMapping(value = "/api/insertDeveloper")
	public String insertDeveloper(@ModelAttribute(name = "formDeveloperPlaceholder") Developer newDeveloper, @ModelAttribute(name = "selectedDevelopmentTeamIndex") int developmentTeamIndex){
		addDeveloper(developmentTeamIndex, newDeveloper);
		return REDIRECT_DEVELOPERS;
	}



	@PatchMapping(value = "/api/modifyDeveloper")
	public String modifyDeveloper(@ModelAttribute(name = "formEditDeveloperPlaceholder") Developer existingDeveloper, @ModelAttribute(name = "editDeveloperSelectedDevelopmentTeamIndex") int developmentTeamIndex, @ModelAttribute(name = "developerIndex") int developerIndex, @ModelAttribute(name = "previousDevelopmentTeamIndex") int previousDevelopmentTeamIndex){
		editDeveloper(developmentTeamIndex == -1 ? previousDevelopmentTeamIndex : developmentTeamIndex, previousDevelopmentTeamIndex, developerIndex, existingDeveloper);
		return REDIRECT_DEVELOPERS;
	}

	@DeleteMapping(value = "/api/deleteDeveloper")
	public ModelAndView deleteDeveloper(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		removeDeveloper(developmentTeamIndex, developerIndex);
		return null; //Solves issue: Error resolving template [api/removeDeveloper]
	}

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

	@PostMapping(value = "/api/applicationFlowRandomized")
	public ModelAndView applicationFlowRandomized(@RequestParam(name = "save", defaultValue = "false", required = false) boolean save, @RequestParam("min") int min, @RequestParam("max") int max){
		generateRandomEpics(save, min, max);
		return null;
	}

	private String getBackgroundColor(String text) {
		text = text.toLowerCase().replaceAll("[^a-zA-Z0-9]", Strings.EMPTY);
		int hash = 0;
		for (int i = 0; i < text.length(); i++) {
			hash = text.charAt(i) + ((hash << 5) - hash);
		}
		int finalHash = Math.abs(hash) % (256 * 256 * 256);
		int red = ((finalHash & 0xFF0000) >> 16);
		int blue = ((finalHash & 0xFF00) >> 8);
		int green = (finalHash & 0xFF);
		return String.format("#%02x%02x%02x", red, green, blue);
	}

	private String getForegroundColor(String backgroundColor) {
		return (calculateLuminance(hexToRBG(backgroundColor)) < 140) ? "#fff" : "#000";
	}

	private float calculateLuminance(List<Integer> rgb) {
		return (float) (0.2126 * rgb.get(0) + 0.7152 * rgb.get(1) + 0.0722 * rgb.get(2));
	}

	private List<Integer> hexToRBG(String colorStr) {
		ArrayList<Integer> rbg = new ArrayList<>();
		rbg.add(Integer.valueOf(colorStr.substring(1, 3), 16));
		rbg.add(Integer.valueOf(colorStr.substring(3, 5), 16));
		rbg.add(Integer.valueOf(colorStr.substring(5, 7), 16));
		return rbg;
	}
}