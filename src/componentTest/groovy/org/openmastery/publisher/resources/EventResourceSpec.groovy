package org.openmastery.publisher.resources

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.annotation.FAQAnnotation
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.testsupport.BeanCompare
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.beans.PersistenceDelegate

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class EventResourceSpec extends Specification {

	@Autowired
	private EventClient eventClient
	@Autowired
	private BatchClient batchClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Autowired
	private TimeService timeService

	private Long taskId

	def setup() {
		TaskEntity taskEntity = aRandom.taskEntity().build()
		taskId = persistenceService.saveTask(taskEntity).id
	}

	def "Should retrieve recent events equal or after the specified afterDate"() {
		given:
		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newEvent(taskId, timeService.now(), EventType.AWESOME, "YAY!")
				.newEvent(taskId, timeService.now(), EventType.WTF, "WTF?")
				.build()
		batchClient.addIFMBatch(batch)

		when:
		List<Event> eventList = eventClient.getRecentEvents(timeService.now().minusDays(2), 5)

		then:
		assert eventList.size() == 2
	}

	def "Should update event with PUT"() {
		given:
		EventEntity eventEntity = aRandom.eventEntity().build()
		persistenceService.saveEvent(eventEntity)
		EntityMapper mapper = new EntityMapper()
		Event event = mapper.mapIfNotNull(eventEntity, Event.class)

		when:
		Event savedEvent = eventClient.updateEvent(event)

		then:
		assert savedEvent != null
	}

	def "Should update FAQ on POST for existing event"() {
		given:
		EventEntity eventEntity = aRandom.eventEntity().build()
		EventEntity savedEntity = persistenceService.saveEvent(eventEntity)

		FAQAnnotation faqAnnotation = new FAQAnnotation(savedEntity.id, "My FAQ!")

		when:
		FAQAnnotation savedAnnotation = eventClient.annotateWithFAQ(faqAnnotation)

		then:
		assert savedAnnotation != null
	}

	def "Should overwrite FAQ on POST if annotation already exists"() {
		given:
		EventEntity eventEntity = aRandom.eventEntity().build()
		EventEntity savedEntity = persistenceService.saveEvent(eventEntity)

		FAQAnnotation faqAnnotation = new FAQAnnotation(savedEntity.id, "My FAQ!")

		when:
		eventClient.annotateWithFAQ(faqAnnotation)
		faqAnnotation.faq = "Modified!"

		FAQAnnotation updatedAnnotation = eventClient.annotateWithFAQ(faqAnnotation)

		then:
		assert updatedAnnotation != null
		assert updatedAnnotation == faqAnnotation
	}

}
