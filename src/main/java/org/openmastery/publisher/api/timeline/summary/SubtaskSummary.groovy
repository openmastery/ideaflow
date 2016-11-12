package org.openmastery.publisher.api.timeline.summary

import org.openmastery.publisher.api.event.Event

class SubtaskSummary {

	IdeaFlowSummary learningSummary
	IdeaFlowSummary progressSummary
	IdeaFlowSummary troubleshootingSummary
	IdeaFlowSummary reworkSummary

	List<Event> events
}
