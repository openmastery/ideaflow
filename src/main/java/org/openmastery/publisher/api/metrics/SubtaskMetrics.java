package org.openmastery.publisher.api.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskMetrics {

	Long id;
	String description;
	Long durationInSeconds;

	List<Metric<?>> metrics;

	@JsonIgnore
	Map<MetricType, MetricsCalculator> calculators;

	public void addMetric(MetricType type, MetricsCalculator calculator) {
		if (calculators == null) {
			calculators = new HashMap<MetricType, MetricsCalculator>();
		}
		calculators.put(type, calculator);
	}

	public void calculate(IdeaFlowTimeline timeline) {
		metrics = new ArrayList<Metric<?>>();
		for (MetricsCalculator calculator: calculators.values()) {
			Metric<?> result = calculator.calculateMetrics(timeline);
			metrics.add(result);
		}
	}
}
