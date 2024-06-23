package dev.markodojkic.softwaredevelopmentsimulation.util;

import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;

import java.io.Serial;

public class EpicNotDoneException extends Exception {
    @Serial
    private static final long serialVersionUID = 7718828512143293557L;

    public <T extends Epic> EpicNotDoneException(T epic) {
        super(String.format("Epic '%s' still has in-progress user stories", epic.getName()));
    }
}
