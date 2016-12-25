package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.time.MockTimeService
import spock.lang.Specification

public class IdeaFlowTimelineGeneratorSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)
	private IdeaFlowTimelineGenerator timelineGenerator = new IdeaFlowTimelineGenerator()

	private IdeaFlowTimeline createTimeline() {
		timelineGenerator.events = builder.eventList
		timelineGenerator.idleTimeBands = builder.idleTimeBands
		timelineGenerator.modificationActivities = builder.modificationActivityList
		timelineGenerator.executionEvents = builder.executionEventList
		timelineGenerator.generate()
	}

	def "should create subtask at timeline start if no subtasks exist"() {
		given:
		builder.activate().advanceHours(2)
				.deactivate()

		when:
		IdeaFlowTimeline timeline = createTimeline()

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
		IdeaFlowTimeline timeline = createTimeline()

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
		IdeaFlowTimeline timeline = createTimeline()

		then:
		Event subtaskEvent = timeline.events.find { it.type == EventType.SUBTASK }
		assert subtaskEvent != null
		assert subtaskEvent.position == timeline.start
		assert subtaskEvent.relativePositionInSeconds == 0
		assert timeline.events.findAll { it.type == EventType.SUBTASK }.size() == 1
	}

}