package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskMetrics {

	Long id;
	String description;
	Long durationInSeconds;

	Map<MetricType, Object> metrics;

	public void addMetric(MetricType type, Object value) {
		if (metrics == null) {
			metrics = new HashMap<>();
		}
		metrics.put(type, value);
	}
}
