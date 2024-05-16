package dev.markodojkic.softwaredevelopmentsimulation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserType {
    INTERN_DEVELOPER(1.00),
    JUNIOR_DEVELOPER(2.25),
    ADVANCED_JUNIOR_DEVELOPER(4.30),
    MEDIOCRE_DEVELOPER(5.15),
    ADVANCED_MEDIOCRE_DEVELOPER(6.30),
    SENIOR_DEVELOPER(7.35);

    private final double seniorityCoefficient;
}
