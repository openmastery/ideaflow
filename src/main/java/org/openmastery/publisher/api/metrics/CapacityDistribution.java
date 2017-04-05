package org.openmastery.publisher.api.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapacityDistribution {


	Map<IdeaFlowStateType, Entry> capacityDistributionByType = new HashMap<IdeaFlowStateType, Entry>();

	public void addDurationForType(IdeaFlowStateType ideaFlowStateType, Long durationInSeconds) {
		Entry existingEntry = capacityDistributionByType.get(ideaFlowStateType);

		if (existingEntry == null) {
			Entry entry = new Entry();
			entry.durationInSeconds = durationInSeconds;
			capacityDistributionByType.put(ideaFlowStateType, entry);
		} else {
			existingEntry.durationInSeconds = existingEntry.durationInSeconds + durationInSeconds;
		}
	}

	public void calculatePercentages() {
		long sumDuration = 0L;
		for (Entry capacityEntry : capacityDistributionByType.values()) {
			sumDuration += capacityEntry.durationInSeconds;
		}

		for (Entry capacityEntry : capacityDistributionByType.values()) {
			capacityEntry.percentCapacity = 100 * capacityEntry.durationInSeconds / sumDuration;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Entry {
		Long durationInSeconds;
		Long percentCapacity;
	}

}
