package dev.markodojkic.softwaredevelopmentsimulation.util;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;

import java.util.List;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

public class DataProvider {
	private DataProvider() {
		throw new IllegalStateException("Utility class");
	}

	public static final List<User> developers = List.of(
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.values()[random.nextInt(UserType.values().length)], random.nextDouble(1.00, 18.00))
	);
}
