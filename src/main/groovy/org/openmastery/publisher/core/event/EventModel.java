package org.openmastery.publisher.core.event;


import lombok.Getter;
import lombok.Setter;
import org.openmastery.publisher.api.event.EventType;

import java.time.LocalDateTime;

public class EventModel {

	private EventEntity delegate;
	@Getter
	@Setter
	private Long relativeStart;

	public EventModel(EventEntity delegate) {
		this.delegate = delegate;
	}

	public Long getId() {
		return delegate.getId();
	}

	public Long getTaskId() {
		return delegate.getTaskId();
	}

	public LocalDateTime getPosition() {
		return delegate.getPosition();
	}

	public String getComment() {
		return delegate.getComment();
	}

	public EventType getType() {
		return delegate.getType();
	}

}
