package org.openmastery.publisher.api.timeline.summary

import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType


class IdeaFlowSummary {

	IdeaFlowStateType ideaFlowStateType
	String description
	Long durationInSeconds

	Map<String, Double> longestDurationActivities
}
