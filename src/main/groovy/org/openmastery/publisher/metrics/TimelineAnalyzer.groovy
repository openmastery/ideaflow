package org.openmastery.publisher.metrics

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.GraphPoint
import org.springframework.stereotype.Component

@Component
class TimelineAnalyzer {


	List<GraphPoint<?>> analyzeTimeline(IdeaFlowTimeline timeline) {

	}

	List<GraphPoint<?>> analyzeJourneys(List<TroubleshootingJourney> journeys) {

	}
}
