package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.metrics.Metric;
import org.openmastery.publisher.api.metrics.SubtaskOverview;
import org.openmastery.tags.TagsUtil;

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
	String faqComment;

	FormattableSnippet formattableSnippet;
	Set<String> tags; //derived from WTF/YAY #hashtags

	List<ExperimentCycle> experimentCycles;

	public PartialDiscovery(Event wtfYayEvent, Long durationInSeconds) {
		this.event = wtfYayEvent;
		this.experimentCycles = new ArrayList<ExperimentCycle>();
		this.tags = TagsUtil.extractUniqueHashTags(wtfYayEvent.getComment());

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



}
