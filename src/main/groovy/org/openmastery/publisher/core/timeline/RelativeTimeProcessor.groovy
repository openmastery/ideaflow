package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.core.Positionable
import org.openmastery.publisher.core.PositionableComparator
import org.openmastery.publisher.core.event.EventModel

import java.time.Duration

class RelativeTimeProcessor {

	public void computeRelativeTime(List<BandTimelineSegment> segments) {
		Set<Positionable> allSegmentPositionables = getFlattenedSortedSegmentContentSet(segments);
		Positionable previousPositionable = null
		long relativeTime = 0

		for (int i = 0; i < allSegmentPositionables.size(); i++) {
			Positionable positionable = allSegmentPositionables[i]

			if (previousPositionable != null) {
				Duration duration
				if (previousPositionable instanceof IdleTimeBandModel) {
					duration = Duration.between(previousPositionable.end, positionable.getPosition())
				} else {
					duration = Duration.between(previousPositionable.getPosition(), positionable.getPosition())
				}
				if (duration.isNegative() == false) {
					relativeTime += duration.seconds
				}
			}

			positionable.relativePositionInSeconds = relativeTime
			previousPositionable = positionable
		}
	}

	private Set<Positionable> getFlattenedSortedSegmentContentSet(List<BandTimelineSegment> segments) {
		ArrayList<Positionable> allTimeBands = new ArrayList<>();
		for (BandTimelineSegment segment : segments) {
			addTimeBands(allTimeBands, segment.allTimeBands)
			addEvents(allTimeBands, segment.events)
		}
		Collections.sort(allTimeBands, PositionableComparator.INSTANCE);
		// convert to a set b/c we could have duplicate idle bands (e.g. if idle is w/in nested conflict)
		return allTimeBands as Set;
	}

	private void addTimeBands(List<Positionable> targetList, List<TimeBandModel> bandsToAdd) {
		for (TimeBandModel bandToAdd : bandsToAdd) {
			targetList.add(bandToAdd);
			addTimeBands(targetList, bandToAdd.getContainedBands());
		}
	}

	private void addEvents(List<Positionable> targetList, List<EventModel> eventsToAdd) {
		for (EventModel eventToAdd : eventsToAdd) {
			targetList.add(eventToAdd)
		}
	}

}
