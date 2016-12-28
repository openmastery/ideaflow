package org.openmastery.publisher.api.journey;

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

	Event progressEvent;

	CapacityDistribution capacityDistribution;
	List<TroubleshootingJourney> troubleshootingJourneys = new ArrayList<TroubleshootingJourney>();
	List<DangerLink> dangerLinks = new ArrayList<DangerLink>();


	public ProgressMilestone(Event progressEvent) {
		this.progressEvent = progressEvent;
		setRelativeStart(progressEvent.getRelativePositionInSeconds());
	}

	public void addJourney(TroubleshootingJourney journey) {
		addDangerLinks(journey);
		troubleshootingJourneys.add(journey);
	}

	public LocalDateTime getStart() {
		return progressEvent.getPosition();
	}

	public String getDescription() {
		return progressEvent.getComment();
	}

	private void addDangerLinks(TroubleshootingJourney journey) {

		for (Metric<?> metric : journey.getMetrics()) {
			if (metric.isDanger()) {
				DangerLink dangerLink = new DangerLink();
				dangerLink.setEventId(journey.getEventId());
				dangerLink.setRelativePositionInSeconds(journey.getRelativeStart());
				dangerLink.setMetric(metric);
				dangerLinks.add(dangerLink);
			}
		}
	}




}
