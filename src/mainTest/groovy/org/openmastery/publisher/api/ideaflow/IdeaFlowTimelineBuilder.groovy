package org.openmastery.publisher.api.ideaflow

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.activity.BlockActivity
import org.openmastery.publisher.api.activity.ModificationActivity
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.task.Task
import org.openmastery.time.MockTimeService
import org.openmastery.time.TimeConverter

class IdeaFlowTimelineBuilder {

	private MockTimeService timeService
	private LocalDateTime startTime
	private long id = 0
	private IdeaFlowBand lastIdeaFlowBand = null
	private List<IdeaFlowBand> ideaFlowBandList = []
	private List<ModificationActivity> modificationActivityList = []
	private List<BlockActivity> blockActivityList = []
	private List<ExecutionEvent> executionEventList = []
	private List<Event> eventList = []

	IdeaFlowTimelineBuilder() {
		this(new MockTimeService())
	}

	IdeaFlowTimelineBuilder(MockTimeService timeService) {
		this.timeService = timeService
		this.startTime = timeService.now()
	}

	IdeaFlowTimelineBuilder advanceDays(int days) {
		timeService.plusDays(days)
		this
	}

	IdeaFlowTimelineBuilder advanceHours(int hours) {
		timeService.plusHours(hours)
		this
	}

	IdeaFlowTimelineBuilder advanceMinutes(int minutes) {
		timeService.plusMinutes(minutes)
		this
	}

	private Long timeSinceStartInSeconds(LocalDateTime position) {
		secondsBetween(startTime, position)
	}

	private Long secondsBetween(LocalDateTime start, LocalDateTime end) {
		TimeConverter.between(start, end).toStandardSeconds().seconds
	}

	private IdeaFlowTimelineBuilder modificationActivity(int modificationCount, LocalDateTime start, LocalDateTime end) {
		while (start.isBefore(end)) {
			start = start.plusMinutes(1)
			if (end.isBefore(start)) {
				start = end
			}

			ModificationActivity modificationActivity = new ModificationActivity()
			modificationActivity.position = start
			modificationActivity.durationInSeconds = 60
			modificationActivity.modificationCount = modificationCount
			modificationActivity.relativePositionInSeconds = timeSinceStartInSeconds(start)
			modificationActivityList << modificationActivity
		}
	}

	IdeaFlowTimelineBuilder modifyCodeHours(int hours) {
		modificationActivity(50, timeService.now(), timeService.hoursInFuture(hours))
	}

	IdeaFlowTimelineBuilder modifyCodeAndAdvanceHours(int hours) {
		modifyCodeHours(hours)
		advanceHours(hours)
	}

	IdeaFlowTimelineBuilder modifyCodeMinutes(int minutes) {
		modificationActivity(50, timeService.now(), timeService.minutesInFuture(minutes))
	}

	IdeaFlowTimelineBuilder modifyCodeAndAdvanceMinutes(int minutes) {
		modifyCodeMinutes(minutes)
		advanceMinutes(minutes)
	}

	IdeaFlowTimelineBuilder readCodeHours(int hours) {
		modificationActivity(0, timeService.now(), timeService.hoursInFuture(hours))
	}

	IdeaFlowTimelineBuilder readCodeAndAdvanceHours(int hours) {
		readCodeHours(hours)
		advanceHours(hours)
	}

	IdeaFlowTimelineBuilder readCodeMinutes(int minutes) {
		modificationActivity(0, timeService.now(), timeService.minutesInFuture(minutes))
	}

	IdeaFlowTimelineBuilder readCodeAndAdvanceMinutes(int minutes) {
		readCodeMinutes(minutes)
		advanceMinutes(minutes)
	}

