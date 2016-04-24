package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleActivityEntity

import java.time.Duration

class IdleTimeProcessor {

	public void collapseIdleTime(TimelineSegment timelineSegment, List<IdleActivityEntity> idleActivities) {
		for (IdleActivityEntity idle : idleActivities) {
			addIdleDuration(timelineSegment.ideaFlowBands, idle)
		}
	}

	private void addIdleDuration(List<IdeaFlowBand> timeBands, IdleActivityEntity idle) {
		for (IdeaFlowBand timeBand : timeBands) {
			Duration idleDuration = getIdleDurationForTimeBand(timeBand, idle)
			timeBand.idle = timeBand.idle.plus(idleDuration)
			addIdleDuration(timeBand.nestedBands, idle)
		}
	}

	// TODO: extract this to a class and add unit tests

	private Duration getIdleDurationForTimeBand(IdeaFlowBand timeBand, IdleActivityEntity idle) {
		Duration duration = Duration.ZERO
		if (isIdleStartAfterOrEqualToTimeBandStart(timeBand, idle)) {
			if (isIdleEndBeforeOrEqualToTimeBandEnd(timeBand, idle)) {
				// happy path
				duration = idle.duration
			} else if (idle.start.isBefore(timeBand.end)) {
				// idle overlaps end of time band
				duration = Duration.between(idle.start, timeBand.end)
			}
		} else if (isIdleEndBeforeOrEqualToTimeBandEnd(timeBand, idle)) {
			// idle overlaps start of time band
			duration = Duration.between(timeBand.start, idle.end)
		}
		duration
	}

	private boolean isIdleStartAfterOrEqualToTimeBandStart(IdeaFlowBand timeBand, IdleActivityEntity idle) {
		idle.start.isAfter(timeBand.start) || idle.start == timeBand.start
	}

	private boolean isIdleEndBeforeOrEqualToTimeBandEnd(IdeaFlowBand timeBand, IdleActivityEntity idle) {
		idle.end.isBefore(timeBand.end) || idle.end == timeBand.end
	}

}
