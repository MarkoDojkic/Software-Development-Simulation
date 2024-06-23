package dev.markodojkic.softwaredevelopmentsimulation.web;

import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.generateRandomTasks;

@Controller
public class DevelopersPageController {
	private static final String REDIRECT_DEVELOPERS = "redirect:/developers";

	@PutMapping(value = "/developers")
	public String recreateDevelopmentTeams(@ModelAttribute(name = "parameters") DevelopmentTeamCreationParameters parameters){
		DataProvider.updateDevelopmentTeamsSetup(parameters);
		return REDIRECT_DEVELOPERS;
	}

	@GetMapping("/developers")
	public ModelAndView getDevelopersPage(){
		List<String> backgroundColors = DataProvider.getCurrentDevelopmentTeamsSetup().stream().map(developmentTeam -> getBackgroundColor(developmentTeam.getFirst().getDisplayName())).toList();
		ModelAndView developersPage = new ModelAndView("/developers");

		developersPage.addObject("developmentTeams", DataProvider.getCurrentDevelopmentTeamsSetup());
		developersPage.addObject("developmentTeamBackgroundColors", backgroundColors);
		developersPage.addObject("developmentTeamForegroundColors", backgroundColors.stream().map(this::getForegroundColor).toList());
		developersPage.addObject("developerTypes", DeveloperType.values());
		developersPage.addObject("formDeveloperPlaceholder", new Developer());
		developersPage.addObject("formEditDeveloperPlaceholder", new Developer());
		developersPage.addObject("developerIndex");
		developersPage.addObject("developmentTeamIndex");

		return developersPage;
	}

	@PostMapping(value = "/developers")
	public String addDeveloper(@ModelAttribute(name = "formDeveloperPlaceholder") Developer newDeveloper, @ModelAttribute(name = "selectedDevelopmentTeamIndex") int developmentTeamIndex){
		DataProvider.addDeveloper(developmentTeamIndex, newDeveloper);
		return REDIRECT_DEVELOPERS;
	}

	@GetMapping(value = "/developers/edit")
	public ModelAndView getEditingDeveloperForm(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		ModelAndView editingDeveloperForm = new ModelAndView("/developers::editingDeveloperForm"); //Warning is false positive: View is thymeleaf fragment contained in developers.html file

		editingDeveloperForm.addObject("developmentTeams", DataProvider.getCurrentDevelopmentTeamsSetup());
		editingDeveloperForm.addObject("developmentTeamIndex", developmentTeamIndex);
		editingDeveloperForm.addObject("developerIndex", developerIndex);
		editingDeveloperForm.addObject("developerTypes", DeveloperType.values());
		editingDeveloperForm.addObject("formEditDeveloperPlaceholder", DataProvider.getCurrentDevelopmentTeamsSetup().get(developmentTeamIndex).get(developerIndex));

		return editingDeveloperForm;
	}

	@PatchMapping(value = "/developers")
	public String editDeveloper(@ModelAttribute(name = "formEditDeveloperPlaceholder") Developer existingDeveloper, @ModelAttribute(name = "editDeveloperSelectedDevelopmentTeamIndex") int developmentTeamIndex, @ModelAttribute(name = "developerIndex") int developerIndex, @ModelAttribute(name = "previousDevelopmentTeamIndex") int previousDevelopmentTeamIndex){
		DataProvider.editDeveloper(developmentTeamIndex == -1 ? previousDevelopmentTeamIndex : developmentTeamIndex, previousDevelopmentTeamIndex, developerIndex, existingDeveloper);
		return REDIRECT_DEVELOPERS;
	}

	@DeleteMapping(value = "/developers")
	public ModelAndView removeDeveloper(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		DataProvider.removeDeveloper(developmentTeamIndex, developerIndex);
		return null;
	}

	@RequestMapping(value = "/api/applicationFlowRandomized", method = RequestMethod.OPTIONS)
	public ModelAndView applicationFlowRandomized(@RequestParam("min") int min, @RequestParam("max") int max){
		generateRandomTasks(min,max);
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