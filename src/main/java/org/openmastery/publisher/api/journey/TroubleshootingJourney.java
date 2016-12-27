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

		partialDiscoveries.add(partialDiscovery);
	}

	public void fillWithActivity(List<ExecutionEvent> executionEvents) {
		for (ExecutionEvent executionEvent : executionEvents) {
			if (shouldContain(executionEvent)) {
				addExecutionEvent(executionEvent);
			}
		}
	}

	private void addExecutionEvent(ExecutionEvent event) {
		for (PartialDiscovery experiment : partialDiscoveries) {
			if (experiment.shouldContain(event)) {
				experiment.addExecutionEvent(event);
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
