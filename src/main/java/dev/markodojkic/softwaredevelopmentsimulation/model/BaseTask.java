package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BaseTask implements Serializable {
	@Serial
	private static final long serialVersionUID = -3427998889352799377L;

	private String id;
	private String name;
	private String description;
	private Priority priority;
	private User assignee;
	private User reporter;
}
