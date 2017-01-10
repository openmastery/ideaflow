package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiVariateThreshold {

	List<MetricThreshold<?>> relativePathDimensionalLimits;

}
