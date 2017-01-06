package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.metrics.MetricType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricThreshold<V> {
	MetricType metricType;
	V threshold;

	String valueType;

	public void setThreshold(V threshold) {
		if (threshold != null) {
			valueType = threshold.getClass().getSimpleName();
		}
		this.threshold = threshold;
	}

}
