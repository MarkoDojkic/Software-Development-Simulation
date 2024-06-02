package dev.markodojkic.softwaredevelopmentsimulation.web;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DevelopersPageController {

	@PutMapping(value = "/developers")
	public String recreateDevelopmentTeams(@ModelAttribute(name = "parameters") DevelopmentTeamCreationParameters parameters){
		DataProvider.updateDevelopmentTeamsSetup(parameters);
		return "redirect:/developers";
	}

	@GetMapping("/developers")
	public ModelAndView getDevelopersPage(){
		List<String> backgroundColors = DataProvider.currentDevelopmentTeamsSetup.stream().map(developmentTeam -> getBackgroundColor(developmentTeam.get(0).getDisplayName())).toList();
		ModelAndView developersPage = new ModelAndView("developers");

		developersPage.addObject("developmentTeams", DataProvider.currentDevelopmentTeamsSetup);
		developersPage.addObject("developmentTeamBackgroundColors", backgroundColors);
		developersPage.addObject("developmentTeamForegroundColors", backgroundColors.stream().map(this::getForegroundColor).toList());
		developersPage.addObject("userTypes", UserType.values());
		developersPage.addObject("formDeveloperPlaceholder", new User());
		developersPage.addObject("formEditDeveloperPlaceholder", new User());

		return developersPage;
	}

	@PostMapping(value = "/developers")
	public String addDeveloper(@ModelAttribute(name = "formDeveloperPlaceholder") User newDeveloper, @ModelAttribute(name = "selectedDevelopmentTeamIndex") int developmentTeamIndex, @ModelAttribute(name = "gender") int gender){
		DataProvider.addDeveloper(developmentTeamIndex, gender, newDeveloper);
		return "redirect:/developers";
	}

	@RequestMapping(value = "/developers/edit", method = RequestMethod.GET)
	public ModelAndView getEditingDeveloperForm(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		ModelAndView editingDeveloperForm = new ModelAndView("developers::editingDeveloperForm");

		editingDeveloperForm.addObject("developmentTeams", DataProvider.currentDevelopmentTeamsSetup);
		editingDeveloperForm.addObject("developmentTeamIndex", developmentTeamIndex);
		editingDeveloperForm.addObject("developerIndex", developerIndex);
		editingDeveloperForm.addObject("userTypes", UserType.values());
		editingDeveloperForm.addObject("formEditDeveloperPlaceholder", DataProvider.currentDevelopmentTeamsSetup.get(developmentTeamIndex).get(developerIndex));

		return editingDeveloperForm;
	}

	@PatchMapping(value = "/developers")
	public String editDeveloper(@ModelAttribute(name = "formEditDeveloperPlaceholder") User existingDeveloper, @ModelAttribute(name = "editDeveloperSelectedDevelopmentTeamIndex") int developmentTeamIndex, @ModelAttribute(name = "developerIndex") int developerIndex, @ModelAttribute(name = "previousDevelopmentTeamIndex") int previousDevelopmentTeamIndex){
		DataProvider.editDeveloper(developmentTeamIndex == -1 ? previousDevelopmentTeamIndex : developmentTeamIndex, previousDevelopmentTeamIndex, developerIndex, existingDeveloper);
		return "redirect:/developers";
	}

	@RequestMapping(value = "/developers", method = RequestMethod.DELETE)
	public void removeDeveloper(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		DataProvider.removeDeveloper(developmentTeamIndex, developerIndex);
		//No redirection, removal is handled via ajax on front-end
	}

	private String getBackgroundColor(String text) {
		text = text.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
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
		if (colorStr == null) {
			return Arrays.asList(128, 128, 128);
		}
		ArrayList<Integer> rbg = new ArrayList<>();
		rbg.add(Integer.valueOf(colorStr.substring(1, 3), 16));
		rbg.add(Integer.valueOf(colorStr.substring(3, 5), 16));
		rbg.add(Integer.valueOf(colorStr.substring(5, 7), 16));
		return rbg;
	}

}
