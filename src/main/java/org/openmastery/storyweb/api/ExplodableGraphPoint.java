package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.journey.DiscoveryCycle;
import org.openmastery.publisher.api.journey.TroubleshootingJourney;
import org.openmastery.publisher.api.metrics.DurationInSeconds;
import org.openmastery.publisher.api.metrics.Metric;
import org.openmastery.publisher.api.task.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ExplodableGraphPoint {

	String relativePath;

	Set<String> painTags;
	Set<String> contextTags;

	LocalDateTime position;
	DurationInSeconds durationInSeconds;
	Integer frequency;

	String typeName;
	String description;

	List<ExplodableGraphPoint> explodableGraphPoints;

	public ExplodableGraphPoint() {
		contextTags = new HashSet<String>();
		painTags = new HashSet<String>();
		durationInSeconds = new DurationInSeconds(0);
		explodableGraphPoints = new ArrayList<ExplodableGraphPoint>();
	}

	private void addContextTags(Set<String> contextTags) {
		this.contextTags.addAll(contextTags);
	}

	private void addPainTags(Set<String> painTags) {
		this.painTags.addAll(painTags);
	}


	public void forcePushTagsToChildren(Set<String> contextTags, Set<String> painTags) {
		this.contextTags = contextTags;
		this.painTags = painTags;
		for (ExplodableGraphPoint childPoint : explodableGraphPoints) {
			childPoint.forcePushTagsToChildren(contextTags, painTags);
		}
	}
}
