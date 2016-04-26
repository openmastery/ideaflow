package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineSegment {

	private LocalDateTime start;
	private LocalDateTime end;

	private List<IdeaFlowBand> ideaFlowBands = new ArrayList<>();
	private List<TimeBandGroup> timeBandGroups = new ArrayList<>();

	public Duration getDuration() {
		Duration duration = TimeBand.sumDuration(ideaFlowBands);
		return duration.plus(TimeBand.sumDuration(timeBandGroups));
	}

	public void addTimeBand(TimeBand timeBand) {
		if (timeBand instanceof IdeaFlowBand) {
			ideaFlowBands.add((IdeaFlowBand) timeBand);
		} else if (timeBand instanceof TimeBandGroup) {
			timeBandGroups.add((TimeBandGroup) timeBand);
		} else {
			throw new RuntimeException("Unexpected time band=" + timeBand);
		}
	}

}
