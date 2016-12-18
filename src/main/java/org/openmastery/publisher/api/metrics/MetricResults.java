package org.openmastery.publisher.api.metrics;

import java.util.Map;

public class MetricResults {
	MetricType type;
	GroupBy groupBy;

	Map<String, Double> results;
}
