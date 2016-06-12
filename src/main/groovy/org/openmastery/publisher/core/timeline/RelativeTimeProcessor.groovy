package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.core.event.EventModel

import java.time.Duration
import java.time.LocalDateTime

class RelativeTimeProcessor {

	public void computeRelativeTime(List<BandTimelineSegment> segments) {
		Set<TimeBandModel> allTimeBands = getFlattenedSortedTimeBandSet(segments);
		TimeBandModel previousTimeBand = null
		long relativeTime = 0

		for (int i = 0; i < allTimeBands.size(); i++) {
			TimeBandModel timeBand = allTimeBands[i]

			if (previousTimeBand != null) {
				Duration duration
				if (previousTimeBand instanceof IdleTimeBandModel) {
					duration = Duration.between(previousTimeBand.end, timeBand.start)
				} else {
					duration = Duration.between(previousTimeBand.start, timeBand.start)
				}
				if (duration.isNegative() == false) {
					relativeTime += duration.seconds
				}
			}

			timeBand.relativePositionInSeconds = relativeTime
			previousTimeBand = timeBand
		}
	}

	private Set<TimeBandModel> getFlattenedSortedTimeBandSet(List<BandTimelineSegment> segments) {
		ArrayList<TimeBandModel> allTimeBands = new ArrayList<>();
		for (BandTimelineSegment segment : segments) {
			addTimeBands(allTimeBands, segment.allTimeBands)
			addEventAdapters(allTimeBands, segment.events)
		}
		Collections.sort(allTimeBands, TimeBandComparator.INSTANCE);
		// convert to a set b/c we could have duplicate idle bands (e.g. if idle is w/in nested conflict)
		return allTimeBands as Set;
	}

	private void addTimeBands(List<TimeBandModel> targetList, List<TimeBandModel> bandsToAdd) {
		for (TimeBandModel bandToAdd : bandsToAdd) {
			targetList.add(bandToAdd);
			addTimeBands(targetList, bandToAdd.getContainedBands());
		}
	}

	private void addEventAdapters(List<TimeBandModel> targetList, List<EventModel> eventsToAdd) {
		for (EventModel eventToAdd : eventsToAdd) {
			targetList.add(new EventTimeBandAdapter(eventToAdd))
		}
	}


	private static class EventTimeBandAdapter extends TimeBandModel<EventTimeBandAdapter> {

		private EventModel event

		EventTimeBandAdapter(EventModel event) {
			this.event = event
		}

		public void setRelativePositionInSeconds(long relativePositionInSeconds) {
			event.relativePositionInSeconds = relativePositionInSeconds
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
		List<? extends TimeBandModel> getContainedBands() {
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
