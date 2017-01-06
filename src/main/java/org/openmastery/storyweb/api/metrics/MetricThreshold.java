package org.openmastery.storyweb.api.metrics;

import lombok.*;
import org.openmastery.publisher.api.metrics.MetricType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "metricType")
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
