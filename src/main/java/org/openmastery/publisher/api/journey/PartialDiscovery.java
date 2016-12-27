package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.metrics.Metric;
import org.openmastery.publisher.api.metrics.SubtaskOverview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class PartialDiscovery extends AbstractRelativeInterval {

	Event event;

	Set<String> tags; //derived from WTF/YAY #hashtags

	List<ExperimentCycle> experimentCycles;

	public PartialDiscovery(Event wtfYayEvent, Long durationInSeconds) {
		this.event = wtfYayEvent;
		this.experimentCycles = new ArrayList<ExperimentCycle>();
		this.tags = extractHashTags(wtfYayEvent.getComment());

		setRelativeStart(wtfYayEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

	public void addExecutionEvent(ExecutionEvent executionEvent) {
		updateEndTimeOfLastExecutionCycle(executionEvent.getRelativePositionInSeconds());

		Long durationInSeconds = getRelativeEnd() - executionEvent.getRelativePositionInSeconds();
		ExperimentCycle experimentCycle = new ExperimentCycle(executionEvent, durationInSeconds);
		experimentCycles.add(experimentCycle);
	}

	void updateEndTimeOfLastExecutionCycle(Long beginningOfNextExecutionCycle) {
		if (experimentCycles.size() > 0) {
			ExperimentCycle lastCycle = experimentCycles.get(experimentCycles.size() - 1);
			lastCycle.setDurationInSeconds(beginningOfNextExecutionCycle - lastCycle.getRelativeStart());
		}
	}

	private Set<String> extractHashTags(String commentWithTags) {
		Set<String> hashtags = new HashSet<String>();

		if (commentWithTags != null) {
			Pattern hashTagPattern = Pattern.compile("(#\\w+)");
			Matcher matcher = hashTagPattern.matcher(commentWithTags);
			while (matcher.find()) {
				hashtags.add(matcher.group(1));
			}
		}

		return hashtags;
	}
}
