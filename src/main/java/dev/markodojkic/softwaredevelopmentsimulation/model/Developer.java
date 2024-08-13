package dev.markodojkic.softwaredevelopmentsimulation.model;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.generateRandomYugoslavianUMCN;
import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.getPlaceOfBirthBasedUMCNPoliticalRegionCode;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"isFemale", "displayName"})
public class Developer implements Serializable {
	@Serial
	private static final long serialVersionUID = -949472808846392995L;

	private String id;
	private String name;
	private String surname;
	private String yugoslavianUMCN;
	private String placeOfBirth;
	private DeveloperType developerType = DeveloperType.INTERN_DEVELOPER;
	private long experienceCoefficient;
	private boolean isFemale;
	@JsonIgnore
	private String displayName;

	public Developer(String id, String name, String surname, String yugoslavianUMCN, String placeOfBirth, DeveloperType developerType, long experienceCoefficient, boolean isFemale) {
		this.name = name;
		this.surname = surname;
		this.yugoslavianUMCN = StringUtil.isNullOrEmpty(yugoslavianUMCN) ? generateRandomYugoslavianUMCN(isFemale, false) : yugoslavianUMCN;
		this.id = StringUtil.isNullOrEmpty(id) ? UUID.nameUUIDFromBytes(this.yugoslavianUMCN.getBytes(StandardCharsets.UTF_8)).toString() : id;
		this.placeOfBirth = StringUtil.isNullOrEmpty(placeOfBirth) ? getPlaceOfBirthBasedUMCNPoliticalRegionCode(Integer.parseInt(this.yugoslavianUMCN.substring(7, 9))) : placeOfBirth;
		this.developerType = developerType;
		this.experienceCoefficient = experienceCoefficient;
		this.isFemale = isFemale;
	}

	public Developer(String displayName, String yugoslavianUMCN, DeveloperType developerType, boolean isFemale, long experienceCoefficient) {
		this(null, displayName.split(" ")[0], displayName.split(" ")[1], yugoslavianUMCN, null, developerType, experienceCoefficient, isFemale);
	}

	public String getDisplayName() {
		return name.concat(" ").concat(surname);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		else if (!(o instanceof Developer developer)) return false;
        else return Objects.equals(getId(), developer.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}
}