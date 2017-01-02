package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.tags.TagsUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class DiscoveryCycle extends AbstractRelativeInterval {

	Event event;
	String faqComment;

	FormattableSnippet formattableSnippet;

	Set<String> painTags; //derived from WTF/YAY #hashtags
	Set<String> contextTags; //derived from FAQs or containing subtasks

	List<ExperimentCycle> experimentCycles;

	public DiscoveryCycle(Event wtfYayEvent, Long durationInSeconds) {
		this.event = wtfYayEvent;
		this.experimentCycles = new ArrayList<ExperimentCycle>();
		this.contextTags = new HashSet<String>();
		this.painTags = TagsUtil.extractUniqueHashTags(wtfYayEvent.getComment());

		setRelativeStart(wtfYayEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

	public void addExperimentCycle(ExecutionEvent executionEvent) {
		updateEndTimeOfLastExperimentCycle(executionEvent.getRelativePositionInSeconds());

		Long durationInSeconds = getRelativeEnd() - executionEvent.getRelativePositionInSeconds();
		ExperimentCycle experimentCycle = new ExperimentCycle(executionEvent, durationInSeconds);
		experimentCycles.add(experimentCycle);
	}

	public void addFaq(String faqComment) {
		this.faqComment = faqComment;
		contextTags = TagsUtil.extractUniqueHashTags(faqComment);
	}

	void updateEndTimeOfLastExperimentCycle(Long beginningOfNextExecutionCycle) {
		if (experimentCycles.size() > 0) {
			ExperimentCycle lastCycle = experimentCycles.get(experimentCycles.size() - 1);
			lastCycle.setDurationInSeconds(beginningOfNextExecutionCycle - lastCycle.getRelativeStart());
		}
	}



}
