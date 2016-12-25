package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.Event;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.TimelineMetrics;

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

	Long id;
	IdeaFlowBand band;

	Set<String> tags; //derived from WTF/YAY #hashtags

	List<Experiment> experiments;
	TimelineMetrics metrics;

	public TroubleshootingJourney(IdeaFlowBand band) {
		this.band = band;
		setRelativeStart(band.getRelativePositionInSeconds());
		setDurationInSeconds(band.getDurationInSeconds());

		this.experiments = new ArrayList<Experiment>();
		this.tags = new HashSet<String>();
	}

	public void addExperiment(Event wtfYayEvent, Long durationInSeconds) {
		Experiment experiment = new Experiment(wtfYayEvent, durationInSeconds);
		tags.addAll(experiment.tags);

		experiments.add(experiment);
	}


	public void fillWithActivity(List<ExecutionEvent> executionEvents) {
		for (ExecutionEvent executionEvent : executionEvents) {
			if (shouldContain(executionEvent)) {
				addExecutionEvent(executionEvent);
			}
		}
	}

	private void addExecutionEvent(ExecutionEvent event) {
		for (Experiment experiment : experiments ) {
			if (experiment.shouldContain(event)) {
				experiment.addExecutionEvent(event);
			}
		}

	}
}
