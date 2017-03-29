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

	List<PainCycle> painCycles;

	List<Metric<?>> allMetrics;
	List<Metric<?>> dangerMetrics;

	public TroubleshootingJourney(String parentPath, IdeaFlowBand band) {
		this.band = band;
		this.parentPath = parentPath;
		setRelativeStart(band.getRelativePositionInSeconds());
		setDurationInSeconds(band.getDurationInSeconds());

		this.painCycles = new ArrayList<PainCycle>();
		this.contextTags = new HashSet<String>();
		this.painTags = new HashSet<String>();
	}

	public Long getRelativePositionInSeconds() { return band.getRelativeStart(); }

	public LocalDateTime getPosition() {
		return band.getPosition();
	}

	public String getDescription() {
		String description = "";
		if (painCycles.size() > 0) {
			description = painCycles.get(0).event.getComment();
		}
		return description;
	}

	@JsonIgnore
	public String getFullPath() { return parentPath + getRelativePath(); }

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
		band.setFullPath(getFullPath());
		event.setFullPath(getFullPath());

		for (PainCycle discoveryCycle : painCycles) {
			discoveryCycle.setParentPath(getFullPath());
		}
	}

	public void addPainCycle(Event wtfYayEvent, Long durationInSeconds) {
		if (id == null) {
			id = wtfYayEvent.getId();
			relativePath = "/journey/"+id;
			this.event = wtfYayEvent;
			band.setFullPath(getFullPath());
			event.setFullPath(getFullPath());
		}

		PainCycle painCycle = new PainCycle(getFullPath(), wtfYayEvent, durationInSeconds);
		painTags.addAll(painCycle.painTags);
		painCycles.add(painCycle);
	}

	public boolean containsEvent(long eventId) {
		boolean containsEvent = false;

		for (PainCycle painCycle : painCycles) {
			if (painCycle.event.getId() == eventId) {
				containsEvent = true;
				break;
			}
		}
		return containsEvent;
	}

	public void addFAQ(long eventId, String faqComment) {
		for (PainCycle discoveryCycle : painCycles) {
			if (discoveryCycle.event.getId() == eventId) {
				discoveryCycle.addFaq(faqComment);
				contextTags.addAll(discoveryCycle.contextTags);
				break;
			}
		}
	}

	public void addSnippet(long eventId, String source, String snippet) {
		for (PainCycle partialDiscovery : painCycles) {
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
		return getPainCycles().size();
	}

	@JsonIgnore
	@Override
	public List<? extends StoryElement> getChildStoryElements() {
		return painCycles;
	}


}
