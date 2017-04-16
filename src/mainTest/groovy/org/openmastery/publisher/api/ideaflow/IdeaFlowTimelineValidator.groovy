package org.openmastery.publisher.api.ideaflow

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.testsupport.BeanCompare

import java.time.LocalDateTime

class IdeaFlowTimelineValidator {

	private BeanCompare beanCompare = new BeanCompare()
	private def timeline
	private boolean executionEventsValidated = false
	private List<IdeaFlowBand> validatedIdeaFlowBands = []
	private List<Event> validatedEvents = []

	IdeaFlowTimelineValidator(IdeaFlowTaskTimeline timeline) {
		this.timeline = timeline
	}

	IdeaFlowTimelineValidator(IdeaFlowSubtaskTimeline timeline) {
		this.timeline = timeline
	}

	private IdeaFlowBandValidator assertBand(int index, IdeaFlowStateType expectedType, LocalDateTime expectedStartTime, LocalDateTime expectedEndTime) {
		validatedIdeaFlowBands << timeline.ideaFlowBands[index]
		new IdeaFlowBandValidator(timeline.ideaFlowBands[index]).assertExpectedValues(expectedType, expectedStartTime, expectedEndTime)
	}

	IdeaFlowBandValidator assertProgressBand(int index, LocalDateTime expectedStartTime, LocalDateTime expectedEndType) {
		assertBand(index, IdeaFlowStateType.PROGRESS, expectedStartTime, expectedEndType)
	}

	IdeaFlowBandValidator assertStrategyBand(int index, LocalDateTime expectedStartTime, LocalDateTime expectedEndType) {
		assertBand(index, IdeaFlowStateType.LEARNING, expectedStartTime, expectedEndType)
	}

	IdeaFlowBandValidator assertTroubleshootingBand(int index, LocalDateTime expectedStartTime, LocalDateTime expectedEndType) {
		assertBand(index, IdeaFlowStateType.TROUBLESHOOTING, expectedStartTime, expectedEndType)
	}

	void assertEvents(int eventCount, EventType eventType) {
		List<Event> matchingEvents = timeline.events.findAll { it.type == eventType }
		validatedEvents.addAll(matchingEvents)
	}

	void assertExecutionEvents(int executionCount) {
		assert timeline.executionEvents.size() == executionCount
		executionEventsValidated = true
	}

	void assertValidationComplete() {
		beanCompare.assertEquals(validatedIdeaFlowBands, timeline.ideaFlowBands)
		beanCompare.assertEquals(validatedEvents, timeline.events)
		if (executionEventsValidated == false) {
			assert timeline.executionEvents.size() == 0: "Timeline contains execution activity but was not validated, eventCount=${timeline.executionEvents.size()}"
		}
	}

}
