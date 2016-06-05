package org.ideaflow.publisher.client;

import org.openmastery.rest.client.CrudClient;
import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.timeline.BandTimeline;

public class TimelineClient extends CrudClient<BandTimeline, TimelineClient> {

	public TimelineClient(String baseUrl) {
		super(baseUrl, ResourcePaths.TIMELINE_PATH, BandTimeline.class);
	}

	public BandTimeline getBandTimelineForTask(long taskId) {
		return crudClientRequest
				.path(ResourcePaths.TIMELINE_BAND_PATH)
				.queryParam("taskId", taskId)
				.find();
	}

}
