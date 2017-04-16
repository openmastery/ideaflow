package org.openmastery.storyweb.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.openmastery.publisher.api.metrics.DurationInSeconds;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@Deprecated
public class ExplodableGraphPoint {

	String relativePath;

	Set<String> painTags;
	Set<String> contextTags;

	LocalDateTime position;
	DurationInSeconds durationInSeconds;
	Integer frequency;

	String typeName;
	String description;

	List<ExplodableGraphPoint> childPoints;

	public ExplodableGraphPoint() {
		contextTags = new HashSet<>();
		painTags = new HashSet<>();
		durationInSeconds = new DurationInSeconds(0);
		childPoints = new ArrayList<>();
	}


	private void addContextTags(Set<String> contextTags) {
		this.contextTags.addAll(contextTags);
	}

	private void addPainTags(Set<String> painTags) {
		this.painTags.addAll(painTags);
	}

	public void forcePushTagsToChildren() {
		for (ExplodableGraphPoint childPoint : childPoints) {
			childPoint.forcePushTagsToThisAndChildren(contextTags, painTags);
		}
	}

	public void forcePushTagsToThisAndChildren(Set<String> contextTags, Set<String> painTags) {
		this.contextTags = contextTags;
		this.painTags = painTags;
		for (ExplodableGraphPoint childPoint : childPoints) {
			childPoint.forcePushTagsToThisAndChildren(contextTags, painTags);
		}
	}
}
