package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Epic implements Serializable {
	@Serial
	private static final long serialVersionUID = 4347765096760107156L;

	private String name;
	private String description;
	private Priority priority;
	private Status status;
	private List<UserStory> userStories;
	private User assignee;
	private User reporter;
}
