package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.google.common.collect.Lists;
import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@UtilityClass
public class DataProvider {
	@Getter
	@Setter
	static Developer technicalManager;

	@Getter
	static List<List<Developer>> currentDevelopmentTeamsSetup = Collections.emptyList();
	@Getter
	static Deque<Integer> availableDevelopmentTeamIds = new ArrayDeque<>();

	static final String PLACE_OF_BIRTH_MAPS_DEFAULT_VALUE = "Unknown";
	static NavigableMap<Integer, String> countryMap;
	static Map<Integer, String> regionMap;

	public static void setupDataProvider(boolean isTest){
		countryMap = new TreeMap<>();
		countryMap.put(0, "Outside of former Yugoslavia"); //0-9
		countryMap.put(10, "Bosnia and Herzegovina"); //10-19
		countryMap.put(20, "Montenegro"); //20-29
		countryMap.put(30, "Croatia"); //30-39
		countryMap.put(40, "Macedonia"); //40-49
		//50-59 is for Slovenia, 60-69 is for citizens with temporary residence
		countryMap.put(70, "Central Serbia"); //70-79
		countryMap.put(80, "Serbian province of Vojvodina"); //80-89
		countryMap.put(90, "Serbian province of Kosovo"); //90-99
		countryMap.put(100, PLACE_OF_BIRTH_MAPS_DEFAULT_VALUE); //Default (invalid value should not ever be this value!)

		regionMap = new HashMap<>();
		regionMap.putAll(Map.<Integer, String>ofEntries(Map.entry(0, "naturalized citizen without republican citizenship"), Map.entry(1, "foreigner in Bosnia and Herzegovina"), Map.entry(2, "foreigner in Montenegro"), Map.entry(3, "foreigner in Croatia"), Map.entry(4, "foreigner in Macedonia"), Map.entry(5, "foreigner in Slovenia"), Map.entry(6, "foreigner in Central Serbia"), Map.entry(7, "foreigner in Vojvodina"), Map.entry(8, "foreigner in Kosovo"), Map.entry(9, "naturalized citizen without republican citizenship"), Map.entry(10, "Banja Luka"), Map.entry(11, "Bihać"), Map.entry(12, "Doboj"), Map.entry(13, "Goražde"), Map.entry(14, "Livno"), Map.entry(15, "Mostar"), Map.entry(16, "Prijedor"), Map.entry(17, "Sarajevo"), Map.entry(18, "Tuzla"), Map.entry(19, "Zenica"), Map.entry(21, "Podgorica, Danilovgrad, Kolašin"), Map.entry(22, "Bar, Ulcinj"), Map.entry(23, "Budva, Kotor, Tivat"), Map.entry(24, "Herceg Novi"), Map.entry(25, "Cetinje"), Map.entry(26, "Nikšić, Plužine, Šavnik"), Map.entry(27, "Berane, Rožaje, Plav, Andrijevica"), Map.entry(28, "Bijelo Polje, Mojkovac"), Map.entry(29, "Pljevlja, Žabljak"), Map.entry(30, "Osijek, Slavonia region"), Map.entry(31, "Bjelovar, Virovitica, Koprivnica, Pakrac, Podravina region"), Map.entry(32, "Varaždin, Međimurje region"), Map.entry(33, "Zagreb"), Map.entry(34, "Karlovac, Kordun region"), Map.entry(35, "Gospić, Lika region"), Map.entry(36, "Rijeka, Pula, Gorski kotar, Istria and Croatian Littoral regions"), Map.entry(37, "Sisak, Banovina region"), Map.entry(38, "Split, Zadar, Šibenik, Dubrovnik, Dalmatia region"), Map.entry(39, "Hrvatsko Zagorje and mixed"), Map.entry(41, "Bitola"), Map.entry(42, "Kumanovo"), Map.entry(43, "Ohrid"), Map.entry(44, "Prilep"), Map.entry(45, "Skopje"), Map.entry(46, "Strumica"), Map.entry(47, "Tetovo"), Map.entry(48, "Veles"), Map.entry(49, "Štip"), Map.entry(70, "Serbian citizens registered abroad at a Serbian diplomatic/consular post"), Map.entry(71, "Belgrade region (City of Belgrade)"), Map.entry(72, "Šumadija and Pomoravlje regions"), Map.entry(73, "Niš region"), Map.entry(74, "Southern Morava region"), Map.entry(75, "Zaječar region"), Map.entry(76, "Podunavlje region"), Map.entry(77, "Podrinje and Kolubara regions"), Map.entry(78, "Kraljevo region"), Map.entry(79, "Užice region"), Map.entry(80, "Novi Sad region"), Map.entry(81, "Sombor region"), Map.entry(82, "Subotica region"), Map.entry(84, "Kikinda region"), Map.entry(85, "Zrenjanin region"), Map.entry(86, "Pančevo region"), Map.entry(87, "Vršac region"), Map.entry(88, "Ruma region"), Map.entry(89, "Sremska Mitrovica region"), Map.entry(91, "Priština region"), Map.entry(92, "Kosovska Mitrovica region"), Map.entry(93, "Peć region"), Map.entry(94, "Đakovica region"), Map.entry(95, "Prizren region"), Map.entry(96, "Gnjilane region")));

		setTechnicalManager(new Developer("Marko Dojkić", isTest ? generateRandomYugoslavianUMCN(false, true) : "???????71?000", DeveloperType.TECHNICAL_MANAGER, false, 5));
	}

