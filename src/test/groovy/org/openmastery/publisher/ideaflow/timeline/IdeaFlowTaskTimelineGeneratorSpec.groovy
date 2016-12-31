package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimelineValidator
import org.openmastery.publisher.api.task.Task
import org.openmastery.time.MockTimeService
import spock.lang.Specification

import java.time.Duration

public class IdeaFlowTaskTimelineGeneratorSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)
	private IdeaFlowTaskTimelineGenerator timelineGenerator = new IdeaFlowTaskTimelineGenerator()

	private IdeaFlowTaskTimeline createTimeline() {
		timelineGenerator.events = builder.eventList
		timelineGenerator.idleTimeBands = builder.idleTimeBands
		timelineGenerator.modificationActivities = builder.modificationActivityList
		timelineGenerator.executionEvents = builder.executionEventList
		timelineGenerator.task(new Task(id: 4))
		timelineGenerator.generate()
	}

	def "should create subtask at timeline start if no subtasks exist"() {
		given:
		builder.activate().advanceHours(2)
				.deactivate()

		when:
		IdeaFlowTaskTimeline timeline = createTimeline()

		then:
		Event subtaskEvent = timeline.events.find { it.type == EventType.SUBTASK }
		assert subtaskEvent != null
		assert subtaskEvent.position == timeline.start
		assert subtaskEvent.relativePositionInSeconds == 0
	}

	def "should create subtask at timeline start if subtask exists"() {
		given:
		builder.activate().advanceHours(2)
				.subtask()
				.advanceHours(2)
				.deactivate()

		when:
		IdeaFlowTaskTimeline timeline = createTimeline()

		then:
		Event subtaskEvent = timeline.events.find { it.type == EventType.SUBTASK }
		assert subtaskEvent != null
		assert subtaskEvent.position == timeline.start
		assert subtaskEvent.relativePositionInSeconds == 0
		assert timeline.events.findAll { it.type == EventType.SUBTASK }.size() == 2
	}

	def "should not create subtask if subtask exists at timeline start"() {
		given:
		builder.activate()
				.subtask()
				.advanceHours(2)
				.deactivate()

		when:
		IdeaFlowTaskTimeline timeline = createTimeline()

		then:
		Event subtaskEvent = timeline.events.find { it.type == EventType.SUBTASK }
		assert subtaskEvent != null
		assert subtaskEvent.position == timeline.start
		assert subtaskEvent.relativePositionInSeconds == 0
		assert timeline.events.findAll { it.type == EventType.SUBTASK }.size() == 1
	}

	def "should interpret deactivation/activation as idle"() {
		LocalDateTime startTime = mockTimeService.now()
		builder.activate()
				.modifyCodeAndAdvance(30)
				.deactivate()
				.advanceHours(1)
				.activate()
				.modifyCodeAndAdvance(33)
				.readCodeAndAdvance(27)
				.deactivate()

		when:
		IdeaFlowTaskTimeline timeline = createTimeline()
		IdeaFlowTimelineValidator validator = new IdeaFlowTimelineValidator(timeline)

		then:
		validator.assertEvents(2, EventType.ACTIVATE)
		validator.assertEvents(1, EventType.DEACTIVATE)
		validator.assertEvents(1, EventType.SUBTASK)
		validator.assertProgressBand(0, startTime, startTime.plusHours(2))
				.assertRelativePositionInSeconds(0)
				.assertDurationInSeconds(Duration.ofHours(1).seconds)
		validator.assertStrategyBand(1, startTime.plusHours(2), startTime.plusHours(2).plusMinutes(30))
				.assertRelativePositionInSeconds(Duration.ofHours(1).seconds)
				.assertDurationInSeconds(Duration.ofMinutes(30).seconds)
		validator.assertValidationComplete()
	}

}