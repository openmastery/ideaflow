package org.openmastery.publisher.api.timeline;

import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.timeline.summary.ExecutionType;


class ExecutionEvent {
	private Long id;

	private LocalDateTime position;
	private Long relativePositionInSeconds;

	String processName;
	ExecutionType executionType;
	Long durationInSeconds;

	boolean isDebug;
	boolean failed;
}
