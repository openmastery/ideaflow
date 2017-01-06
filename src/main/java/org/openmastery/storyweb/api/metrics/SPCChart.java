package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.openmastery.storyweb.core.metrics.spc.MetricSet;

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

	MetaMetrics meta;

	List<GraphPoint<?>> graphPoints; //these are all the raw data points that can be sliced and diced on the client side

	Set<MetricThreshold<?>> painThresholds;
	List<String> includeByTag;
	List<String> excludeByTag;
	List<AggregateBy> aggregateBy;

	public SPCChart() {
		painTags = new HashSet<String>();
		contextTags = new HashSet<String>();
		meta = new MetaMetrics();

		graphPoints = new ArrayList<GraphPoint<?>>();
		painThresholds = new HashSet<MetricThreshold<?>>();

		includeByTag = new ArrayList<String>();
		excludeByTag = new ArrayList<String>();
		aggregateBy = new ArrayList<AggregateBy>();
	}

	public void addMetricSet(MetricSet metricSet) {
		graphPoints.addAll(metricSet.getExplodableTrees());
		painThresholds.addAll( metricSet.getPainThresholds());
		calculateStats(metricSet.getExplodableTrees());
	}



	private void calculateStats(List<GraphPoint<?>> graphPoints) {

		for (GraphPoint<?> graphPoint: graphPoints) {

			contextTags.addAll(graphPoint.contextTags);
			painTags.addAll(graphPoint.painTags);

			meta.totalFirstDegree++;

			meta.totalSecondDegree += graphPoint.getChildPoints().size();

			for (GraphPoint<?> innerPoint: graphPoint.getChildPoints()) {
				meta.totalThirdDegree += innerPoint.getChildPoints().size();

				for (GraphPoint<?> innerInnerPoint: innerPoint.getChildPoints()) {
					meta.totalFourthDegree += innerInnerPoint.getChildPoints().size();
				}
			}
		}
	}


}
