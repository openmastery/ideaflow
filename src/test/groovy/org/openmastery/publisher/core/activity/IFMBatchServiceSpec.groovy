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
	MockTimeService mockTimeService

	def setup() {
		ifmBatchService = new IFMBatchService()
		mockTimeService = new MockTimeService()
		ifmBatchService.timeService = mockTimeService
		ifmBatchService.invocationContext = Mock(InvocationContext)
	}

	def "determineTimeAdjustment SHOULD adjust for local clock being behind"() {
		when:
		LocalDateTime laggingClock = mockTimeService.now().minusSeconds(15)
		Duration adjustment = ifmBatchService.determineTimeAdjustment(laggingClock)

		then:
		assert adjustment == Duration.ofSeconds(15)
	}


	def "determineTimeAdjustment SHOULD adjust for local clock being ahead"() {
		when:
		LocalDateTime aheadClock = mockTimeService.now().plusSeconds(15)
		Duration adjustment = ifmBatchService.determineTimeAdjustment(aheadClock)

		then:
		assert adjustment == Duration.ofSeconds(-15)
	}

	def "buildEntity SHOULD adjust start and end time on entity for lagging clock"() {
		given:
		LocalDateTime laggingClock = mockTimeService.now().minusSeconds(15)
		NewEditorActivity newActivity = aRandom.newEditorActivity().endTime(TimeConverter.toJodaLocalDateTime(laggingClock)).build()

		when:
		EditorActivityEntity actualEntity =
				ifmBatchService.buildActivityEntity(newActivity, ifmBatchService.determineTimeAdjustment(laggingClock), EditorActivityEntity.class)

		then:
		assert actualEntity.start == mockTimeService.now().minusSeconds(newActivity.durationInSeconds)
		assert actualEntity.end == mockTimeService.now()
	}

	def "buildEvent SHOULD adjust position of event for lagging clock"() {
		given:
		LocalDateTime laggingClock = mockTimeService.now().minusSeconds(15)
		NewBatchEvent event = aRandom.newBatchEvent().endTime(TimeConverter.toJodaLocalDateTime(laggingClock)).build()

		when:
		EventEntity eventEntity =
				ifmBatchService.buildEventEntity(event, ifmBatchService.determineTimeAdjustment(laggingClock))

		then:
		assert eventEntity.position == mockTimeService.now()

	}
}
