package org.ideaflow.publisher.resources

import org.openmastery.testsupport.BeanCompare
import org.ideaflow.publisher.ComponentTest
import org.ideaflow.publisher.api.event.EventType
import org.ideaflow.publisher.client.EventClient
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class EventResourceSpec extends Specification {

	@Autowired
	private EventClient eventClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BeanCompare eventComparator = new BeanCompare().excludeFields("id", "position")
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
		eventClient.addUserNote(taskId, "user note")

		then:
		assertEventPosted(EventType.NOTE, "user note")
	}

	def "SHOULD post subtask"() {
		when:
		eventClient.startSubtask(taskId, "subtask")

		then:
		assertEventPosted(EventType.SUBTASK, "subtask")
	}

}
