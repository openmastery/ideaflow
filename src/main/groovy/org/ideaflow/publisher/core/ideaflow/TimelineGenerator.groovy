package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.Timeline
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBand
import org.ideaflow.publisher.core.event.EventEntity

public class TimelineGenerator {

	private TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
	private IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
	private TimelineSplitter timelineSplitter = new TimelineSplitter()

	public Timeline createTimeline(List<IdeaFlowStateEntity> ideaFlowStates, List<IdleTimeBand> idleActivities,
	                               List<EventEntity> eventList) {
		List<EventEntity> subtaskEventList = eventList.findAll { it.eventType == EventEntity.Type.SUBTASK }
		TimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		List<TimelineSegment> segments = timelineSplitter.splitTimelineSegment(segment, subtaskEventList)
		return Timeline.builder()
				.timelineSegments(segments)
				.build()
	}

}
