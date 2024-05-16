package dev.markodojkic.softwaredevelopmentsimulation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Priority {
    TRIVIAL(0, 1.00),
    NORMAL(1, 2.75),
    MINOR(2, 4.35),
    MAJOR(3, 6.45),
    CRITICAL(4, 7.15),
    BLOCKER(5, 8.00);

    private final int urgency;
    private final double typicalResolutionTimeCoefficient;
}
