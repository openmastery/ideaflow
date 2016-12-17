package org.openmastery.publisher.core.activity

import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.time.MockTimeService
import org.openmastery.time.TimeConverter
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class IFMBatchServiceSpec extends Specification {

	IFMBatchService ifmBatchService
	IFMBatchService.EntityBuilder entityBuilder
	MockTimeService mockTimeService

	def setup() {
		ifmBatchService = new IFMBatchService()
		entityBuilder = new IFMBatchService.EntityBuilder(-1)
		mockTimeService = new MockTimeService()
		ifmBatchService.timeService = mockTimeService
		ifmBatchService.invocationContext = Mock(InvocationContext)
	}

	def "determineTimeAdjustment SHOULD adjust for local clock being behind"() {
		when:
		LocalDateTime laggingClock = mockTimeService.javaNow().minusSeconds(15)
		Duration adjustment = ifmBatchService.determineTimeAdjustment(laggingClock)

		then:
		assert adjustment == Duration.ofSeconds(15)
	}


	def "determineTimeAdjustment SHOULD adjust for local clock being ahead"() {
		when:
		LocalDateTime aheadClock = mockTimeService.javaNow().plusSeconds(15)
		Duration adjustment = ifmBatchService.determineTimeAdjustment(aheadClock)

		then:
		assert adjustment == Duration.ofSeconds(-15)
	}

	def "buildEntity SHOULD adjust start and end time on entity for lagging clock"() {
		given:
		LocalDateTime laggingClock = mockTimeService.javaNow().minusSeconds(15)
		NewEditorActivity newActivity = aRandom.newEditorActivity().endTime(TimeConverter.toJodaLocalDateTime(laggingClock)).build()

		when:
		EditorActivityEntity actualEntity = entityBuilder.buildActivityEntity(newActivity, ifmBatchService.determineTimeAdjustment(laggingClock), EditorActivityEntity.class)

		then:
		assert actualEntity.start == mockTimeService.javaNow().minusSeconds(newActivity.durationInSeconds)
		assert actualEntity.end == mockTimeService.javaNow()
	}

	def "buildEvent SHOULD adjust position of event for lagging clock"() {
		given:
		LocalDateTime laggingClock = mockTimeService.javaNow().minusSeconds(15)
		NewBatchEvent event = aRandom.newBatchEvent().position(TimeConverter.toJodaLocalDateTime(laggingClock)).build()

		when:
		EventEntity eventEntity =
				entityBuilder.buildEventEntity(event, ifmBatchService.determineTimeAdjustment(laggingClock))

		then:
		assert eventEntity.position == mockTimeService.javaNow()

	}

	def "buildActivity SHOULD record modification times"() {
		given:
		NewEditorActivity newActivity = aRandom.newEditorActivity().endTime(mockTimeService.now()).build()

		when:
		entityBuilder.buildActivityEntity(newActivity, Duration.ofSeconds(0), EditorActivityEntity.class)

		then:
		assert entityBuilder.taskModificationDates.get(newActivity.taskId) != newActivity.endTime
	}

	def "buildEvent SHOULD record modification times"() {
		given:
		NewBatchEvent event = aRandom.newBatchEvent().position(mockTimeService.now()).build()

		when:
		entityBuilder.buildEventEntity(event, Duration.ofSeconds(0))

		then:
		assert entityBuilder.taskModificationDates.get(event.taskId) != event.position
	}

	def "recordTaskModification SHOULD save the most recent modifications"() {
		given:
		LocalDateTime oldest = mockTimeService.javaNow().minusMinutes(30)
		LocalDateTime newest = mockTimeService.javaNow()

		when:
		entityBuilder.recordTaskModification(5L, newest)
		entityBuilder.recordTaskModification(5L, oldest)

		then:
		assert entityBuilder.taskModificationDates.get(5L) == newest

	}

	def "recordTaskModification SHOULD throw an exception on modification once the list is retrieved"() {
		when:
		entityBuilder.taskModificationDates
		entityBuilder.recordTaskModification(5L, mockTimeService.javaNow())

		then:
		thrown(UnsupportedOperationException)
	}
}
