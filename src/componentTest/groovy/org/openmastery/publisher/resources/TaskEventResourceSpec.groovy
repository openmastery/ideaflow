package org.openmastery.publisher.resources

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.annotation.FAQAnnotation
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.client.TaskEventClient
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.user.UserEntity
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class TaskEventResourceSpec extends Specification {

	@Autowired
	private TaskEventClient eventClient
	@Autowired
	private BatchClient batchClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Autowired
	private TimeService timeService

	private Long taskId

	@Autowired
	public UserEntity testUser


	def setup() {
		TaskEntity taskEntity = aRandom.taskEntity().build()
		taskEntity.ownerId = testUser.id
		taskId = persistenceService.saveTask(taskEntity).id
	}

	private EventEntity createRandomEvent() {
		aRandom.eventEntity().taskId(taskId).ownerId(testUser.id).build()
	}



	def "Should update event description with PUT"() {
		given:
		EventEntity eventEntity = createRandomEvent()
		eventEntity.type = EventType.SUBTASK
		eventEntity = persistenceService.saveEvent(eventEntity)

		when:
		Event savedEvent = eventClient.updateEventDescription("/task/id/$taskId/subtask/${eventEntity.id}", "hello" );

		then:
		assert savedEvent != null
		assert savedEvent.comment == "hello"
	}

	def "Should update FAQ for existing event"() {
		given:
		EventEntity eventEntity = createRandomEvent()
		eventEntity.type = EventType.SUBTASK
		eventEntity = persistenceService.saveEvent(eventEntity)

		when:
		Event savedEvent = eventClient.updateEventFaq("/task/id/$taskId/subtask/${eventEntity.id}", "My FAQ!")

		then:
		assert savedEvent != null

	}
}
