package org.openmastery.storyweb.api.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.metrics.MetricType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric<V> implements Cloneable {

	@JsonIgnore
	String relativePath;

	@JsonIgnore
	Set<String> contextTags;
	@JsonIgnore
	Set<String> painTags;

	V value;

	MetricType type;
	private String valueType;
	private boolean danger;




	public void setValue(V value) {
		if (value != null) {
			valueType = value.getClass().getSimpleName();
		}
		this.value = value;
	}

}
