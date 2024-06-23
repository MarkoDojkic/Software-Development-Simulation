package dev.markodojkic.softwaredevelopmentsimulation.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TechnicalTask extends BaseTask {
	@Serial
	private static final long serialVersionUID = 6697684717367472299L;

	private String userStoryId;
}