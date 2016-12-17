package org.openmastery.publisher.api.timeline.summary;

import org.openmastery.publisher.api.activity.ExecutionActivity;
import org.openmastery.publisher.api.event.Event;

import java.util.List;

public class TroubleshootingSession {
	Long id;
	Long durationInSeconds;

	List<String> tags;
	List<Event> events;

	List<ExecutionActivity> executionActivities;



}
