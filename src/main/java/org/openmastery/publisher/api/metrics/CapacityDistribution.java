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
class CapacityDistribution {

	Map<IdeaFlowStateType, Long> timeSpentPerStateType = new HashMap<IdeaFlowStateType, Long>();

	void addTime(IdeaFlowStateType ideaFlowStateType, Long durationInSeconds) {
		Long existingTime = timeSpentPerStateType.get(ideaFlowStateType);

		if (existingTime == null) {
			timeSpentPerStateType.put(ideaFlowStateType, durationInSeconds);
		} else {
			timeSpentPerStateType.put(ideaFlowStateType, existingTime + durationInSeconds);
		}
	}
}
