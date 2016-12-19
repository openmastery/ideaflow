package org.openmastery.publisher.metrics.calculator

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.IdeaFlowMetrics


public interface MetricsCalculator {

	IdeaFlowMetrics calculateMetrics(IdeaFlowTimeline timeline);
}
