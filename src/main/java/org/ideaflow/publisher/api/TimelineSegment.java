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
public class TimelineSegment {

	private LocalDateTime start;
	private LocalDateTime end;

	private List<IdeaFlowBand> ideaFlowBands;
	private List<IdeaFlowBandGroup> ideaFlowBandGroups;

	public Duration getDuration() {
		Duration duration = IdeaFlowBand.sumDuration(ideaFlowBands);
		return duration.plus(IdeaFlowBandGroup.sumDuration(ideaFlowBandGroups));
	}

}
