package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.getPlaceOfBirthBasedUMCNPoliticalRegionCode;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = -949472808846392995L;

	private String id;
	private String name;
	private String surname;
	private String yugoslavianUMCN;
	private String placeOfBirth;
	private UserType userType = UserType.INTERN_DEVELOPER;
	private long experienceCoefficient = 0L;

	public User(String displayName, String yugoslavianUMCN, UserType userType, long experienceCoefficient) {
		this();
		this.id = UUID.nameUUIDFromBytes(yugoslavianUMCN.getBytes(StandardCharsets.UTF_8)).toString();
		this.name = displayName.split(" ")[0];
		this.surname = displayName.split(" ")[1];
		this.yugoslavianUMCN = yugoslavianUMCN;
		this.placeOfBirth = getPlaceOfBirthBasedUMCNPoliticalRegionCode(Integer.parseInt(yugoslavianUMCN.substring(7, 9)));
		this.userType = userType;
		this.experienceCoefficient = experienceCoefficient;
	}

	public String getDisplayName() {
		return name.concat(" ").concat(surname);
	}
}
