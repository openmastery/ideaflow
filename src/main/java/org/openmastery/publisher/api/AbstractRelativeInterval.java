package org.openmastery.publisher.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractRelativeInterval implements RelativeInterval, RelativePositionable {

	Long durationInSeconds;


	@JsonIgnore
	Long relativeStart; //only used for metrics

	@JsonIgnore //only used for metrics
	public Long getRelativeEnd() {
		return relativeStart + durationInSeconds;
	}

	@Override
	public Long getRelativePositionInSeconds() {
		return relativeStart;
	}

	@Override
	public void setRelativePositionInSeconds(Long relativePositionInSeconds) {
		this.relativeStart = relativePositionInSeconds;
	}

	public boolean shouldContain(RelativePositionable positionable) {
		boolean isWithinRange = (positionable.getRelativePositionInSeconds() >= getRelativeStart()
				&& positionable.getRelativePositionInSeconds() < getRelativeEnd());

		return isWithinRange;
	}


}
