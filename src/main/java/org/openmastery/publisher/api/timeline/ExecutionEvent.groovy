package org.openmastery.publisher.api.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.EventType


class ExecutionEvent {
	private Long id;

	private LocalDateTime position;
	private Long relativePositionInSeconds;

	private boolean isTest;
	private boolean isFailure;


}
