package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Utilities {
	private Utilities() {
		throw new IllegalStateException("Utility class");
	}

	public static final Random random = new Random();
	public static final Lorem lorem = LoremIpsum.getInstance();
	public static long strToLong(Double value){
		return Long.getLong(value.toString());
	}

	public static <T> T getRandomElementFromList(List<T> list){
		return list.get(random.nextInt(list.size()));
	}

	//Below functions are adapted form https://github.com/borko-rajkovic/ts-jmbg
	public static String generateRandomJMBG() {
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
