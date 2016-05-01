package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.Event
import org.ideaflow.publisher.api.EventType
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandComparator
import org.ideaflow.publisher.api.TimelineSegment

class TimelineSplitter {

	List<TimelineSegment> splitTimelineSegment(TimelineSegment segment) {
		boolean hasSubtask = segment.events.find { it.eventType == EventType.SUBTASK }
		if (hasSubtask == false) {
			return [segment]
		}

		List timeBands = segment.getAllTimeBands()
		Collections.sort(timeBands, TimeBandComparator.INSTANCE);
		List<TimelineSegment> splitSegments = []
		TimelineSegment activeSegment = new TimelineSegment()
		activeSegment.description = segment.description

		List<Event> sortedEvents = segment.events.sort { Event event -> event.position }
		sortedEvents.each { Event event ->
			if ((event.eventType == EventType.SUBTASK) == false) {
				activeSegment.addEvent(event)
				return
			}

			while (timeBands.isEmpty() == false) {
				TimeBand timeBand = timeBands.remove(0)
				if (timeBand.endsOnOrBefore(event.position)) {
					activeSegment.addTimeBand(timeBand)
				} else {
					if (timeBand.start == event.position) {
						// pop the band back on the stack for processing by the next event
						timeBands.add(0, timeBand)
					} else {
						TimeBand leftBand = timeBand.splitAndReturnLeftSide(event.position)
						TimeBand rightBand = timeBand.splitAndReturnRightSide(event.position)
						activeSegment.addTimeBand(leftBand)
						timeBands.add(0, rightBand)
					}
					break
				}
			}

			if (activeSegment.ideaFlowBands || activeSegment.timeBandGroups) {
				splitSegments << activeSegment
				activeSegment = new TimelineSegment()
				activeSegment.addEvent(event)
				activeSegment.description = event.comment
			}
		}

		if (timeBands) {
			timeBands.each { TimeBand timeBand ->
				activeSegment.addTimeBand(timeBand)
			}
			splitSegments << activeSegment
		}

		splitSegments
	}

}
