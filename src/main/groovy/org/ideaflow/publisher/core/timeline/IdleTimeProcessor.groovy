package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandGroup
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBand
import org.ideaflow.publisher.core.timeline.TimeBandIdleCalculator

class IdleTimeProcessor {

	private TimeBandIdleCalculator timeBandCalculator = new TimeBandIdleCalculator()

	public void collapseIdleTime(TimelineSegment timelineSegment, List<IdleTimeBand> idleActivities) {
		for (IdleTimeBand idle : idleActivities) {
			addIdleDuration(timelineSegment.ideaFlowBands, idle)

			for (TimeBandGroup group : timelineSegment.timeBandGroups) {
				addIdleDuration(group.linkedTimeBands, idle)
			}
		}
	}

	private void addIdleDuration(List<TimeBand> timeBands, IdleTimeBand idle) {
		for (TimeBand timeBand : timeBands) {
			if (timeBand instanceof IdeaFlowBand) {
				IdleTimeBand splitIdle = timeBandCalculator.getIdleForTimeBandOrNull(timeBand, idle)
				if (splitIdle != null) {
					timeBand.addIdleBand(splitIdle)
					addIdleDuration(timeBand.nestedBands, idle)
				}
			}
		}
	}

}
