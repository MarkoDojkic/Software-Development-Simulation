package dev.markodojkic.softwaredevelopmentsimulation.util;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import dev.markodojkic.softwaredevelopmentsimulation.model.User;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

import static dev.markodojkic.softwaredevelopmentsimulation.util.Utilities.*;

@UtilityClass
public class DataProvider {
	public static final List<User> developers = List.of(
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), UserType.TECHNICAL_MANAGER, random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18)),
			new User((random.nextInt(100) % 100 < 45 ? lorem.getNameFemale() : lorem.getNameMale()), generateRandomJMBG(), Arrays.stream(UserType.values()).skip(1).findAny().orElse(UserType.INTERN_DEVELOPER), random.nextLong(1, 18))
	);
}
