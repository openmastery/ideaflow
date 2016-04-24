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
public class TimeBand {

	private long id;

	private LocalDateTime start;
	private LocalDateTime end;

	private IdeaFlowStateType type;

	private Duration idle;

	private List<TimeBand> nestedBands;

	public void addNestedBand(TimeBand timeBand) {
		nestedBands.add(timeBand);
	}

	public Duration getDuration() {
		return Duration.between(start, end).minus(idle);
	}

	public static Duration sumDuration(List<TimeBand> timeBands) {
		Duration duration = Duration.ZERO;
		for (TimeBand timeBand : timeBands) {
			duration = duration.plus(timeBand.getDuration());
		}
		return duration;
	}

}


