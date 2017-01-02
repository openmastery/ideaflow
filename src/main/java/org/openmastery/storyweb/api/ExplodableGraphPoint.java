package org.openmastery.storyweb.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openmastery.publisher.api.journey.DiscoveryCycle;
import org.openmastery.publisher.api.journey.TroubleshootingJourney;
import org.openmastery.publisher.api.metrics.DurationInSeconds;
import org.openmastery.publisher.api.task.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ExplodableGraphPoint {


	Task task;

	Set<String> painTags;
	Set<String> contextTags;

	Set<String> allTags; //derived

	List<TroubleshootingJourney> troubleshootingJourneys;

	DurationInSeconds durationInSeconds;

	int totalFirstDegree;
	int totalSecondDegree;
	int totalThirdDegree;

	public ExplodableGraphPoint(Task task, List<TroubleshootingJourney> journeys) {
		allTags = new HashSet<String>();
		contextTags = new HashSet<String>();
		painTags = new HashSet<String>();

		durationInSeconds = new DurationInSeconds(0);

		this.task = task;
		troubleshootingJourneys = journeys;

		for (TroubleshootingJourney journey : journeys) {
			addContextTags(journey.getContextTags());
			addPainTags(journey.getPainTags());
			incrementFrequencyCounters(journey);
		}
	}

	private void incrementFrequencyCounters(TroubleshootingJourney journey) {
		totalFirstDegree++;

		totalSecondDegree += journey.getDiscoveryCycles().size();

		for (DiscoveryCycle discovery : journey.getDiscoveryCycles()) {
			totalThirdDegree += discovery.getExperimentCycles().size();
		}

		durationInSeconds.incrementBy(journey.getDurationInSeconds());
	}

	private void addContextTags(Set<String> contextTags) {
		this.contextTags.addAll(contextTags);
		this.allTags.addAll(contextTags);
	}

	private void addPainTags(Set<String> painTags) {
		this.painTags.addAll(painTags);
		this.allTags.addAll(painTags);
	}


}
