package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.reflect.TypeToken;
import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.*;

import java.io.IOException;
import java.util.List;

//Below class was generated by ChatGPT :)
public class PredefinedTasksSerializer extends JsonSerializer<List<Epic>> {

    public static final String SELECTED_EPIC_DEVELOPMENT_TEAM = "selectedEpicDevelopmentTeam";

    @Override
    public void serialize(List<Epic> epics, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartArray();

        for (int i = 0; i < epics.size(); i++) {
            jsonGenerator.writeStartObject();
            Epic epic = epics.get(i);

            List<Developer> currentDevelopmentTeam = DataProvider.getCurrentDevelopmentTeamsSetup().stream().filter(team -> team.contains(epic.getAssignee())).findFirst().orElse(DataProvider.getCurrentDevelopmentTeamsSetup().getFirst());

            jsonGenerator.writeStringField("epicId", epic.getId());
            jsonGenerator.writeStringField("epicName", epic.getName());
            jsonGenerator.writeStringField("epicPriority", epic.getPriority().name());
            jsonGenerator.writeNumberField("epicReporter", getDeveloperIndex(currentDevelopmentTeam, epic.getReporter()));
            jsonGenerator.writeNumberField("epicAssignee", getDeveloperIndex(currentDevelopmentTeam, epic.getAssignee()));
            jsonGenerator.writeStringField("epicCreatedOn", epic.getCreatedOn().format(Utilities.DATE_TIME_FORMATTER));
            jsonGenerator.writeStringField("epicDescription", epic.getDescription());
            jsonGenerator.writeStringField(SELECTED_EPIC_DEVELOPMENT_TEAM, String.valueOf(DataProvider.getCurrentDevelopmentTeamsSetup().indexOf(currentDevelopmentTeam)));

            serializeUserStories(i, currentDevelopmentTeam, String.valueOf(DataProvider.getCurrentDevelopmentTeamsSetup().indexOf(currentDevelopmentTeam)), epic.getUserStories(), jsonGenerator);

            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    @Override
    public Class<List<Epic>> handledType() {
        TypeToken<List<Epic>> typeToken = new TypeToken<>() {
        };
        return (Class<List<Epic>>) typeToken.getRawType();
    }

    private void serializeUserStories(int epicIndex, List<Developer> currentDevelopmentTeam, String selectedEpicDevelopmentTeam, List<UserStory> userStories, JsonGenerator jsonGenerator)
            throws IOException {
        jsonGenerator.writeFieldName("userStories");
        jsonGenerator.writeStartArray();

        for (int i = 0; i < userStories.size(); i++) {
            serializeUserStory(epicIndex, i, currentDevelopmentTeam, selectedEpicDevelopmentTeam, userStories.get(i), jsonGenerator);
        }

        jsonGenerator.writeEndArray();
    }

    private void serializeUserStory(int epicIndex, int userStoryIndex, List<Developer> currentDevelopmentTeam, String selectedEpicDevelopmentTeam, UserStory userStory, JsonGenerator jsonGenerator)
            throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("userStoryId", userStory.getId());
        jsonGenerator.writeStringField("userStoryName", userStory.getName());
        jsonGenerator.writeStringField("userStoryPriority", userStory.getPriority().name());
        jsonGenerator.writeNumberField("userStoryReporter", getDeveloperIndex(currentDevelopmentTeam, userStory.getReporter()));
        jsonGenerator.writeNumberField("userStoryAssignee", getDeveloperIndex(currentDevelopmentTeam, userStory.getAssignee()));
        jsonGenerator.writeStringField("userStoryCreatedOn", userStory.getCreatedOn().format(Utilities.DATE_TIME_FORMATTER));
        jsonGenerator.writeStringField("userStoryDescription", userStory.getDescription());
        jsonGenerator.writeStringField(SELECTED_EPIC_DEVELOPMENT_TEAM, selectedEpicDevelopmentTeam);
        jsonGenerator.writeStringField("selectedEpicIndex", String.valueOf(epicIndex));

        serializeTechnicalTasks(epicIndex, userStoryIndex, currentDevelopmentTeam, selectedEpicDevelopmentTeam, userStory.getTechnicalTasks(), jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void serializeTechnicalTasks(int epicIndex, int userStoryIndex, List<Developer> currentDevelopmentTeam, String selectedEpicDevelopmentTeam, List<TechnicalTask> technicalTasks, JsonGenerator jsonGenerator)
            throws IOException {
        jsonGenerator.writeFieldName("technicalTasks");
        jsonGenerator.writeStartArray();

        for (TechnicalTask technicalTask : technicalTasks) {
            serializeTechnicalTask(epicIndex, userStoryIndex, currentDevelopmentTeam, selectedEpicDevelopmentTeam, technicalTask, jsonGenerator);
        }

        jsonGenerator.writeEndArray();
    }

    private void serializeTechnicalTask(int epicIndex, int userStoryIndex, List<Developer> currentDevelopmentTeam, String selectedEpicDevelopmentTeam, TechnicalTask technicalTask, JsonGenerator jsonGenerator)
            throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("technicalTaskId", technicalTask.getId());
        jsonGenerator.writeStringField("technicalTaskName", technicalTask.getName());
        jsonGenerator.writeStringField("technicalTaskPriority", technicalTask.getPriority().name());
        jsonGenerator.writeNumberField("technicalTaskReporter", getDeveloperIndex(currentDevelopmentTeam, technicalTask.getReporter()));
        jsonGenerator.writeNumberField("technicalTaskAssignee", getDeveloperIndex(currentDevelopmentTeam, technicalTask.getAssignee()));
        jsonGenerator.writeStringField("technicalTaskCreatedOn", technicalTask.getCreatedOn().format(Utilities.DATE_TIME_FORMATTER));
        jsonGenerator.writeStringField("technicalTaskDescription", technicalTask.getDescription());
        jsonGenerator.writeStringField(SELECTED_EPIC_DEVELOPMENT_TEAM, selectedEpicDevelopmentTeam);
        jsonGenerator.writeStringField("selectedEpicIndex", String.valueOf(epicIndex));
        jsonGenerator.writeStringField("selectedUserStoryIndex", String.valueOf(userStoryIndex));

        jsonGenerator.writeEndObject();
    }

    private int getDeveloperIndex(List<Developer> currentDevelopmentTeam, Developer developer) {
        return developer.getDeveloperType().equals(DeveloperType.TECHNICAL_MANAGER) ? -1 : currentDevelopmentTeam.indexOf(developer);
    }
}