package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.timeline.ActivityTimeline;
import org.openmastery.publisher.api.timeline.BandTimeline;
import org.openmastery.publisher.api.timeline.TreeTimeline;

public class TimelineClient extends OpenMasteryClient<Object, TimelineClient> {

	public TimelineClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TIMELINE_PATH, Object.class);
	}

	public BandTimeline getBandTimelineForTask(long taskId) {
		return (BandTimeline) getUntypedCrudClientRequest()
				.path(ResourcePaths.TIMELINE_BAND_PATH)
				.queryParam("taskId", taskId)
				.entity(BandTimeline.class)
				.find();
	}

	public TreeTimeline getTreeTimelineForTask(long taskId) {
		return (TreeTimeline) getUntypedCrudClientRequest()
				.path(ResourcePaths.TIMELINE_TREE_PATH)
				.queryParam("taskId", taskId)
				.entity(TreeTimeline.class)
				.find();
	}

	public ActivityTimeline getActivityTimelineForTask(long taskId) {
		return (ActivityTimeline) getUntypedCrudClientRequest()
				.path(ResourcePaths.TIMELINE_ACTIVITY_PATH)
				.queryParam("taskId", taskId)
				.entity(ActivityTimeline.class)
				.find();
	}

}
