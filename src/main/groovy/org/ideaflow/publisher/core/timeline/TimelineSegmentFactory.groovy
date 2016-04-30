package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.IdleTimeBand
import org.ideaflow.publisher.api.TimeBandGroup
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity

import java.time.LocalDateTime


class TimelineSegmentFactory {

	// TODO: refactor... AAHHHHH!!!!!

	public TimelineSegment createTimelineSegment(List<IdeaFlowStateEntity> ideaFlowStates) {
		ideaFlowStates = new ArrayList<>(ideaFlowStates);
		Collections.sort(ideaFlowStates)

		IdeaFlowBand previousBand = null;
		TimeBandGroup activeTimeBandGroup = null;
		ArrayList<IdeaFlowBand> ideaFlowBands = new ArrayList<>();
		ArrayList<TimeBandGroup> ideaFlowBandGroups = new ArrayList<>();
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			IdeaFlowBand timeBand = IdeaFlowBand.builder()
					.type(state.type)
					.start(state.start)
					.end(state.end)
					.idleBands(new ArrayList<IdleTimeBand>())
					.nestedBands(new ArrayList<IdeaFlowBand>())
					.build()

			if (state.isNested()) {
				previousBand.addNestedBand(timeBand)
			} else {
				if (state.isLinkedToPrevious() && (ideaFlowBands.isEmpty() == false)) {
					if (activeTimeBandGroup == null) {
						activeTimeBandGroup = TimeBandGroup.builder()
								.linkedTimeBands(new ArrayList<IdeaFlowBand>())
								.build()

						IdeaFlowBand firstBandInGroup = ideaFlowBands.remove(ideaFlowBands.size() - 1)
						activeTimeBandGroup.addLinkedTimeBand(firstBandInGroup)
						ideaFlowBandGroups.add(activeTimeBandGroup)
					}

					activeTimeBandGroup.addLinkedTimeBand(timeBand)
				} else {
					activeTimeBandGroup = null
					ideaFlowBands.add(timeBand)
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
				.ideaFlowBands(ideaFlowBands)
				.timeBandGroups(ideaFlowBandGroups)
				.build();

		return segment;
	}

}
