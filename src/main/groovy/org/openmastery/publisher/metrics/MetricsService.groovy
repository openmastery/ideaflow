package org.openmastery.publisher.metrics

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.IdeaFlowMetrics
import org.openmastery.publisher.api.metrics.IdeaFlowMetricsCalculator
import org.springframework.stereotype.Component

@Component
class MetricsService {

	IdeaFlowMetrics calculateMetrics(IdeaFlowTimeline timeline, IdeaFlowMetricsCalculator calculator) {
		//implement me for subtask metrics
	}
}