	public static void updateDevelopmentTeamsSetup(DevelopmentTeamCreationParameters parameters){
		if(!parameters.isRetainOld()) currentDevelopmentTeamsSetup = Collections.emptyList();
		currentDevelopmentTeamsSetup = Stream.concat(currentDevelopmentTeamsSetup.stream(), Lists.partition(Stream.generate(() -> {
			boolean isFemale = SECURE_RANDOM.nextInt(100) % 100 < parameters.getFemaleDevelopersPercentage();
			return new Developer((isFemale ? LOREM.getNameFemale() : LOREM.getNameMale()), Strings.EMPTY, Arrays.stream(DeveloperType.values()).skip(SECURE_RANDOM.nextInt(1, DeveloperType.values().length)).findAny().orElse(DeveloperType.INTERN_DEVELOPER), isFemale, SECURE_RANDOM.nextLong(1, 10));
	}).limit(SECURE_RANDOM.nextInt(parameters.getMinimalDevelopersCount(), parameters.getMaximalDevelopersCount())).toList(), SECURE_RANDOM.nextInt(parameters.getMinimalDevelopersInTeamCount(), parameters.getMaximalDevelopersInTeamCount())).stream()).collect(Collectors.toCollection(ArrayList::new));
		availableDevelopmentTeamIds.addAll(IntStream.rangeClosed(0, currentDevelopmentTeamsSetup.size() - 1).boxed().collect(Collectors.toCollection(ArrayList::new)));
	} //Generate between <min - default 30> and <max - default 100> developers ('Developer' class objects) and group them evenly in groups of anywhere between <min - default 5> and <max - default 15) and append that list to already existing list of developers (or use retainOld = false to override)

	public static void addDeveloper(int developmentTeamIndex, Developer developer){
		List<Developer> developmentTeam = new ArrayList<>(currentDevelopmentTeamsSetup.get(developmentTeamIndex));
		developmentTeam.add(developer);
		currentDevelopmentTeamsSetup.set(developmentTeamIndex, developmentTeam);
	}

	public static void editDeveloper(int developmentTeamIndex, int previousDevelopmentTeamIndex, int developerIndex, Developer developer){
		if(previousDevelopmentTeamIndex != developmentTeamIndex){
			removeDeveloper(previousDevelopmentTeamIndex, developerIndex);
			addDeveloper(developmentTeamIndex, developer);
		} else {
			List<Developer> developmentTeam = new ArrayList<>(currentDevelopmentTeamsSetup.get(previousDevelopmentTeamIndex));
			developmentTeam.set(developerIndex, developer);
			currentDevelopmentTeamsSetup.set(previousDevelopmentTeamIndex, developmentTeam);
		}
	}

	public static void removeDeveloper(int developmentTeamIndex, int developerIndex){
		List<Developer> developmentTeam = new ArrayList<>(currentDevelopmentTeamsSetup.get(developmentTeamIndex));
		developmentTeam.remove(developerIndex);
		if(developmentTeam.isEmpty()) currentDevelopmentTeamsSetup.remove(developmentTeamIndex);
		else currentDevelopmentTeamsSetup.set(developmentTeamIndex, developmentTeam);
	}

	//Below functions are adapted from https://github.com/borko-rajkovic/ts-jmbg with slight changes for gender and randomized birthdate generation
	public static String generateRandomYugoslavianUMCN(boolean isFemale, boolean isTest) {
		LocalDateTime dateOfBirth = LOREM.getPriorDate(Duration.ofSeconds((long) 31536000 * 47)).minusYears(18);

		int rr = isTest ? 20 : SECURE_RANDOM.nextInt(97);
		int bbb = !isFemale ? SECURE_RANDOM.nextInt(0, 500) : SECURE_RANDOM.nextInt(500, 1000);

		String withoutControlNumber = String.format("%02d", dateOfBirth.getDayOfMonth()) + String.format("%02d", dateOfBirth.getMonth().getValue()) + String.format("%03d", dateOfBirth.getYear() % 1000) + String.format("%02d", rr == 20 || rr == 90 ? rr+1 : rr) + String.format("%03d", bbb);
		//Political region codes 20 and 90 are not used

		return withoutControlNumber + calculateControlNumber(convertToDigits(withoutControlNumber));
	}

	private static int calculateControlNumber(int[] digits) {
		int sum = 0;
		for (int i = 0; i < digits.length; i++) {
			sum += (i % 2 == 0) ? digits[i] * 7 : digits[i] * 6;
		}

		int number = (11 - (sum % 11)) % 11;

		return number > 9 ? 0 : number;
	}

	private static int[] convertToDigits(String input) {
		int[] digits = new int[input.length()];
		for (int i = 0; i < input.length(); i++) {
			digits[i] = Character.getNumericValue(input.charAt(i));
		}
		return digits;
	}

	//Below function and data for regionMap and countryMap were generated by ChatGPT :)
	public static String getPlaceOfBirthBasedUMCNPoliticalRegionCode(int rr){
		if(rr < 10) return "Outside of former Yugoslavia, ".concat(regionMap.getOrDefault(rr, PLACE_OF_BIRTH_MAPS_DEFAULT_VALUE));
		else if (rr >= 50 && rr <= 59) return "Somewhere in Slovenia";
		else if (rr >= 60 && rr <= 69) return "Unknown (Has temporary residence in Yugoslavia)";
		else return countryMap.floorEntry(rr).getValue().concat(", ").concat(regionMap.getOrDefault(rr, PLACE_OF_BIRTH_MAPS_DEFAULT_VALUE));
	}
}
