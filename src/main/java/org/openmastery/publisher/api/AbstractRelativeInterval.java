package org.openmastery.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractRelativeInterval implements RelativeInterval {

	Long relativeStart;
	Long durationInSeconds;

	public Long getRelativeEnd() {
		return relativeStart + durationInSeconds;
	}

	public boolean shouldContain(RelativePositionable positionable) {
		return (positionable.getRelativePositionInSeconds() >= getRelativeStart()
				&& positionable.getRelativePositionInSeconds() <= getRelativeEnd());
	}

}
