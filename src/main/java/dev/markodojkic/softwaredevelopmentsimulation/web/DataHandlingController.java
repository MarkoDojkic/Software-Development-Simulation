package dev.markodojkic.softwaredevelopmentsimulation.web;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class DataHandlingController {

	@GetMapping("/developers")
	public String getAllDevelopers(Model model){
		List<String> backgroundColors = DataProvider.currentDevelopmentTeamsSetup.stream().map(developmentTeam -> getBackgroundColor(developmentTeam.get(0).getDisplayName())).toList();
		model.addAttribute("developmentTeams", DataProvider.currentDevelopmentTeamsSetup);
		model.addAttribute("developmentTeamBackgroundColors", backgroundColors);
		model.addAttribute("developmentTeamForegroundColors", backgroundColors.stream().map(this::getForegroundColor).toList());
		model.addAttribute("userTypes", UserType.values());
		model.addAttribute("newDeveloper", new User());
		return "developers";
	}

	@RequestMapping(value = "/developers", params = {"save"}, method = RequestMethod.POST)
	public String addDeveloper(@ModelAttribute(name = "newDeveloper") User newDeveloper, @ModelAttribute(name = "developmentTeamId") int developmentTeamId, BindingResult bindingResult, ModelMap modelMap){
		if(!bindingResult.hasErrors()){
			//TODO: Implement thymeleaf form error handling
			return "redirect:/developers";
		}
		newDeveloper.setPersonalId(UUID.randomUUID().toString());
		DataProvider.addDeveloper(developmentTeamId, newDeveloper);
		modelMap.clear();
		return "redirect:/developers";
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
