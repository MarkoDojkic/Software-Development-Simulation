package dev.markodojkic.softwaredevelopmentsimulation.util;

import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;

import java.io.Serial;

public class UserStoryNotDoneException extends Exception {
    @Serial
    private static final long serialVersionUID = 7718828512143293558L;

    public <T extends UserStory> UserStoryNotDoneException(T userStory) {
        super(String.format("User story '%s' still has in-progress technical tasks", userStory.getName()));
    }
}
