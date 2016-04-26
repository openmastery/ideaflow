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
			Duration idleDuration = TimeBandCalculator.getIdleDurationForTimeBand(timeBand, idle)
			timeBand.idle = timeBand.idle.plus(idleDuration)
			addIdleDuration(timeBand.nestedBands, idle)
		}
	}

}
