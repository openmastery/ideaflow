package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBand

class IdleTimeProcessor {

	private TimeBandIdleCalculator timeBandCalculator = new TimeBandIdleCalculator()

	public void collapseIdleTime(TimelineSegment timelineSegment, List<IdleTimeBand> idleActivities) {
		for (IdleTimeBand idle : idleActivities) {
			addIdleDuration(timelineSegment.ideaFlowBands, idle)
		}
	}

	private void addIdleDuration(List<IdeaFlowBand> timeBands, IdleTimeBand idle) {
		for (IdeaFlowBand timeBand : timeBands) {
			IdleTimeBand splitIdle = timeBandCalculator.getIdleForTimeBandOrNull(timeBand, idle)
			if (splitIdle != null) {
				timeBand.idle = timeBand.idle.plus(splitIdle.getDuration())
				addIdleDuration(timeBand.nestedBands, idle)
			}
		}
	}

}