	private IdeaFlowTimelineBuilder ideaFlowBand(IdeaFlowStateType type, LocalDateTime start, LocalDateTime end, boolean nested = false) {
		IdeaFlowBand ideaFlowBand = IdeaFlowBand.builder()
				.start(start)
				.end(end)
				.relativePositionInSeconds(timeSinceStartInSeconds(start))
				.durationInSeconds(secondsBetween(start, end))
				.type(type)
				.nestedBands([])
				.build()

		if (nested) {
			assert lastIdeaFlowBand != null
			assert (start.isEqual(lastIdeaFlowBand.start) || start.isAfter(lastIdeaFlowBand.start)) &&
					(end.isEqual(lastIdeaFlowBand.end) || end.isBefore(lastIdeaFlowBand.end))

			lastIdeaFlowBand.nestedBands << ideaFlowBand
		} else {
			lastIdeaFlowBand = ideaFlowBand
			ideaFlowBandList << ideaFlowBand
		}
		this
	}

	private IdeaFlowTimelineBuilder strategy(LocalDateTime start, LocalDateTime end) {
		ideaFlowBand(IdeaFlowStateType.LEARNING, start, end)
	}

	IdeaFlowTimelineBuilder strategyHours(int hours) {
		strategy(timeService.now(), timeService.hoursInFuture(hours))
	}

	IdeaFlowTimelineBuilder strategyMinutes(int minutes) {
		strategy(timeService.now(), timeService.minutesInFuture(minutes))
	}

	private IdeaFlowTimelineBuilder troubleshooting(LocalDateTime start, LocalDateTime end, boolean nested = false) {
		ideaFlowBand(IdeaFlowStateType.TROUBLESHOOTING, start, end, nested)
	}

	IdeaFlowTimelineBuilder troubleshootingHours(int hours) {
		troubleshooting(timeService.now(), timeService.hoursInFuture(hours))
	}

	IdeaFlowTimelineBuilder nestedTroubleshootingHours(int hours) {
		troubleshooting(timeService.now(), timeService.hoursInFuture(hours), true)
	}

	IdeaFlowTimelineBuilder troubleshootingMinutes(int minutes) {
		troubleshooting(timeService.now(), timeService.minutesInFuture(minutes))
	}

	IdeaFlowTimelineBuilder nestedTroubleshootingMinutes(int minutes) {
		troubleshooting(timeService.now(), timeService.minutesInFuture(minutes), true)
	}

	private void addEvent(EventType eventType) {
		Event event = new Event()
		event.id = id++
		event.position = timeService.now()
		event.relativePositionInSeconds = timeSinceStartInSeconds(timeService.now())
		event.type = eventType
		eventList << event
	}

	IdeaFlowTimelineBuilder execute() {
		ExecutionEvent executionEvent = ExecutionEvent.builder()
				.failed(false)
				.debug(false)
				.executionTaskType("JUnit")
				.processName("MyTestClass")
				.build()
		executionEvent.position = timeService.now()
		executionEvent.relativePositionInSeconds = timeSinceStartInSeconds(timeService.now())
		executionEventList << executionEvent
		this
	}

	IdeaFlowTimelineBuilder activate() {
		addEvent(EventType.ACTIVATE)
		this
	}

	IdeaFlowTimelineBuilder deactivate() {
		addEvent(EventType.DEACTIVATE)
		this
	}

	IdeaFlowTimelineBuilder wtf() {
		addEvent(EventType.WTF)
		this
	}

	IdeaFlowTimelineBuilder awesome() {
		addEvent(EventType.AWESOME)
		this
	}

	IdeaFlowTimelineBuilder subtask() {
		addEvent(EventType.SUBTASK)
		this
	}

	IdeaFlowTaskTimeline buildTaskTimeline() {
		List<Positionable> allThings = ideaFlowBandList + modificationActivityList + blockActivityList + executionEventList + eventList
		allThings.sort(PositionableComparator.INSTANCE)

		LocalDateTime start = allThings.first().position
		Positionable last = allThings.last()
		LocalDateTime end = last.position
		if (last instanceof Interval) {
			end = end.plus(last.duration)
		}

		IdeaFlowTaskTimeline.builder()
				.task(new Task(id: -1))
				.start(start)
				.end(end)
				.relativePositionInSeconds(0)
				.durationInSeconds(timeSinceStartInSeconds(end))
				.ideaFlowBands(ideaFlowBandList.sort(false, PositionableComparator.INSTANCE))
				.executionEvents(executionEventList.sort(false, PositionableComparator.INSTANCE))
				.events(eventList.sort(false, PositionableComparator.INSTANCE))
				.build()
	}

}
