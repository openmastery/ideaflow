package org.openmastery.publisher.api.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskOverview {

	Event subtaskEvent;
	Long durationInSeconds;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;

	public Long getSubtaskId() {
		return subtaskEvent.getId();
	}

	public String getDescription() {
		return subtaskEvent.getComment();
	}

	@JsonIgnore
	Map<MetricType, MetricsCalculator> calculators;

	public void addMetric(MetricType type, MetricsCalculator calculator) {
		if (calculators == null) {
			calculators = new HashMap<MetricType, MetricsCalculator>();
		}
		calculators.put(type, calculator);
	}

	public void calculate(IdeaFlowTimeline timeline) {
		allMetrics = new ArrayList<Metric<?>>();
		for (MetricsCalculator calculator: calculators.values()) {
			Metric<?> result = calculator.calculateMetrics(timeline);

			if (result.isDanger()) {
				dangerMetrics.add(result);
			}

			allMetrics.add(result);
		}
	}
}
