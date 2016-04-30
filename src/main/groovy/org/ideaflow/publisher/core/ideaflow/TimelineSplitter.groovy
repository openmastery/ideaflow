package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandComparator
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.event.EventEntity

class TimelineSplitter {

	List<TimelineSegment> splitTimelineSegment(TimelineSegment segment, List<EventEntity> subtaskEvents) {
		if (subtaskEvents.isEmpty()) {
			return [segment]
		}

		subtaskEvents = subtaskEvents.sort { EventEntity event -> event.position }

		List timeBands = segment.getAllTimeBands()
		Collections.sort(timeBands, TimeBandComparator.INSTANCE);
		List<TimelineSegment> splitSegments = []
		TimelineSegment activeSegment = new TimelineSegment()

		subtaskEvents.each { EventEntity subtask ->
			while (timeBands.isEmpty() == false) {
				TimeBand timeBand = timeBands.remove(0)
				if (timeBand.endsOnOrBefore(subtask.position)) {
					activeSegment.addTimeBand(timeBand)
				} else {
					if (timeBand.start == subtask.position) {
						// pop the band back on the stack for processing by the next event
						timeBands.add(0, timeBand)
					} else {
						TimeBand leftBand = timeBand.splitAndReturnLeftSide(subtask.position)
						TimeBand rightBand = timeBand.splitAndReturnRightSide(subtask.position)
						activeSegment.addTimeBand(leftBand)
						timeBands.add(0, rightBand)
					}
					break
				}
			}

			if (activeSegment.ideaFlowBands || activeSegment.timeBandGroups) {
				splitSegments << activeSegment
				activeSegment = new TimelineSegment()
			}
		}

		if (timeBands) {
			activeSegment = new TimelineSegment()
			timeBands.each { TimeBand timeBand ->
				activeSegment.addTimeBand(timeBand)
			}
			splitSegments << activeSegment
		}

		splitSegments
	}

}
