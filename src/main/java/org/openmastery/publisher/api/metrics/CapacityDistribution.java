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

	Map<IdeaFlowStateType, Long> capacityDistributionByType = new HashMap<IdeaFlowStateType, Long>();

	public void addTotalDurationForType(IdeaFlowStateType ideaFlowStateType, Long durationInSeconds) {
		Long existingTime = capacityDistributionByType.get(ideaFlowStateType);

		if (existingTime == null) {
			capacityDistributionByType.put(ideaFlowStateType, durationInSeconds);
		} else {
			capacityDistributionByType.put(ideaFlowStateType, existingTime + durationInSeconds);
		}
	}
}
