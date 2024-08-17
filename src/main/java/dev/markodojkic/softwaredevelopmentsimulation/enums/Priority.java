package dev.markodojkic.softwaredevelopmentsimulation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Priority {
    TRIVIAL(2.45 , 0, 7),
    NORMAL(4.00, 1, 15),
    MINOR(5.78, 2, 50),
    MAJOR(7.13578, 3, 37),
    CRITICAL(11.1545, 4, 124),
    BLOCKER(14.1216, 5, 160);

    private final double resolutionTimeCoefficient;
    private final int urgency;
    private final int ansiColorCode;
}