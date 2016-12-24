package org.openmastery.publisher.api.journey;

import lombok.*;
import org.openmastery.publisher.api.AbstractRelativeInterval;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;
import org.openmastery.publisher.api.metrics.TimelineMetrics;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
public class TroubleshootingJourney extends AbstractRelativeInterval {

	Long id;
	IdeaFlowBand band;

	List<String> tags; //derived from WTF/YAY #hashtags

	List<Experiment> experiments;
	TimelineMetrics metrics;

	public TroubleshootingJourney(IdeaFlowBand band) {
		this.band = band;
		setRelativeStart(band.getRelativePositionInSeconds());
		setDurationInSeconds(band.getDurationInSeconds());
	}

	public void addExperiment(Experiment experiment) {
		if (experiments == null) {
			experiments = new ArrayList<Experiment>();
		}
		experiments.add(experiment);
	}


	public void addExecutionEvent(ExecutionEvent event) {
		for (Experiment experiment : experiments ) {
			if (experiment.shouldContain(event)) {
				experiment.addExecutionEvent(event);
			}
		}

	}
}
