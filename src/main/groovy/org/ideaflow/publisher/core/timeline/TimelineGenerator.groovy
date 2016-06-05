package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.timeline.BandTimeline
import org.ideaflow.publisher.api.timeline.TreeTimeline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TimelineGenerator {

	@Autowired
	private BandTimelineFactory bandTimelineFactory

	public BandTimeline createBandTimelineForTask(long taskId) {
		bandTimelineFactory.createSingleSegmentBandTimelineForTask(taskId)
	}

	public TreeTimeline createTreeTimelineForTask(long taskId) {
		BandTimeline bandTimeline = bandTimelineFactory.createSingleSegmentBandTimelineForTask(taskId)
		return new TreeTimelineBuilder()
				.addTimeline(bandTimeline)
				.build();
	}

}
