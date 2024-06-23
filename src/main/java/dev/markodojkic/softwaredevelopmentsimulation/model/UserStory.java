package dev.markodojkic.softwaredevelopmentsimulation.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "technicalTasks")
@EqualsAndHashCode(callSuper = true)
public class UserStory extends BaseTask {
	@Serial
	private static final long serialVersionUID = -8638964085664554290L;

	private String epicId;
	private List<TechnicalTask> technicalTasks;
}
