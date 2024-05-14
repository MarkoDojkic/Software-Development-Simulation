package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import lombok.*;

import java.util.List;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserStory implements Serializable {
	@Serial
	private static final long serialVersionUID = -8638964085664554290L;

	private String name;
	private String description;
	private Priority priority;
	private Status status;
	private List<TechnicalTask> technicalTasks;
	private User assignee;
	private User reporter;
}
