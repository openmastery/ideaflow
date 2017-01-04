package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.storyweb.api.TagsUtil;

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
public class DiscoveryCycle extends AbstractRelativeInterval implements Measurable {

	@JsonIgnore
	Event event;

	Set<String> painTags; //derived from WTF/YAY #hashtags
	Set<String> contextTags; //derived from FAQs or containing subtasks

	String faqComment;
	FormattableSnippet formattableSnippet;

	List<ExperimentCycle> experimentCycles;

	public DiscoveryCycle(Event wtfYayEvent, Long durationInSeconds) {
		this.event = wtfYayEvent;
		this.experimentCycles = new ArrayList<ExperimentCycle>();
		this.contextTags = new HashSet<String>();
		this.painTags = TagsUtil.extractUniqueHashTags(wtfYayEvent.getComment());

		setRelativeStart(wtfYayEvent.getRelativePositionInSeconds());
		setDurationInSeconds(durationInSeconds);
	}

	public void addExperimentCycle(ExperimentCycle experimentCycle) {
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

	public String getDescription() {
		return event.getType().name() + ": " + event.getComment();
	}

	public LocalDateTime getPosition() { return event.getPosition(); }

	public Long getId() { return event.getId(); }

	public int getFrequency() { return getExperimentCycles().size(); }

}
