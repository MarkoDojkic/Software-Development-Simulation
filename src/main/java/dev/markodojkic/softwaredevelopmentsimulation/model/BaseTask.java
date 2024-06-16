package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BaseTask implements Serializable {
	@Serial
	private static final long serialVersionUID = -3427998889352799377L;

	private String id;
	private String name;
	private String description;
	private Priority priority;
	private Developer assignee;
	private Developer reporter;
	private ZonedDateTime createdOn;

	@Override
	public String toString() {
		return "BaseTask{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", priority=" + priority +
				", assignee='" + assignee.getDisplayName() +
				"', reporter='" + reporter.getDisplayName() +
				"', createdOn=" + createdOn +
				'}';
	}
}
