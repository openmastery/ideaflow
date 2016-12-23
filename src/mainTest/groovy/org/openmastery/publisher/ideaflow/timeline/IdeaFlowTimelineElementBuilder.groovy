package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.Duration
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.time.MockTimeService
import org.openmastery.time.TimeConverter


class IdeaFlowTimelineElementBuilder {

	private MockTimeService timeService
	List<Event> eventList = []
	List<ModificationActivity> modificationActivityList = []
	List<IdleTimeBandModel> idleTimeBands = []
	List<ExecutionEvent> executionEventList = []

	LocalDateTime activationTime

	IdeaFlowTimelineElementBuilder() {
		this(new MockTimeService())
	}

	IdeaFlowTimelineElementBuilder(MockTimeService timeService) {
		this.timeService = timeService
	}

	IdeaFlowTimelineElementBuilder advanceDays(int days) {
		timeService.plusDays(days)
		this
	}

	IdeaFlowTimelineElementBuilder advanceHours(int hours) {
		timeService.plusHours(hours)
		this
	}

	IdeaFlowTimelineElementBuilder advanceMinutes(int minutes) {
		timeService.plusMinutes(minutes)
		this
	}

	IdeaFlowTimelineElementBuilder readCode(int minutes) {
		for (int i = 0; i < minutes; i++) {
			ModificationActivity modificationActivity = new ModificationActivity()
			modificationActivity.position = timeService.now().plusMinutes(i)
			modificationActivity.durationInSeconds = 60
			modificationActivity.modificationCount = 0
			modificationActivityList << modificationActivity
		}
		this
	}

	IdeaFlowTimelineElementBuilder readCodeAndAdvance(int minutes) {
		readCode(minutes)
		advanceMinutes(minutes)
	}

	IdeaFlowTimelineElementBuilder modifyCode(int minutes) {
		for (int i = 0; i < minutes; i++) {
			ModificationActivity modificationActivity = new ModificationActivity()
			modificationActivity.position = timeService.now().plusMinutes(i)
			modificationActivity.durationInSeconds = 60
			modificationActivity.modificationCount = 50
			modificationActivityList << modificationActivity
		}
		this
	}

	IdeaFlowTimelineElementBuilder executeCode() {
		ExecutionEvent event = ExecutionEvent.builder()
			.failed(false)
			.debug(false)
			.executionTaskType("JUnit")
			.processName("MyTestClass")
			.build()
		event.setStart(timeService.now())
		event.relativePositionInSeconds = TimeConverter.between(activationTime, timeService.now()).standardSeconds

		executionEventList << event

		return this
	}

	IdeaFlowTimelineElementBuilder modifyCodeAndAdvance(int minutes) {
		modifyCode(minutes)
		advanceMinutes(minutes)
	}

	private void addEvent(EventType eventType) {
		Event event = new Event()
		event.position = timeService.now()
		event.type = eventType
		eventList << event
	}

	IdeaFlowTimelineElementBuilder activate() {
		addEvent(EventType.ACTIVATE)
		activationTime = timeService.now()
		this
	}

	IdeaFlowTimelineElementBuilder deactivate() {
		addEvent(EventType.DEACTIVATE)
		this
	}

	IdeaFlowTimelineElementBuilder wtf() {
		addEvent(EventType.WTF)
		this
	}

	IdeaFlowTimelineElementBuilder awesome() {
		addEvent(EventType.AWESOME)
		this
	}

}
