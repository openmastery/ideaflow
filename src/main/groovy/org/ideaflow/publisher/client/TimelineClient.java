package org.ideaflow.publisher.client;

import org.openmastery.rest.client.CrudClient;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.timeline.Timeline;

public class TimelineClient extends CrudClient<Timeline, TimelineClient> {

	public TimelineClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TIMELINE_PATH, Timeline.class);
	}

	public Timeline getTimelineForTask(long taskId) {
		return crudClientRequest
				.path(ResourcePaths.TASK_PATH)
				.path(taskId)
				.find();
	}

}
