package dev.markodojkic.softwaredevelopmentsimulation.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import dev.markodojkic.softwaredevelopmentsimulation.model.Epic;
import dev.markodojkic.softwaredevelopmentsimulation.model.UserStory;
import dev.markodojkic.softwaredevelopmentsimulation.model.TechnicalTask;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

//Below class was generated by ChatGPT :)
public class PredefinedTasksDeserializer extends JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode epicNode = objectMapper.readTree(jsonParser);

        List<Developer> currentDevelopmentTeam = DataProvider.getCurrentDevelopmentTeamsSetup().get(epicNode.get("selectedEpicDevelopmentTeam").asInt());

        // Extract fields for Epic
        String epicId = epicNode.get("epicId").asText();
        String epicName = epicNode.get("epicName").asText();
        String epicPriority = epicNode.get("epicPriority").asText();
        int epicReporterIndex = epicNode.get("epicReporter").asInt();
        int epicAssigneeIndex = epicNode.get("epicAssignee").asInt();
        String epicCreatedOn = epicNode.get("epicCreatedOn").asText();
        String epicDescription = epicNode.get("epicDescription").asText();

        // Deserialize user stories
        List<UserStory> userStories = new ArrayList<>();
        JsonNode userStoriesNode = epicNode.get("userStories");
        for (JsonNode userStoryNode : userStoriesNode) {
            UserStory userStory = deserializeUserStory(currentDevelopmentTeam, epicId, userStoryNode);
            userStories.add(userStory);
        }

        return Epic.builder()
                .id(epicId)
                .name(epicName)
                .priority(Priority.valueOf(epicPriority))
                .reporter(epicReporterIndex == -1 ? DataProvider.getTechnicalManager() : currentDevelopmentTeam.get(epicReporterIndex))
                .assignee(currentDevelopmentTeam.get(epicAssigneeIndex))
                .createdOn(ZonedDateTime.of(LocalDateTime.parse(epicCreatedOn, Utilities.DATE_TIME_FORMATTER), ZoneId.systemDefault()))
                .description(epicDescription)
                .userStories(userStories)
                .build();
    }

    private UserStory deserializeUserStory(List<Developer> currentDevelopmentTeam, String epicId, JsonNode userStoryNode) {
        // Extract fields for UserStory
        String userStoryId = userStoryNode.get("userStoryId").asText();
        String userStoryName = userStoryNode.get("userStoryName").asText();
        String userStoryPriority = userStoryNode.get("userStoryPriority").asText();
        int userStoryReporter = userStoryNode.get("userStoryReporter").asInt();
        int userStoryAssignee = userStoryNode.get("userStoryAssignee").asInt();
        String userStoryCreatedOn = userStoryNode.get("userStoryCreatedOn").asText();
        String userStoryDescription = userStoryNode.get("userStoryDescription").asText();

        // Deserialize technical tasks
        List<TechnicalTask> technicalTasks = new ArrayList<>();
        JsonNode technicalTasksNode = userStoryNode.get("technicalTasks");
        for (JsonNode technicalTaskNode : technicalTasksNode) {
            TechnicalTask technicalTask = deserializeTechnicalTask(currentDevelopmentTeam, userStoryId, technicalTaskNode);
            technicalTasks.add(technicalTask);
        }

        return UserStory.builder()
                .id(userStoryId)
                .epicId(epicId)
                .name(userStoryName)
                .priority(Priority.valueOf(userStoryPriority))
                .reporter(currentDevelopmentTeam.get(userStoryReporter))
                .assignee(currentDevelopmentTeam.get(userStoryAssignee))
                .createdOn(ZonedDateTime.of(LocalDateTime.parse(userStoryCreatedOn, Utilities.DATE_TIME_FORMATTER), ZoneId.systemDefault()))
                .description(userStoryDescription)
                .technicalTasks(technicalTasks)
                .build();
    }

    private TechnicalTask deserializeTechnicalTask(List<Developer> currentDevelopmentTeam, String userStoryId, JsonNode technicalTaskNode) {
        // Extract fields for TechnicalTask
        String technicalTaskId = technicalTaskNode.get("technicalTaskId").asText();
        String technicalTaskName = technicalTaskNode.get("technicalTaskName").asText();
        String technicalTaskPriority = technicalTaskNode.get("technicalTaskPriority").asText();
        int technicalTaskReporter = technicalTaskNode.get("technicalTaskReporter").asInt();
        int technicalTaskAssignee = technicalTaskNode.get("technicalTaskAssignee").asInt();
        String technicalTaskCreatedOn = technicalTaskNode.get("technicalTaskCreatedOn").asText();
        String technicalTaskDescription = technicalTaskNode.get("technicalTaskDescription").asText();

        return TechnicalTask.builder()
                .id(technicalTaskId)
                .userStoryId(userStoryId)
                .name(technicalTaskName)
                .priority(Priority.valueOf(technicalTaskPriority))
                .reporter(currentDevelopmentTeam.get(technicalTaskReporter))
                .assignee(currentDevelopmentTeam.get(technicalTaskAssignee))
                .createdOn(ZonedDateTime.of(LocalDateTime.parse(technicalTaskCreatedOn, Utilities.DATE_TIME_FORMATTER), ZoneId.systemDefault()))
                .description(technicalTaskDescription)
                .build();
    }
}
