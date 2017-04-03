package org.openmastery.publisher.client;

import org.joda.time.LocalDateTime;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.annotation.FAQAnnotation;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.EventPatch;
import org.openmastery.publisher.api.journey.FormattableSnippet;


import java.util.List;

public class TaskEventClient extends IdeaFlowClient<Event, TaskEventClient> {

	public TaskEventClient(String baseUrl) {
		super(baseUrl, ResourcePaths.IDEAFLOW_PATH , Event.class);
	}

	public Event updateEventDescription(String fullPath, String description) {
		EventPatch patch = new EventPatch();
		patch.setDescription(description);

		return crudClientRequest
				.path(fullPath)
				.updateWithPut(patch);
	}

	public Event updateEventFaq(String fullPath, String faq) {
		EventPatch patch = new EventPatch();
		patch.setFaq(faq);
		return crudClientRequest
				.path(fullPath)
				.updateWithPut(patch);
	}

	public Event updateEventSnippet(String fullPath, FormattableSnippet snippet) {
		EventPatch patch = new EventPatch();
		patch.setFormattableSnippet(snippet);
		return crudClientRequest
				.path(fullPath)
				.updateWithPut(patch);
	}

}
