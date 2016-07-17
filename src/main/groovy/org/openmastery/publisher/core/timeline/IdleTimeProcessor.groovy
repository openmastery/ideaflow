package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.activity.IdleActivityEntity

class IdleTimeProcessor {

	private TimeBandIdleCalculator timeBandCalculator = new TimeBandIdleCalculator()

	public void collapseIdleTime(BandTimelineSegment timelineSegment, List<IdleActivityEntity> idleActivities) {
		for (IdleActivityEntity idle : idleActivities) {
			addIdleDuration(timelineSegment.ideaFlowBands, idle)

			for (TimeBandGroupModel group : timelineSegment.timeBandGroups) {
				addIdleDuration(group.linkedTimeBands, idle)
			}
		}
	}

	private void addIdleDuration(List<TimeBandModel> timeBands, IdleActivityEntity idleEntity) {
		for (TimeBandModel timeBand : timeBands) {
			if (timeBand instanceof IdeaFlowBandModel) {
				IdleTimeBandModel idle = toIdleTimeBand(idleEntity)
				IdleTimeBandModel splitIdle = timeBandCalculator.getIdleForTimeBandOrNull(timeBand, idle)
				if (splitIdle != null) {
					timeBand.addIdleBand(splitIdle)
					addIdleDuration(timeBand.nestedBands, idleEntity)
				}
			}
		}
	}

	private IdleTimeBandModel toIdleTimeBand(IdleActivityEntity entity) {
		// TODO: use dozer
		IdleTimeBandModel.builder()
				.id(entity.id)
				.taskId(entity.taskId)
				.start(entity.start)
				.end(entity.end)
				.build()
	}

}
