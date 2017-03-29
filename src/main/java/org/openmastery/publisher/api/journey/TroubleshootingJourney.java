package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;
import org.openmastery.storyweb.api.metrics.Metric;

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
public class TroubleshootingJourney extends AbstractRelativeInterval implements StoryElement {

	@JsonIgnore
	String parentPath;
	@JsonIgnore
	IdeaFlowBand band;
	@JsonIgnore
	Long id;
	@JsonIgnore
	Event event;

	String relativePath;
	Set<String> contextTags;
	Set<String> painTags; //derived from WTF/YAY #hashtags

	List<DiscoveryCycle> discoveryCycles;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;

	public TroubleshootingJourney(String parentPath, IdeaFlowBand band) {
		this.band = band;
		this.parentPath = parentPath;
		setRelativeStart(band.getRelativePositionInSeconds());
		setDurationInSeconds(band.getDurationInSeconds());

		this.discoveryCycles = new ArrayList<DiscoveryCycle>();
		this.contextTags = new HashSet<String>();
		this.painTags = new HashSet<String>();


	}

	public Long getRelativePositionInSeconds() { return band.getRelativeStart(); }

	public LocalDateTime getPosition() {
		return band.getPosition();
	}

	public String getDescription() {
		String description = "";
		if (discoveryCycles.size() > 0) {
			description = discoveryCycles.get(0).event.getComment();
		}
		return description;
	}

	@JsonIgnore
	public String getFullPath() { return parentPath + getRelativePath(); }

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
		band.setFullPath(getFullPath());
		event.setFullPath(getFullPath());

		for (DiscoveryCycle discoveryCycle : discoveryCycles) {
			discoveryCycle.setParentPath(getFullPath());
		}
	}

	public void addPartialDiscovery(Event wtfYayEvent, Long durationInSeconds) {
		if (id == null) {
			id = wtfYayEvent.getId();
			relativePath = "/journey/"+id;
			this.event = wtfYayEvent;
			band.setFullPath(getFullPath());
			event.setFullPath(getFullPath());
		}

		DiscoveryCycle discoveryCycle = new DiscoveryCycle(getFullPath(), wtfYayEvent, durationInSeconds);
		painTags.addAll(discoveryCycle.painTags);
		discoveryCycles.add(discoveryCycle);
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
		for (DiscoveryCycle discoveryCycle : discoveryCycles) {
			if (discoveryCycle.event.getId() == eventId) {
				discoveryCycle.addFaq(faqComment);
				contextTags.addAll(discoveryCycle.contextTags);
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


	@JsonIgnore
	public LocalDateTime getEnd() {
		return band.getEnd();
	}

	@JsonIgnore
	public int getFrequency() {
		return getDiscoveryCycles().size();
	}

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return discoveryCycles;
	}


}
