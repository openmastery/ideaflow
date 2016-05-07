package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.timeline.IdleTimeBand
import org.ideaflow.publisher.api.timeline.TimeBand
import org.ideaflow.publisher.api.timeline.TimeBandComparator
import org.ideaflow.publisher.api.timeline.Timeline
import org.ideaflow.publisher.api.timeline.TimelineSegment

import java.time.Duration

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
			addTimeBands(allTimeBands, segment.getAllTimeBands());
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

}
