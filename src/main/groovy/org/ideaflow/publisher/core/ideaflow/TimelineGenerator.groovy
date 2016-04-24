package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleActivityEntity

public class TimelineGenerator {

	private TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
	private IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()

	public TimelineSegment createTimeline(List<IdeaFlowStateEntity> ideaFlowStates, List<IdleActivityEntity> idleActivities) {
		TimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		segment
	}

}
