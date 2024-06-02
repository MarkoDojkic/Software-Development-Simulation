package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.google.common.collect.Lists;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@UtilityClass
public class DataProvider {
	public static final User technicalManager = new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.TECHNICAL_MANAGER, random.nextLong(1, 18));

	public static List<List<User>> currentDevelopmentTeamsSetup = Collections.emptyList();
	public static Stack<Integer> availableDevelopmentTeamIds = new Stack<>();

	public static void updateDevelopmentTeamsSetup(DevelopmentTeamCreationParameters parameters){
		if(!parameters.isRetainOld()) currentDevelopmentTeamsSetup = Collections.emptyList();
		currentDevelopmentTeamsSetup = Stream.concat(currentDevelopmentTeamsSetup.stream(), Lists.partition(Stream.generate(() -> new User((random.nextInt(100) % 100 < parameters.getFemaleDevelopersPercentage() ? lorem.getNameFemale() : lorem.getNameMale()), UUID.randomUUID().toString(), Arrays.stream(UserType.values()).skip(random.nextInt(1, UserType.values().length)).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 10))).limit(random.nextInt(parameters.getMinimalDevelopersCount(), parameters.getMaximalDevelopersCount())).toList(), random.nextInt(parameters.getMinimalDevelopersInTeamCount(), parameters.getMaximalDevelopersInTeamCount())).stream()).collect(Collectors.toCollection(ArrayList::new));
		availableDevelopmentTeamIds.addAll(IntStream.rangeClosed(0, currentDevelopmentTeamsSetup.size() - 1).boxed().collect(Collectors.toCollection(ArrayList::new)));
	} //Generate between <min - default 30> and <max - default 100> developers ('User' class objects) and group them evenly in groups of anywhere between <min - default 5> and <max - default 15) and append that list to already existing list of developers (or use retainOld = false to override)

	public static void addDeveloper(int developmentTeamIndex, User developer){
		List<User> developmentTeam = new ArrayList<>(currentDevelopmentTeamsSetup.get(developmentTeamIndex));
		developmentTeam.add(developer);
		currentDevelopmentTeamsSetup.set(developmentTeamIndex, developmentTeam);
	}

	public static void editDeveloper(int developmentTeamIndex, int previousDevelopmentTeamIndex, int developerIndex, User developer){
		if(previousDevelopmentTeamIndex != developmentTeamIndex){
			removeDeveloper(previousDevelopmentTeamIndex, developerIndex);
			addDeveloper(developmentTeamIndex, developer);
		} else {
			List<User> developmentTeam = new ArrayList<>(currentDevelopmentTeamsSetup.get(previousDevelopmentTeamIndex));
			developmentTeam.set(developerIndex, developer);
			currentDevelopmentTeamsSetup.set(previousDevelopmentTeamIndex, developmentTeam);
		}
	}

	public static void removeDeveloper(int developmentTeamIndex, int developerIndex){
		List<User> developmentTeam = new ArrayList<>(currentDevelopmentTeamsSetup.get(developmentTeamIndex));
		developmentTeam.remove(developerIndex);
		currentDevelopmentTeamsSetup.set(developmentTeamIndex, developmentTeam);
	}

	//Below functions are adapted form https://github.com/borko-rajkovic/ts-jmbg
	private static String generateRandomJMBG() {
		String dd = String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		String mm = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1);
		String yyy = String.format("%03d", Calendar.getInstance().get(Calendar.YEAR) % 100);

		int rr = random.nextInt(100);
		int bbb = random.nextInt(1000);

		String withoutControlNumber = dd + mm + yyy + String.format("%02d", rr) + String.format("%03d", bbb);
		int calculatedControlNumber = calculateJMBGControlNumber(convertToDigits(withoutControlNumber));

		return withoutControlNumber + calculatedControlNumber;
	}

	private static int calculateJMBGControlNumber(int[] digits) {
		int sum = 0;
		for (int i = 0; i < digits.length; i++) {
			sum += (i % 2 == 0) ? digits[i] * 7 : digits[i] * 6;
		}
		return (11 - (sum % 11)) % 11;
	}

	private static int[] convertToDigits(String input) {
		int[] digits = new int[input.length()];
		for (int i = 0; i < input.length(); i++) {
			digits[i] = Character.getNumericValue(input.charAt(i));
		}
		return digits;
	}
}
