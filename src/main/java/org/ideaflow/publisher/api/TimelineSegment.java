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
	private List<IdeaFlowBandGroup> ideaFlowBandGroups = new ArrayList<>();

	public Duration getDuration() {
		Duration duration = IdeaFlowBand.sumDuration(ideaFlowBands);
		return duration.plus(IdeaFlowBandGroup.sumDuration(ideaFlowBandGroups));
	}

	public void addTimeBand(TimeBand timeBand) {
		if (timeBand instanceof IdeaFlowBand) {
			ideaFlowBands.add((IdeaFlowBand) timeBand);
		} else if (timeBand instanceof IdeaFlowBandGroup) {
			ideaFlowBandGroups.add((IdeaFlowBandGroup) timeBand);
		} else {
			throw new RuntimeException("Unexpected time band=" + timeBand);
		}
	}

}
