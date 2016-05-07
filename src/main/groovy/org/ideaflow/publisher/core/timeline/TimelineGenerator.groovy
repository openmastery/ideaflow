package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.timeline.Timeline
import org.ideaflow.publisher.api.timeline.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity

public class TimelineGenerator {

	private TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
	private IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
	private TimelineSplitter timelineSplitter = new TimelineSplitter()
	private RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()

	public Timeline createTimeline(List<IdeaFlowStateEntity> ideaFlowStates, List<IdleTimeBandEntity> idleActivities,
	                               List<EventEntity> eventList) {
		TimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates, eventList)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		List<TimelineSegment> segments = timelineSplitter.splitTimelineSegment(segment)
		Timeline timeline = Timeline.builder()
				.timelineSegments(segments)
				.build()
		relativeTimeProcessor.setRelativeTime(timeline)
		timeline
	}

}
