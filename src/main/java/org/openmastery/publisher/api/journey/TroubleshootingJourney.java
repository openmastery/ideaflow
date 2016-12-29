package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;
import org.openmastery.publisher.api.metrics.Metric;

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
public class TroubleshootingJourney extends AbstractRelativeInterval {

	@JsonIgnore
	IdeaFlowBand band;

	Set<String> tags; //derived from WTF/YAY #hashtags

	List<PartialDiscovery> partialDiscoveries;
	List<Metric<?>> metrics;

	Long eventId;

	public TroubleshootingJourney(IdeaFlowBand band) {
		this.band = band;
		setRelativeStart(band.getRelativePositionInSeconds());
		setDurationInSeconds(band.getDurationInSeconds());

		this.partialDiscoveries = new ArrayList<PartialDiscovery>();
		this.tags = new HashSet<String>();
	}

	public void addPartialDiscovery(Event wtfYayEvent, Long durationInSeconds) {
		PartialDiscovery partialDiscovery = new PartialDiscovery(wtfYayEvent, durationInSeconds);
		tags.addAll(partialDiscovery.tags);

		if (eventId == null) {
			eventId = wtfYayEvent.getId();
		}

		partialDiscoveries.add(partialDiscovery);
	}

	public boolean containsEvent(long eventId) {
		boolean containsEvent = false;

		for (PartialDiscovery partialDiscovery : partialDiscoveries) {
			if (partialDiscovery.event.getId() == eventId) {
				containsEvent = true;
				break;
			}
		}
		return containsEvent;
	}


	public void addFAQ(long eventId, String faqComment) {
		for (PartialDiscovery partialDiscovery : partialDiscoveries) {
			if (partialDiscovery.event.getId() == eventId) {
				partialDiscovery.faqComment = faqComment;
				break;
			}
		}
	}

	public void addSnippet(long eventId, String source, String snippet) {
		for (PartialDiscovery partialDiscovery : partialDiscoveries) {
			if (partialDiscovery.event.getId() == eventId) {
				partialDiscovery.formattableSnippet = new FormattableSnippet(source, snippet);
				break;
			}
		}
	}

	public void fillWithActivity(List<ExecutionEvent> executionEvents) {
		for (ExecutionEvent executionEvent : executionEvents) {
			if (shouldContain(executionEvent)) {
				addExecutionEvent(executionEvent);
			}
		}
	}

	private void addExecutionEvent(ExecutionEvent event) {
		for (PartialDiscovery partialDiscovery : partialDiscoveries) {
			if (partialDiscovery.shouldContain(event)) {
				partialDiscovery.addExecutionEvent(event);
			}
		}

	}

	public LocalDateTime getStart() {
		return band.getStart();
	}

	public LocalDateTime getEnd() {
		return band.getEnd();
	}



}
