package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.timeline.BandTimeline
import org.ideaflow.publisher.api.timeline.TreeTimeline
import org.openmastery.mapper.EntityMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TimelineGenerator {

	@Autowired
	private BandTimelineFactory bandTimelineFactory

	public BandTimeline createBandTimelineForTask(long taskId) {
		EntityMapper mapper = new EntityMapper()

		BandTimelineSegment segment = bandTimelineFactory.createBandTimelineSegmentForTask(taskId)
		mapper.mapIfNotNull(segment, BandTimeline.class)
	}

	public TreeTimeline createTreeTimelineForTask(long taskId) {
		List<BandTimelineSegment> segments = bandTimelineFactory.createAndSplitBandTimelineSegmentForTask(taskId)
		new TreeTimelineBuilder()
				.addTimelineSegments(segments)
				.build()
	}

}
