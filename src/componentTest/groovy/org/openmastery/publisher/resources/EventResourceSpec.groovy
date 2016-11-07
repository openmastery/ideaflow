package org.openmastery.publisher.resources

import org.openmastery.testsupport.BeanCompare
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class EventResourceSpec extends Specification {

	@Autowired
	private EventClient eventClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BeanCompare eventComparator = new BeanCompare().excludeFields("id", "ownerId", "position")
	private long taskId = 123

	private void assertEventPosted(EventType expectedType, String comment) {
		EventEntity expectedEvent = EventEntity.builder()
				.taskId(taskId)
				.type(expectedType)
				.comment(comment)
				.build()

		EventEntity actualEvent = persistenceService.getEventList(taskId).last()
		eventComparator.assertEquals(expectedEvent, actualEvent)
		assert actualEvent.id != null
		assert actualEvent.position != null
	}

	def "SHOULD post user note"() {
		when:
		eventClient.createEvent(taskId, EventType.NOTE, "user note")

		then:
		assertEventPosted(EventType.NOTE, "user note")
	}

	def "SHOULD post subtask"() {
		when:
		eventClient.createEvent(taskId, EventType.SUBTASK, "subtask")

		then:
		assertEventPosted(EventType.SUBTASK, "subtask")
	}

	def "SHOULD post WTF"() {
		when:
		eventClient.createEvent(taskId, EventType.WTF, "WTF?!")

		then:
		assertEventPosted(EventType.WTF, "WTF?!")
	}

	def "SHOULD post awesome"() {
		when:
		eventClient.createEvent(taskId, EventType.AWESOME, "YAY!")

		then:
		assertEventPosted(EventType.AWESOME, "YAY!")
	}

}
