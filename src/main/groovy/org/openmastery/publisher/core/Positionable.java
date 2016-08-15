package org.openmastery.publisher.core;

import java.time.LocalDateTime;

public interface Positionable {

	LocalDateTime getPosition();

	Long getRelativePositionInSeconds();
	void setRelativePositionInSeconds(Long relativePositionInSeconds);

}
