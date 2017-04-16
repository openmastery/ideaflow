package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySummary {

	private String activityType;
	private String activityName;
	private String activityDetail;

	private Long totalDurationInSeconds;
	private Long modifiedDurationInSeconds;

	public void aggregate(ActivitySummary activitySummary) {
		totalDurationInSeconds += activitySummary.totalDurationInSeconds;
		modifiedDurationInSeconds += activitySummary.modifiedDurationInSeconds;
	}

}
