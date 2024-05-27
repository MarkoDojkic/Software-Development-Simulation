package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.google.common.collect.Lists;
import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@UtilityClass
public class DataProvider {
	public static final User technicalManager = new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.TECHNICAL_MANAGER, random.nextLong(1, 18));

	public static List<List<User>> currentDevelopmentTeamsSetup = Collections.emptyList();
	public static Stack<Integer> availableDevelopmentTeamIds = new Stack<>();

	public static void updateDevelopmentTeamsSetup(int countDownLimit, int countUpperLimit, boolean... retainOld){
		if(retainOld.length != 0 && !retainOld[0]) currentDevelopmentTeamsSetup = Collections.emptyList();
		currentDevelopmentTeamsSetup = Stream.concat(currentDevelopmentTeamsSetup.stream(), Lists.partition(Stream.generate((() -> new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), null, Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)))).limit(random.nextInt(10, 20)).toList(), random.nextInt(5, 10)).stream()).toList();
		availableDevelopmentTeamIds.addAll(IntStream.rangeClosed(0, currentDevelopmentTeamsSetup.size() - 1).boxed().toList());
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
