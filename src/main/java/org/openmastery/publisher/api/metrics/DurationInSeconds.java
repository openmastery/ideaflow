package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DurationInSeconds {

	long durationInSeconds;


	public boolean greaterThan(DurationInSeconds durationObj) {
		boolean isGreaterThan = false;
		if (durationInSeconds > durationObj.durationInSeconds) {
			isGreaterThan = true;
		}
		return isGreaterThan;
	}

	public void incrementBy(long duration) {
		durationInSeconds += duration;
	}
}
