package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric<V> {
	MetricType type;
	V value;

	private String valueType;

	public void setValue(V value) {
		if (value != null) {
			valueType = value.getClass().getSimpleName();
		}
		this.value = value;
	}

}
