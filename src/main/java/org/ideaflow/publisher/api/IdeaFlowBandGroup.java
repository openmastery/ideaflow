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
public class IdeaFlowBandGroup {

	private long id;

	private List<IdeaFlowBand> linkedIdeaFlowBands;

	public void addLinkedTimeBand(IdeaFlowBand linkedIdeaFlowBand) {
		linkedIdeaFlowBands.add(linkedIdeaFlowBand);
	}

	public LocalDateTime getStart() {
		return linkedIdeaFlowBands.get(0).getStart();
	}

	public LocalDateTime getEnd() {
		return linkedIdeaFlowBands.get(linkedIdeaFlowBands.size() - 1).getEnd();
	}

	public Duration getDuration() {
		return IdeaFlowBand.sumDuration(linkedIdeaFlowBands);
	}

	public static Duration sumDuration(List<IdeaFlowBandGroup> ideaFlowBandGroups) {
		Duration duration = Duration.ZERO;
		for (IdeaFlowBandGroup timeBand : ideaFlowBandGroups) {
			duration = duration.plus(timeBand.getDuration());
		}
		return duration;
	}

}

//conflict <- rework | nested conflict | nested conflict | end rework

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.