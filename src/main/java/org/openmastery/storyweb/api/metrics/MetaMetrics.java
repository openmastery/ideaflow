package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaMetrics {

	int totalFirstDegree;
	int totalSecondDegree;
	int totalThirdDegree;
	int totalFourthDegree;

}
