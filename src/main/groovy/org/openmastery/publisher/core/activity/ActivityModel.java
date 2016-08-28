package org.openmastery.publisher.core.activity;

import lombok.Getter;
import lombok.Setter;
import org.openmastery.publisher.core.Positionable;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class ActivityModel<T extends ActivityEntity> implements Positionable {

	protected T delegate;
	@Getter @Setter
	private Long relativePositionInSeconds;

	public ActivityModel(T delegate) {
		this.delegate = delegate;
	}

	public Long getId() {
		return delegate.getId();
	}

	public Long getTaskId() {
		return delegate.getTaskId();
	}

	public LocalDateTime getPosition() {
		return delegate.getStart();
	}

	public LocalDateTime getStart() {
		return delegate.getStart();
	}

	public LocalDateTime getEnd() {
		return delegate.getEnd();
	}

	public Duration getDuration() {
		return Duration.between(getStart(), getEnd());
	}

}
