package org.openmastery.publisher.resources

import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
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
		List<NewBatchEvent> eventList = eventClient.getRecentEvents(timeService.now().minusDays(2), 5)

		then:
		assert eventList.size() == 2
	}

}
