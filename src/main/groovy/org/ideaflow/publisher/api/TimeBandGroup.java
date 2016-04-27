package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TimeBandGroup extends TimeBand<TimeBandGroup> {

	private long id;

	private List<TimeBand> linkedTimeBands;

	public void addLinkedTimeBand(IdeaFlowBand linkedIdeaFlowBand) {
		linkedTimeBands.add(linkedIdeaFlowBand);
	}

	public LocalDateTime getStart() {
		return linkedTimeBands.get(0).getStart();
	}

	public LocalDateTime getEnd() {
		return linkedTimeBands.get(linkedTimeBands.size() - 1).getEnd();
	}

	public Duration getDuration() {
		return TimeBand.sumDuration(linkedTimeBands);
	}

	@Override
	protected TimeBandGroup internalSplitAndReturnLeftSide(LocalDateTime position) {
		List<TimeBand> splitLinkedBands = TimeBand.splitAndReturnLeftSide(linkedTimeBands, position);
		return TimeBandGroup.builder()
				.id(id)
				.linkedTimeBands(splitLinkedBands)
				.build();
	}

	@Override
	protected TimeBandGroup internalSplitAndReturnRightSide(LocalDateTime position) {
		List<TimeBand> splitLinkedBands = TimeBand.splitAndReturnRightSide(linkedTimeBands, position);
		return TimeBandGroup.builder()
				.id(id)
				.linkedTimeBands(splitLinkedBands)
				.build();
	}

}

//conflict <- rework | nested conflict | nested conflict | end rework

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.