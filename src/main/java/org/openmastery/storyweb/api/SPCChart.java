package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.metrics.Metric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class SPCChart {

	Set<String> painTags;
	Set<String> contextTags;

	int totalFirstDegree;
	int totalSecondDegree;
	int totalThirdDegree;

	List<ExplodableGraphPoint> graphPoints; //these are all the raw data points that can be sliced and diced on the client side
	List<MetricThreshold<?>> metricThresholds;

	public SPCChart() {
		painTags = new HashSet<String>();
		contextTags = new HashSet<String>();
		graphPoints = new ArrayList<ExplodableGraphPoint>();
	}

	public void addGraphPoints(List<ExplodableGraphPoint> graphPoints) {

		this.graphPoints.addAll(graphPoints);

		for (ExplodableGraphPoint graphPoint: graphPoints) {
			contextTags.addAll(graphPoint.contextTags);
			painTags.addAll(graphPoint.painTags);
			totalFirstDegree += graphPoint.totalFirstDegree;
			totalSecondDegree += graphPoint.totalSecondDegree;
			totalThirdDegree += graphPoint.totalThirdDegree;
		}
	}



}
