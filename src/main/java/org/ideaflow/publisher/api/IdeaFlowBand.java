package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowBand {

	private long id;

	private LocalDateTime start;
	private LocalDateTime end;

	private IdeaFlowStateType type;

	private Duration idle;

	private List<IdeaFlowBand> nestedBands;

	public void addNestedBand(IdeaFlowBand ideaFlowBand) {
		nestedBands.add(ideaFlowBand);
	}

	public Duration getDuration() {
		return Duration.between(start, end).minus(idle);
	}

	public static Duration sumDuration(List<IdeaFlowBand> ideaFlowBands) {
		Duration duration = Duration.ZERO;
		for (IdeaFlowBand ideaFlowBand : ideaFlowBands) {
			duration = duration.plus(ideaFlowBand.getDuration());
		}
		return duration;
	}

}


