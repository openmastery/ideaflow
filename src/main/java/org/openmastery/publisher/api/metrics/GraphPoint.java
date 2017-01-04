package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.journey.Measurable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
public class GraphPoint<V> implements Positionable {

	String relativePath;

	Set<String> painTags;
	Set<String> contextTags;

	LocalDateTime position;
	Long relativePositionInSeconds;

	V value;
	MetricType metricType;
	Integer frequency;

	List<GraphPoint<V>> childPoints;

	boolean danger;

	public GraphPoint() {
		contextTags = new HashSet<String>();
		painTags = new HashSet<String>();
		childPoints = new ArrayList<GraphPoint<V>>();
	}

	private void addContextTags(Set<String> contextTags) {
		this.contextTags.addAll(contextTags);
	}

	private void addPainTags(Set<String> painTags) {
		this.painTags.addAll(painTags);
	}

	public void forcePushTagsToChildren() {
		for (GraphPoint childPoint : childPoints) {
			childPoint.forcePushTagsToThisAndChildren(contextTags, painTags);
		}
	}

	public void forcePushTagsToThisAndChildren(Set<String> contextTags, Set<String> painTags) {
		this.contextTags = contextTags;
		this.painTags = painTags;
		for (GraphPoint childPoint : childPoints) {
			childPoint.forcePushTagsToThisAndChildren(contextTags, painTags);
		}
	}

}
