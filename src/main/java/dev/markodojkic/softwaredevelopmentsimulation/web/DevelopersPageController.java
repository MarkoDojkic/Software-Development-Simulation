package dev.markodojkic.softwaredevelopmentsimulation.web;

import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.*;

@Controller
public class DevelopersPageController {
	private static final String REDIRECT_DEVELOPERS = "redirect:/developers";

	@Operation(summary = "This method generates the ModelAndView for the developersPage view in a Spring MVC application")
	@ApiResponse(responseCode = "200", description = "Returns ModelAndView for developers page containing current development team setup")
	@GetMapping("/developers")
	public ModelAndView getDevelopersPage(){
		List<String> backgroundColors = getCurrentDevelopmentTeamsSetup().stream().map(developmentTeam -> getBackgroundColor(developmentTeam.getFirst().getDisplayName())).toList();
		ModelAndView developersPage = new ModelAndView("developersPage");

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

	@Operation(summary = "This method generates the ModelAndView for the developer editing fragment in a Spring MVC application")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Returns ModelAndView for developer editing fragment containing chosen developer data"),
			@ApiResponse(responseCode = "500", description = "In case of invalid development team index and/or developer index, which translates into request for non existent developer, internal server error is received as response")
	})
	@GetMapping(value = "/developers/edit")
	public ModelAndView getEditingDeveloperForm(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		ModelAndView editingDeveloperForm = new ModelAndView("developersPage :: editingDeveloperForm"); //Warning is false positive: View is thymeleaf fragment contained in developersPage.html file

		editingDeveloperForm.addObject("developmentTeams", getCurrentDevelopmentTeamsSetup());
		editingDeveloperForm.addObject("developmentTeamIndex", developmentTeamIndex);
		editingDeveloperForm.addObject("developerIndex", developerIndex);
		editingDeveloperForm.addObject("developerTypes", DeveloperType.values());
		editingDeveloperForm.addObject("formEditDeveloperPlaceholder", getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex));

		return editingDeveloperForm;
	}

	@Operation(summary = "This method replaces current developer team setup with newly generated one based on provided parameters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "Updates current developer team using provided parameters and redirects user to developers list page"),
			@ApiResponse(responseCode = "500", description = "In of invalid parameters, internal server error is received as response")
	})
	@PutMapping(value = "/api/recreateDevelopmentTeams")
	public String recreateDevelopmentTeams(@ModelAttribute(name = "parameters") DevelopmentTeamCreationParameters parameters){
		updateDevelopmentTeamsSetup(parameters);
		return REDIRECT_DEVELOPERS;
	}


	@Operation(summary = "This method adds new developer based on form data to selected developer team")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "New developer is added to team and user is redirected to developers list page"),
			@ApiResponse(responseCode = "500", description = "In case of invalid new developer data or non existent developer team, internal server error is received as response")
	})
	@PostMapping(value = "/api/insertDeveloper")
	public String insertDeveloper(@ModelAttribute(name = "formDeveloperPlaceholder") Developer newDeveloper, @ModelAttribute(name = "selectedDevelopmentTeamIndex") int developmentTeamIndex){
		addDeveloper(developmentTeamIndex, newDeveloper);
		return REDIRECT_DEVELOPERS;
	}



	@Operation(summary = "This method modifies existing developer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "Data for existing developer is modified and user is redirected to developers list page"),
			@ApiResponse(responseCode = "500", description = "In case of invalid developer data, internal server error is received as response")
	})
	@PatchMapping(value = "/api/modifyDeveloper")
	public String modifyDeveloper(@ModelAttribute(name = "formEditDeveloperPlaceholder") Developer existingDeveloper, @ModelAttribute(name = "editDeveloperSelectedDevelopmentTeamIndex") int developmentTeamIndex, @ModelAttribute(name = "developerIndex") int developerIndex, @ModelAttribute(name = "previousDevelopmentTeamIndex") int previousDevelopmentTeamIndex){
		editDeveloper(developmentTeamIndex == -1 ? previousDevelopmentTeamIndex : developmentTeamIndex, previousDevelopmentTeamIndex, developerIndex, existingDeveloper);
		return REDIRECT_DEVELOPERS;
	}

	@Operation(summary = "This method deletes existing developer")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Existing developer is removed"),
			@ApiResponse(responseCode = "500", description = "In case of invalid developer team index and/or developer index, which translates into request for non existent developer, internal server error is received as response")
	})
	@DeleteMapping(value = "/api/deleteDeveloper")
	public ModelAndView deleteDeveloper(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		removeDeveloper(developmentTeamIndex, developerIndex);
		return null; //This call is used in async manner so no redirection is needed
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