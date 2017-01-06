package org.openmastery.storyweb.api.metrics;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.publisher.api.metrics.MetricType;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({"relativePath", "metricType", "relativePositionInSeconds", "position", "value", "contextTags", "painTags", "frequency", "distance", "danger"})
public class GraphPoint<V> implements Positionable, Explodable<GraphPoint<V>>, Cloneable {

	String relativePath;
	MetricType metricType;

	LocalDateTime position;
	Long relativePositionInSeconds;

	V value;

	Set<String> contextTags;
	Set<String> painTags;

	Integer frequency;
	Long distance;
	boolean danger;

	List<GraphPoint<V>> childPoints;


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

	public Metric<V> toMetric(String prefixPath) {
		Metric<V> metric = new Metric<V>();
		String path = relativePath;
		if (prefixPath.length() > 0) {
			path = prefixPath + relativePath;
		}

		metric.setRelativePath(path + "/" + metricType.name());
		metric.setValue(value);
		metric.setType(metricType);

		metric.setDanger(danger);

		return metric;
	}

	public Map<String, GraphPoint<V>> explodeToMap() throws CloneNotSupportedException {
		Map<String, GraphPoint<V>> flattenedMap = new HashMap<String, GraphPoint<V>>();

		List<GraphPoint<V>> explodedPoints = explode();
		for (GraphPoint<V> point : explodedPoints) {
			flattenedMap.put(point.getRelativePath(), point);
		}
		return flattenedMap;
	}

	public List<GraphPoint<V>> explode() throws CloneNotSupportedException {
		return prefixWithRelativePath(relativePath, childPoints);
	}

	public List<GraphPoint<V>> collapse(List<GraphPoint<V>> explodedPoints) {
		throw new NotImplementedException("Implement me! - should compare exploded points to tree and return everything 1 level up");
	}

	private List<GraphPoint<V>> prefixWithRelativePath(String relativePathPrefix, List<GraphPoint<V>> graphPoints) throws CloneNotSupportedException {
		List<GraphPoint<V>> prefixedPoints = new ArrayList<GraphPoint<V>>();

		for (GraphPoint<V> childPoint : childPoints) {
			GraphPoint<V> splodePoint = (GraphPoint<V>) childPoint.clone();
			splodePoint.setRelativePath(relativePathPrefix + splodePoint.getRelativePath());
			prefixedPoints.add(splodePoint);
		}
		return prefixedPoints;
	}

	private List<GraphPoint<V>> stripPrefixFromRelativePath(String relativePathPrefix, List<GraphPoint<V>> graphPoints) throws CloneNotSupportedException {
		List<GraphPoint<V>> prefixedPoints = new ArrayList<GraphPoint<V>>();

		for (GraphPoint<V> childPoint : childPoints) {
			GraphPoint<V> splodePoint = (GraphPoint<V>) childPoint.clone();
			splodePoint.setRelativePath(relativePathPrefix + splodePoint.getRelativePath());
			prefixedPoints.add(splodePoint);
		}
		return prefixedPoints;
	}


	@Override
	protected GraphPoint<V> clone() throws CloneNotSupportedException {
		return (GraphPoint<V>) super.clone();
	}


	public void forcePushContextTagsToChildren() {
		for (GraphPoint childPoint : childPoints) {
			childPoint.forcePushTagsToThisAndChildren(contextTags);
		}
	}

	public void forcePushTagsToThisAndChildren(Set<String> contextTags) {
		this.contextTags.addAll(contextTags);
		for (GraphPoint childPoint : childPoints) {

			childPoint.forcePushTagsToThisAndChildren(this.contextTags);
		}
	}

	public void forceBubbleUpAllPain() {
		Set<String> allPain = new HashSet<String>();
		for (GraphPoint<?> childPoint : childPoints) {
			childPoint.forceBubbleUpAllPain();
			allPain.addAll(childPoint.painTags);
		}
		painTags.addAll(allPain);
	}

	public void forceBubbleUpDangerTags() {
		Set<String> allDangerTags = new HashSet<String>();
		for (GraphPoint<?> childPoint : childPoints) {
			childPoint.forceBubbleUpDangerTags();
			if (childPoint.isDanger()) {
				allDangerTags.addAll(childPoint.painTags);
			}
		}
		painTags.addAll(allDangerTags);
	}


}
