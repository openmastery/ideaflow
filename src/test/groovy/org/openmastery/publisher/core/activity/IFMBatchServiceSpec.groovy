package org.openmastery.publisher.core.activity

import com.bancvue.rest.exception.ForbiddenException
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.core.IFMBatchService
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.time.MockTimeService
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

class IFMBatchServiceSpec extends Specification {

	IFMBatchService ifmBatchService = new IFMBatchService()
	IFMBatchService.EntityBuilder entityBuilder = new IFMBatchService.EntityBuilder(-1)
	MockTimeService mockTimeService = new MockTimeService()
	InvocationContext invocationContext = Mock(InvocationContext)
	IdeaFlowPersistenceService persistenceService = Mock(IdeaFlowPersistenceService)

	def setup() {
		ifmBatchService.timeService = mockTimeService
		ifmBatchService.invocationContext = invocationContext
		ifmBatchService.persistenceService = persistenceService
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
		NewEditorActivity newActivity = aRandom.newEditorActivity().endTime(laggingClock).build()

		when:
		EditorActivityEntity actualEntity = entityBuilder.buildActivityEntity(newActivity, ifmBatchService.determineTimeAdjustment(laggingClock), EditorActivityEntity.class)

		then:
		assert actualEntity.start == mockTimeService.now().minusSeconds(newActivity.durationInSeconds)
		assert actualEntity.end == mockTimeService.now()
	}

	def "buildEvent SHOULD adjust position of event for lagging clock"() {
		given:
		LocalDateTime laggingClock = mockTimeService.now().minusSeconds(15)
		NewBatchEvent event = aRandom.newBatchEvent().position(laggingClock).build()

		when:
		EventEntity eventEntity =
				entityBuilder.buildEventEntity(event, ifmBatchService.determineTimeAdjustment(laggingClock))

		then:
		assert eventEntity.position == mockTimeService.now()

	}

	def "buildActivity SHOULD record modification times"() {
		given:
		NewEditorActivity newActivity = aRandom.newEditorActivity().endTime(mockTimeService.now()).build()

		when:
		entityBuilder.buildActivityEntity(newActivity, Duration.ofSeconds(0), EditorActivityEntity.class)

		then:
		assert entityBuilder.taskModificationDates.get(newActivity.taskId) == newActivity.endTime
	}

	def "buildEvent SHOULD record modification times"() {
		given:
		NewBatchEvent event = aRandom.newBatchEvent().position(mockTimeService.now()).build()

		when:
		entityBuilder.buildEventEntity(event, Duration.ofSeconds(0))

		then:
		assert entityBuilder.taskModificationDates.get(event.taskId) == event.position
	}

	def "recordTaskModification SHOULD save the most recent modifications"() {
		given:
		LocalDateTime oldest = mockTimeService.now().minusMinutes(30)
		LocalDateTime newest = mockTimeService.now()

		when:
		entityBuilder.recordTaskModification(5L, newest)
		entityBuilder.recordTaskModification(5L, oldest)

		then:
		assert entityBuilder.taskModificationDates.get(5L) == newest

	}

	def "recordTaskModification SHOULD throw an exception on modification once the list is retrieved"() {
		when:
		entityBuilder.taskModificationDates
		entityBuilder.recordTaskModification(5L, mockTimeService.now())

		then:
		thrown(UnsupportedOperationException)
	}

	def "addIFMBatch should throw Forbidden if task owner does not match current user"() {
		given:
		TaskEntity taskEntity = TaskEntity.builder()
				.id(2)
				.ownerId(3)
				.build()
		NewIFMBatch batch = NewIFMBatch.builder()
				.event(NewBatchEvent.builder().taskId(taskEntity.id).build())
				.build()
		invocationContext.getUserId() >> taskEntity.ownerId + 1
		persistenceService.findTaskWithId(taskEntity.id) >> taskEntity

		when:
		ifmBatchService.addIFMBatch(batch)

		then:
		thrown(ForbiddenException)
	}

}
