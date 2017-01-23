package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DurationInSeconds implements Comparable<DurationInSeconds> {

	long durationInSeconds;

	public DurationInSeconds(int durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public boolean greaterThan(DurationInSeconds durationObj) {
		boolean isGreaterThan = false;
		if (durationInSeconds > durationObj.durationInSeconds) {
			isGreaterThan = true;
		}
		return isGreaterThan;
	}

	public boolean greaterThan(Long duration) {
		boolean isGreaterThan = false;
		if (durationInSeconds > duration) {
			isGreaterThan = true;
		}
		return isGreaterThan;
	}

	public void incrementBy(long duration) {
		durationInSeconds += duration;
	}

	@Override
	public int compareTo(DurationInSeconds otherDuration) {
		return Long.valueOf(durationInSeconds).compareTo(otherDuration.durationInSeconds);
	}

	DurationInSeconds plus(DurationInSeconds other) {
		return new DurationInSeconds(this.durationInSeconds + other.durationInSeconds);
	}

	DurationInSeconds multiply(int frequency) {
		return new DurationInSeconds(this.durationInSeconds * frequency);
	}

	DurationInSeconds div(int frequency) {
		return new DurationInSeconds(this.durationInSeconds / frequency);
	}
}
