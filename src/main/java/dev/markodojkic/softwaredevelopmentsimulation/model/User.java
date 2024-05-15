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

	private String displayName;
	private String personalId;
	private UserType userType;
	private double experienceCoefficient;
}
