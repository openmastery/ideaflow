package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.event.Event
import org.ideaflow.publisher.api.timeline.IdleTimeBand
import org.ideaflow.publisher.api.timeline.TimeBand
import org.ideaflow.publisher.api.timeline.TimeBandComparator
import org.ideaflow.publisher.api.timeline.Timeline
import org.ideaflow.publisher.api.timeline.TimelineSegment

import java.time.Duration
import java.time.LocalDateTime

class RelativeTimeProcessor {

	public void setRelativeTime(Timeline timeline) {
		Set<TimeBand> allTimeBands = getFlattenedSortedTimeBandSet(timeline);
		TimeBand previousTimeBand = null
		long relativeTime = 0

		for (int i = 0; i < allTimeBands.size(); i++) {
			TimeBand timeBand = allTimeBands[i]

			if (previousTimeBand != null) {
				Duration duration
				if (previousTimeBand instanceof IdleTimeBand) {
					duration = Duration.between(previousTimeBand.end, timeBand.start)
				} else {
					duration = Duration.between(previousTimeBand.start, timeBand.start)
				}
				if (duration.isNegative() == false) {
					relativeTime += duration.seconds
				}
			}

			timeBand.relativeStart = relativeTime
			previousTimeBand = timeBand
		}
	}

	private Set<TimeBand> getFlattenedSortedTimeBandSet(Timeline timeline) {
		ArrayList<TimeBand> allTimeBands = new ArrayList<>();
		for (TimelineSegment segment : timeline.timelineSegments) {
			addTimeBands(allTimeBands, segment.allTimeBands)
			addEventAdapters(allTimeBands, segment.events)
		}
		Collections.sort(allTimeBands, TimeBandComparator.INSTANCE);
		// convert to a set b/c we could have duplicate idle bands (e.g. if idle is w/in nested conflict)
		return allTimeBands as Set;
	}

	private void addTimeBands(List<TimeBand> targetList, List<TimeBand> bandsToAdd) {
		for (TimeBand bandToAdd : bandsToAdd) {
			targetList.add(bandToAdd);
			addTimeBands(targetList, bandToAdd.getContainedBands());
		}
	}

	private void addEventAdapters(List<TimeBand> targetList, List<Event> eventsToAdd) {
		for (Event eventToAdd : eventsToAdd) {
			targetList.add(new EventTimeBandAdapter(eventToAdd))
		}
	}


	private static class EventTimeBandAdapter extends TimeBand<EventTimeBandAdapter> {

		private Event event

		EventTimeBandAdapter(Event event) {
			this.event = event
		}

		public void setRelativeStart(long relativeStart) {
			event.relativeStart = relativeStart
		}

		@Override
		LocalDateTime getStart() {
			event.position
		}

		@Override
		LocalDateTime getEnd() {
			event.position
		}

		@Override
		Duration getDuration() {
			Duration.ZERO
		}

		@Override
		List<? extends TimeBand> getContainedBands() {
			return []
		}

		@Override
		protected EventTimeBandAdapter internalSplitAndReturnLeftSide(LocalDateTime position) {
			this
		}

		@Override
		protected EventTimeBandAdapter internalSplitAndReturnRightSide(LocalDateTime position) {
			this
		}
	}

}
