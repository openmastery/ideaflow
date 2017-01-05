package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
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
public class TroubleshootingJourney extends AbstractRelativeInterval implements MeasurableContext {

	@JsonIgnore
	IdeaFlowBand band;

	Long id;
	String relativePath;

	Set<String> contextTags;
	Set<String> painTags; //derived from WTF/YAY #hashtags

	List<Metric<?>> metrics;
	List<DiscoveryCycle> discoveryCycles;

	public TroubleshootingJourney(IdeaFlowBand band) {
		this.band = band;
		setRelativeStart(band.getRelativePositionInSeconds());
		setDurationInSeconds(band.getDurationInSeconds());

		this.discoveryCycles = new ArrayList<DiscoveryCycle>();
		this.contextTags = new HashSet<String>();
		this.painTags = new HashSet<String>();
	}

	public void addPartialDiscovery(Event wtfYayEvent, Long durationInSeconds) {
		DiscoveryCycle partialDiscovery = new DiscoveryCycle(wtfYayEvent, durationInSeconds);
		painTags.addAll(partialDiscovery.painTags);

		if (id == null) {
			id = wtfYayEvent.getId();
			relativePath = "/journey/"+id;
		}

		discoveryCycles.add(partialDiscovery);
	}

	public boolean containsEvent(long eventId) {
		boolean containsEvent = false;

		for (DiscoveryCycle partialDiscovery : discoveryCycles) {
			if (partialDiscovery.event.getId() == eventId) {
				containsEvent = true;
				break;
			}
		}
		return containsEvent;
	}

	public void addFAQ(long eventId, String faqComment) {
		for (DiscoveryCycle partialDiscovery : discoveryCycles) {
			if (partialDiscovery.event.getId() == eventId) {
				partialDiscovery.addFaq(faqComment);
				contextTags.addAll(partialDiscovery.contextTags);
				break;
			}
		}
	}

	public void addSnippet(long eventId, String source, String snippet) {
		for (DiscoveryCycle partialDiscovery : discoveryCycles) {
			if (partialDiscovery.event.getId() == eventId) {
				partialDiscovery.formattableSnippet = new FormattableSnippet(source, snippet);
				break;
			}
		}
	}

	public LocalDateTime getPosition() {
		return band.getPosition();
	}

	@JsonIgnore
	public LocalDateTime getEnd() {
		return band.getEnd();
	}

	public String getDescription() {
		String description = "";
		if (discoveryCycles.size() > 0) {
			description = discoveryCycles.get(0).event.getComment();
		}
		return description;
	}


	public int getFrequency() {
		return getDiscoveryCycles().size();
	}

	public Long getRelativePositionInSeconds() { return band.getRelativeStart(); }

}
