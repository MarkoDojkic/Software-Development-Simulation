package dev.markodojkic.softwaredevelopmentsimulation.web;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class DevelopersPageController {

	@PutMapping(value = "/developers")
	public String recreateDevelopmentTeams(@ModelAttribute(name = "parameters") DevelopmentTeamCreationParameters parameters, BindingResult bindingResult, ModelMap modelMap){
		if(bindingResult.hasErrors()){
			Utilities.iGateways.sendToError(bindingResult.toString());
		}
		//FIGURE OUT HOW TO PASS BUILDER AS REQUEST BODY
		DataProvider.updateDevelopmentTeamsSetup(parameters);
		modelMap.clear();
		return "redirect:/developers";
	}

	@GetMapping("/developers")
	public String getAllDeveloper(Model model){
		List<String> backgroundColors = DataProvider.currentDevelopmentTeamsSetup.stream().map(developmentTeam -> getBackgroundColor(developmentTeam.get(0).getDisplayName())).toList();
		model.addAttribute("developmentTeams", DataProvider.currentDevelopmentTeamsSetup);
		model.addAttribute("developmentTeamBackgroundColors", backgroundColors);
		model.addAttribute("developmentTeamForegroundColors", backgroundColors.stream().map(this::getForegroundColor).toList());
		model.addAttribute("userTypes", UserType.values());
		model.addAttribute("formDeveloperPlaceholder", new User());
		model.addAttribute("formEditDeveloperPlaceholder", new User());
		return "developers";
	}

	@PostMapping(value = "/developers")
	public String addDeveloper(@ModelAttribute(name = "formDeveloperPlaceholder") User newDeveloper, @ModelAttribute(name = "selectedDevelopmentTeamIndex") int developmentTeamIndex, BindingResult bindingResult, ModelMap modelMap){
		if(bindingResult.hasErrors()){
			//TODO: Implement thymeleaf form error handling
			return "/developers";
		}
		newDeveloper.setPersonalId(UUID.randomUUID().toString());
		DataProvider.addDeveloper(developmentTeamIndex, newDeveloper);
		modelMap.clear();
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
	public String editDeveloper(@ModelAttribute(name = "formEditDeveloperPlaceholder") User existingDeveloper, @ModelAttribute(name = "editDeveloperSelectedDevelopmentTeamIndex") int developmentTeamIndex, @ModelAttribute(name = "developerIndex") int developerIndex, @ModelAttribute(name = "previousDevelopmentTeamIndex") int previousDevelopmentTeamIndex, BindingResult bindingResult, ModelMap modelMap){
		if(bindingResult.hasErrors()){
			//TODO: Implement thymeleaf form error handling
			return "redirect:/developers";
		}
		DataProvider.editDeveloper(developmentTeamIndex == -1 ? previousDevelopmentTeamIndex : developmentTeamIndex, previousDevelopmentTeamIndex, developerIndex, existingDeveloper);
		modelMap.clear();
		return "redirect:/developers";
	}

	@RequestMapping(value = "/developers", method = RequestMethod.DELETE)
	public void removeDeveloper(@RequestParam("developmentTeamIndex") int developmentTeamIndex, @RequestParam("developerIndex") int developerIndex){
		DataProvider.removeDeveloper(developmentTeamIndex, developerIndex);
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
