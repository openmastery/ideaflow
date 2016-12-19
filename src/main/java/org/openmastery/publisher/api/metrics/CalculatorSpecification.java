package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.acl.Group;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorSpecification {

	//TODO figure out how to do this declaratively

	GroupBy groupBy;
	MetricType metricToRun;
}
