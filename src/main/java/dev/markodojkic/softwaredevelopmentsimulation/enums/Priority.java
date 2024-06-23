package dev.markodojkic.softwaredevelopmentsimulation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Priority {
    TRIVIAL(0,  7),
    NORMAL(1,  15),
    MINOR(2,  50),
    MAJOR(3,  37),
    CRITICAL(4,  124),
    BLOCKER(5,  160);

    private final int urgency;
    private final int ansiColorCode;
}