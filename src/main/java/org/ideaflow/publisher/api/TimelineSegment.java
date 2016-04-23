package org.ideaflow.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineSegment {

	private LocalDateTime start;
	private LocalDateTime end;

	private List<TimeBand> timeBands;
	private List<TimeBandGroup> timeBandGroups;

	public Duration getDuration() {
		Duration duration = TimeBand.sumDuration(timeBands);
		return duration.plus(TimeBandGroup.sumDuration(timeBandGroups));
	}

}
