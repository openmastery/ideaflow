package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.event.EventEntity

class TimelineSplitter {

	List<TimelineSegment> splitTimelineSegment(TimelineSegment segment, List<EventEntity> subtaskEvents) {
		if (subtaskEvents.isEmpty()) {
			return [segment]
		}

		subtaskEvents = subtaskEvents.sort { EventEntity event -> event.position }

		List ideaFlowBands = segment.ideaFlowBands
		List ideaFlowBandGroups = segment.timeBandGroups
		List<TimelineSegment> splitSegments = []
		TimelineSegment activeSegment = new TimelineSegment()

		subtaskEvents.each { EventEntity subtask ->
			while (ideaFlowBands.isEmpty() == false) {
				IdeaFlowBand ideaFlowBand = ideaFlowBands.remove(0)
				if (ideaFlowBand.endsOnOrBefore(subtask.position)) {
					activeSegment.addTimeBand(ideaFlowBand)
				} else {
					if (ideaFlowBand.start == subtask.position) {
						// pop the band back on the stack for processing by the next event
						ideaFlowBands.add(0, ideaFlowBand)
					} else {
						IdeaFlowBand leftBand = ideaFlowBand.splitAndReturnLeftSide(subtask.position)
						IdeaFlowBand rightBand = ideaFlowBand.splitAndReturnRightSide(subtask.position)
						activeSegment.addTimeBand(leftBand)
						ideaFlowBands.add(0, rightBand)
					}
					break
				}
			}

			List activeIdeaFlowBandGroups = []
			// TODO: band groups

			if (activeSegment.ideaFlowBands || activeSegment.timeBandGroups) {
				splitSegments << activeSegment
				activeSegment = new TimelineSegment()
			}
		}

		if (ideaFlowBands || ideaFlowBandGroups) {
			splitSegments << TimelineSegment.builder()
					.ideaFlowBands(ideaFlowBands)
					.timeBandGroups(ideaFlowBandGroups)
					.build()
		}

		splitSegments
	}

}
