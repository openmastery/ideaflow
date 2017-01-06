package org.openmastery.publisher.api.journey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.metrics.CapacityDistribution;
import org.openmastery.publisher.api.metrics.Metric;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProgressMilestone extends AbstractRelativeInterval {

	@JsonIgnore
	Event event;

	public Long getId() {
		return event.getId();
	}

	String getRelativePath() {
		return "/milestone/"+event.getId();
	}

	LocalDateTime getPosition() {
		return event.getPosition();
	}

	String getDescription() {
		return event.getComment();
	}

	CapacityDistribution capacityDistribution;
	List<TroubleshootingJourney> troubleshootingJourneys = new ArrayList<TroubleshootingJourney>();

	public ProgressMilestone(Event progressEvent) {
		this.event = progressEvent;
		setRelativeStart(progressEvent.getRelativePositionInSeconds());
	}

	public void addJourney(TroubleshootingJourney journey) {
		troubleshootingJourneys.add(journey);
	}

	@JsonIgnore
	public LocalDateTime getStart() {
		return event.getPosition();
	}





}
