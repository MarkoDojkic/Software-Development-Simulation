package dev.markodojkic.softwaredevelopmentsimulation.model;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.Status;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TechnicalTask implements Serializable {
	@Serial
	private static final long serialVersionUID = 6697684717367472299L;

	private String name;
	private String description;
	private Priority priority;
	private Status status;
	private UserStory userStory;
	private User assignee;
	private User reporter;
}
