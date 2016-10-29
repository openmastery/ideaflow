package org.openmastery.publisher.core.activity

import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.time.MockTimeService
import org.openmastery.time.TimeConverter
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class ActivityServiceSpec extends Specification {

	ActivityService activityService
	MockTimeService mockTimeService

	def setup() {
		activityService = new ActivityService()
		mockTimeService = new MockTimeService()
		activityService.timeService = mockTimeService
	}

	def "determineTimeAdjustment SHOULD adjust for local clock being behind"() {
		when:
		LocalDateTime laggingClock = mockTimeService.now().minusSeconds(15)
		Duration adjustment = activityService.determineTimeAdjustment(laggingClock)

		then:
		assert adjustment == Duration.ofSeconds(15)
	}


	def "determineTimeAdjustment SHOULD adjust for local clock being ahead"() {
		when:
		LocalDateTime aheadClock = mockTimeService.now().plusSeconds(15)
		Duration adjustment = activityService.determineTimeAdjustment(aheadClock)

		then:
		assert adjustment == Duration.ofSeconds(-15)
	}

	def "buildEntity SHOULD adjust start and end time on entity for lagging clock"() {
		given:
		LocalDateTime laggingClock = mockTimeService.now().minusSeconds(15)
		NewEditorActivity newActivity = aRandom.newActivity().endTime(TimeConverter.toJodaLocalDateTime(laggingClock)).build()

		when:
		EditorActivityEntity actualEntity =
				activityService.buildEntity(newActivity, activityService.determineTimeAdjustment(laggingClock), EditorActivityEntity.class)

		then:
		assert actualEntity.start == mockTimeService.now().minusSeconds(newActivity.durationInSeconds)
		assert actualEntity.end == mockTimeService.now()
	}
}
