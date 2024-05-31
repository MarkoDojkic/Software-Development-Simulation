package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.UserType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = -949472808846392995L;

	private String name = "";
	private String surname = "";
	private String personalId = "";
	private UserType userType = UserType.TECHNICAL_MANAGER;
	private long experienceCoefficient = 0L;

	public User(String displayName, String personalId, UserType userType, long experienceCoefficient) {
		this.name = displayName.split(" ")[0];
		this.surname = displayName.split(" ")[1];
		this.personalId = personalId;
		this.userType = userType;
		this.experienceCoefficient = experienceCoefficient;
	}

	public String getDisplayName() {
		return name.concat(" ").concat(surname);
	}
}
