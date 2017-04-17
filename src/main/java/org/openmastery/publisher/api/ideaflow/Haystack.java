package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Haystack {

	public String relativePath;

	public LocalDateTime position;
	public Long relativePositionInSeconds;

	public Long durationInSeconds;
	public Long executionDurationInSeconds;
	public String processName;
	public String executionTaskType;
	public Boolean failed;
	public Boolean debug;

	public List<ActivitySummary> activitySummaries;

}
