package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandComparator
import org.ideaflow.publisher.api.Timeline
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBand

import java.time.Duration

class RelativeTimeProcessor {

	public void setRelativeTime(Timeline timeline) {
		List<TimeBand> allTimeBands = getFlattenedSortedTimeBandList(timeline);
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

	private List<TimeBand> getFlattenedSortedTimeBandList(Timeline timeline) {
		ArrayList<TimeBand> allTimeBands = new ArrayList<>();
		for (TimelineSegment segment : timeline.timelineSegments) {
			addTimeBands(allTimeBands, segment.getAllTimeBands());
		}
		Collections.sort(allTimeBands, TimeBandComparator.INSTANCE);
		return allTimeBands;
	}

	private void addTimeBands(List<TimeBand> targetList, List<TimeBand> bandsToAdd) {
		for (TimeBand bandToAdd : bandsToAdd) {
			targetList.add(bandToAdd);
			addTimeBands(targetList, bandToAdd.getContainedBands());
		}
	}

}
