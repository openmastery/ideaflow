package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBand

import java.time.Duration

class IdleTimeProcessor {

	private TimeBandCalculator timeBandCalculator = new TimeBandCalculator()

	public void collapseIdleTime(TimelineSegment timelineSegment, List<IdleTimeBand> idleActivities) {
		for (IdleTimeBand idle : idleActivities) {
			addIdleDuration(timelineSegment.ideaFlowBands, idle)
		}
	}

	private void addIdleDuration(List<IdeaFlowBand> timeBands, IdleTimeBand idle) {
		for (IdeaFlowBand timeBand : timeBands) {
			Duration idleDuration = timeBandCalculator.getIdleDurationForTimeBand(timeBand, idle)
			timeBand.idle = timeBand.idle.plus(idleDuration)
			addIdleDuration(timeBand.nestedBands, idle)
		}
	}

}
