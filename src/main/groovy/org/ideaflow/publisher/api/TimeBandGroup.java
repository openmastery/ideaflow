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
	public TimeBandGroup splitAndReturnLeftSide(LocalDateTime position) {
		if (startsOnOrAfter(position)) {
			return null;
		} else if (endsOnOrBefore(position)) {
			return this;
		} else {
			List<TimeBand> splitLinkedBands = new ArrayList<>();
			for (TimeBand linkedBand : linkedTimeBands) {
				TimeBand splitLinkedBand = linkedBand.splitAndReturnLeftSide(position);
				if (splitLinkedBand != null) {
					splitLinkedBands.add(splitLinkedBand);
				}
			}

			return TimeBandGroup.builder()
					.id(id)
					.linkedTimeBands(splitLinkedBands)
					.build();
		}
	}

	@Override
	public TimeBandGroup splitAndReturnRightSide(LocalDateTime position) {
		if (endsOnOrBefore(position)) {
			return null;
		} else if (startsOnOrAfter(position)) {
			return this;
		} else {
			List<TimeBand> splitLinkedBands = new ArrayList<>();
			for (TimeBand linkedBand : linkedTimeBands) {
				TimeBand splitLinkedBand = linkedBand.splitAndReturnRightSide(position);
				if (splitLinkedBand != null) {
					splitLinkedBands.add(splitLinkedBand);
				}
			}

			return TimeBandGroup.builder()
					.id(id)
					.linkedTimeBands(splitLinkedBands)
					.build();
		}
	}

}

//conflict <- rework | nested conflict | nested conflict | end rework

//group comment is first comment in the grouping.
//group contains conflict, rework with nested conflicts

//subtask in the middle of a timeband, need to split the band.