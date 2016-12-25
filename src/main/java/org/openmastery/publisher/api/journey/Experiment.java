package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.metrics.TimelineMetrics;

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
public class Experiment extends AbstractRelativeInterval {

	Event wtfYayEvent;

	Set<String> tags; //derived from WTF/YAY #hashtags

	List<ExecutionCycle> executionCycles;

	TimelineMetrics metrics;

	public Experiment(Event wtfYayEvent, Long durationInSeconds) {
		this.wtfYayEvent = wtfYayEvent;
		this.executionCycles = new ArrayList<ExecutionCycle>();
		this.tags = extractHashTags(wtfYayEvent.getComment());

		setRelativeStart(wtfYayEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

	public Long getId() {
		return wtfYayEvent.getId();
	}

	public void addExecutionEvent(ExecutionEvent event) {
		updateEndTimeOfLastExecutionCycle(event.getRelativePositionInSeconds());

		Long durationInSeconds = getRelativeEnd() - event.getRelativePositionInSeconds();
		ExecutionCycle executionCycle = new ExecutionCycle(event, durationInSeconds);
		executionCycles.add(executionCycle);
	}

	void updateEndTimeOfLastExecutionCycle(Long beginningOfNextExecutionCycle) {
		if (executionCycles.size() > 0) {
			ExecutionCycle lastCycle = executionCycles.get(executionCycles.size() - 1);
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
