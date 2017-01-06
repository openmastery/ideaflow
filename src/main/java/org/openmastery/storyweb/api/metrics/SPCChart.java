package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
	int totalForthDegree;

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

			totalFirstDegree++;

			totalSecondDegree += graphPoint.getChildPoints().size();

			for (ExplodableGraphPoint innerPoint: graphPoint.getChildPoints()) {
				totalThirdDegree += innerPoint.getChildPoints().size();

				for (ExplodableGraphPoint innerInnerPoint: innerPoint.getChildPoints()) {
					totalForthDegree += innerInnerPoint.getChildPoints().size();
				}
			}
		}
	}



}
