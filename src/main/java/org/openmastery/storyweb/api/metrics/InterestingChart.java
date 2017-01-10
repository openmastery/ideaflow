package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestingChart {

	//saved configurables for an SPC charting query
	//defined in terms of thresholds

	LocalDate startDate;
	LocalDate endDate;

	List<MetricThreshold<?>> painThresholds; //threshold are rules that run against a specific REST search path, i.e. metrics?path="/task/{taskId}/subtask/{subtaskId}/METRIC"

	List<MultiVariateThreshold> multiVariateThresholds; //Got a complex multi-variate pattern, no problem!...

	List<String> includeByTag;
	List<String> excludeByTag;
	List<AggregateBy> aggregateBy;
}
