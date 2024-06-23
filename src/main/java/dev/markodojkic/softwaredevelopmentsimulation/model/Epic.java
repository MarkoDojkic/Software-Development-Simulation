package dev.markodojkic.softwaredevelopmentsimulation.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;


@SuperBuilder
@Getter
@Setter
@ToString(callSuper = true, exclude = "userStories")
@EqualsAndHashCode(callSuper = true)
public class Epic extends BaseTask {
	@Serial
	private static final long serialVersionUID = 4347765096760107156L;

	private List<UserStory> userStories;
}
