package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.PositionableAndIntervalListBuilder
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.time.MockTimeService;
import spock.lang.Specification

import java.time.LocalDateTime;

public class CalendarEventGeneratorSpec extends Specification {

	MockTimeService mockTimeService = new MockTimeService()
	LocalDateTime startTime = mockTimeService.now().plusHours(1)
	PositionableAndIntervalListBuilder builder = new PositionableAndIntervalListBuilder(mockTimeService)

	private List<Event> generateCalendarEvents() {
		List<Interval> intervals = builder.buildIntervals()
		List<Event> events = new CalendarEventGenerator().generateCalendarEvents(intervals)
		events.each { Event event ->
			assert event.type == EventType.CALENDAR
		}
		events
	}

	private Event createCalendarEventFromStart(int days) {
		LocalDateTime startTime = startTime.plusDays(days).toLocalDate().atStartOfDay()
		Event event = new Event()
		event.setType(EventType.CALENDAR)
		event.setPosition(startTime)
		event.setFullPath("/calendar/" + startTime.toLocalDate().toString().trim())
		return event
	}

	def "should create calendar event at midnight of new day after idle"() {
		given:
		builder.interval(8, 16)
				.idle(16, 32)
				.interval(32, 40)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events[0] == createCalendarEventFromStart(1)
		assert events.size() == 1
	}

	def "should create calendar event at midnight of first day after idle if idle spans multiple days"() {
		given:
		builder.interval(8, 16)
				.idle(16, 56)
				.interval(56, 64)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events[0] == createCalendarEventFromStart(2)
		assert events.size() == 1
	}

	def "should create calendar event at midnight of first non-idle position if idle spans day"() {
		given:
		builder.interval(8, 16)
				.idle(16, 32)
				.idle(32, 36)
				.interval(36, 40)
				.interval(40, 44)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events[0] == createCalendarEventFromStart(1)
		assert events.size() == 1
	}

	def "should not create calendar event if idle is last interval"() {
		given:
		builder.interval(8, 16)
				.idle(16, 32)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events.size() == 0
	}

	def "should create calendar event at midnight if interval spans day"() {
		given:
		builder.interval(8, 16)
				.idle(16, 20)
				.interval(20, 28)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events[0] == createCalendarEventFromStart(1)
		assert events.size() == 1
	}

	def "should create calendar event at midnight on each day if interval spans multiple days"() {
		given:
		builder.interval(8, 16)
				.idle(16, 20)
				.interval(20, 50)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events[0] == createCalendarEventFromStart(1)
		assert events[1] == createCalendarEventFromStart(2)
		assert events.size() == 2
	}

	def "should create calendar event at midnight of last day if interval spans multiple days with idle"() {
		given:
		builder.interval(12, 60)
				.idle(14, 58)
				.interval(58, 60)

		when:
		List<Event> events = generateCalendarEvents()

		then:
		assert events[0] == createCalendarEventFromStart(2)
		assert events.size() == 1
	}

}