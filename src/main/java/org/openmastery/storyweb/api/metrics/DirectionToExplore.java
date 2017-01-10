package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectionToExplore<T> {

	MetricThreshold<T> painThreshold;
	int rate;
}
