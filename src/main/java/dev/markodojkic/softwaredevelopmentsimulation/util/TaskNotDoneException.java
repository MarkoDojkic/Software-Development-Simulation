package dev.markodojkic.softwaredevelopmentsimulation.util;

import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;

import java.io.Serial;

public class TaskNotDoneException extends Exception {
    @Serial
    private static final long serialVersionUID = 7718828512143293558L;

    public <T extends BaseTask> TaskNotDoneException(T task) {
        super(String.format(task instanceof Epic ? "Epic '%s' still has in-progress user stories" : task instanceof UserStory ? "User story '%s' still has in-progress technical tasks" : "Technical task is not done yet", task.getName()));
    }
}
