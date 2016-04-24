package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandGroup
import org.ideaflow.publisher.api.TimelineSegment

import java.time.Duration
import java.time.LocalDateTime


class TimelineSegmentFactory {

	// TODO: refactor... AAHHHHH!!!!!

	public TimelineSegment createTimelineSegment(List<IdeaFlowStateEntity> ideaFlowStates) {
		ideaFlowStates = new ArrayList<>(ideaFlowStates);
		Collections.sort(ideaFlowStates)

		TimeBand previousBand = null;
		TimeBandGroup activeTimeBandGroup = null;
		ArrayList<TimeBand> timeBands = new ArrayList<>();
		ArrayList<TimeBandGroup> timeBandGroups = new ArrayList<>();
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			TimeBand timeBand = TimeBand.builder()
					.type(state.type)
					.start(state.start)
					.end(state.end)
					.idle(Duration.ZERO)
					.nestedBands(new ArrayList<TimeBand>())
					.build()

			if (state.isNested()) {
				previousBand.addNestedBand(timeBand)
			} else {
				if (state.isLinkedToPrevious() && (timeBands.isEmpty() == false)) {
					if (activeTimeBandGroup == null) {
						activeTimeBandGroup = TimeBandGroup.builder()
								.linkedTimeBands(new ArrayList<TimeBand>())
								.build()

						TimeBand firstBandInGroup = timeBands.remove(timeBands.size() - 1)
						activeTimeBandGroup.addLinkedTimeBand(firstBandInGroup)
						timeBandGroups.add(activeTimeBandGroup)
					}

					activeTimeBandGroup.addLinkedTimeBand(timeBand)
				} else {
					activeTimeBandGroup = null
					timeBands.add(timeBand)
				}

				if (previousBand != null) {
					if (previousBand.end.isAfter(timeBand.start)) {
						previousBand.end = timeBand.start
					}
				}

				previousBand = timeBand
			}
		}

		LocalDateTime segmentStart = ideaFlowStates.first().start
		LocalDateTime segmentEnd = ideaFlowStates.last().end

		TimelineSegment segment = TimelineSegment.builder()
				.start(segmentStart)
				.end(segmentEnd)
				.timeBands(timeBands)
				.timeBandGroups(timeBandGroups)
				.build();

		return segment;
	}

}
