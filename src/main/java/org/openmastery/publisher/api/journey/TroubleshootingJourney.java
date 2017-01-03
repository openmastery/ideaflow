package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;
import org.openmastery.publisher.api.metrics.DurationInSeconds;
import org.openmastery.publisher.api.metrics.Metric;
import org.openmastery.storyweb.api.ExplodableGraphPoint;

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

	long id;

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

		if (id == 0) {
			id = wtfYayEvent.getId();
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

	public void fillWithActivity(List<ExecutionEvent> executionEvents) {
		for (ExecutionEvent executionEvent : executionEvents) {
			if (overlaps(executionEvent)) {
				addExecutionEvent(executionEvent);
			}
		}

		ExperimentCycle trailingCycle = null;
		for (DiscoveryCycle discoveryCycle : discoveryCycles) {

			if (trailingCycle != null) {
				discoveryCycle.setExecutionContext(trailingCycle.getExecutionEvent());
			}
			int experimentCount = discoveryCycle.getExperimentCycles().size();
			if ( experimentCount > 0) {
				trailingCycle = discoveryCycle.getExperimentCycles().get(experimentCount - 1);
			}
		}
	}

	private void addExecutionEvent(ExecutionEvent event) {
		for (DiscoveryCycle discoveryCycle : discoveryCycles) {
			if (discoveryCycle.overlaps(event)) {
				discoveryCycle.addExperimentCycle(event);
			}
		}

	}

	public LocalDateTime getStart() {
		return band.getStart();
	}

	public LocalDateTime getEnd() {
		return band.getEnd();
	}

	private String getFirstWTFComment() {
		String comment = "";
		if (discoveryCycles.size() > 0) {
			comment = discoveryCycles.get(0).event.getComment();
		}
		return comment;
	}

	public ExplodableGraphPoint toGraphPoint() {
		ExplodableGraphPoint graphPoint = new ExplodableGraphPoint();
		graphPoint.setContextTags(contextTags);
		graphPoint.setPainTags(painTags);
		graphPoint.setRelativePath("/journey/"+ id);
		graphPoint.setDurationInSeconds(new DurationInSeconds(getDurationInSeconds()));
		graphPoint.setFrequency(discoveryCycles.size());
		graphPoint.setDescription(getFirstWTFComment());
		graphPoint.setTypeName(getClass().getSimpleName());
		graphPoint.setPosition(getStart());

		List<ExplodableGraphPoint> childPoints = new ArrayList<>();
		for (DiscoveryCycle discoveryCycle: discoveryCycles) {
			childPoints.add( discoveryCycle.toGraphPoint());
		}
		graphPoint.setExplodableGraphPoints(childPoints);
		graphPoint.forcePushTagsToChildren(graphPoint.getContextTags(), graphPoint.getPainTags());

		return graphPoint;
	}

}
