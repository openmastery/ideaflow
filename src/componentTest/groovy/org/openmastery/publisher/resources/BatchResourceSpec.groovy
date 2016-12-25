package org.openmastery.publisher.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.testsupport.BeanCompare
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.Duration

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class BatchResourceSpec extends Specification {

	@Autowired
	private BatchClient client
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	@Autowired
	private TimeService timeService
	private BeanCompare comparator = new BeanCompare().excludeFields("id", "ownerId", "metadata", "metadataContainer")

	private Long taskId

	def setup() {
		TaskEntity taskEntity = aRandom.taskEntity().build()
		taskId = persistenceService.saveTask(taskEntity).id
	}

	def "SHOULD post editor activity"() {
		given:
		Duration expectedDuration = aRandom.duration()
		EditorActivityEntity expectedActivity = aRandom.editorActivityEntity()
				.start(timeService.javaNow().minus(expectedDuration))
				.end(timeService.javaNow())
				.taskId(taskId)
				.build()

		NewIFMBatch batch = aRandom.batch()
			.timeSent(timeService.now())
			.newEditorActivity(taskId, timeService.now(), expectedDuration.seconds, expectedActivity.filePath, expectedActivity.modified)
			.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<EditorActivityEntity> activityEntities = persistenceService.getEditorActivityList(expectedActivity.taskId)
		comparator.assertEquals(expectedActivity, activityEntities.last())
		assert activityEntities.last().id != null
	}

	def "SHOULD post idle activity"() {
		given:
		Duration expectedDuration = aRandom.duration()
		IdleActivityEntity expectedIdle = aRandom.idleActivityEntity()
				.start(timeService.javaNow().minus(expectedDuration))
				.end(timeService.javaNow())
				.taskId(taskId)
				.build()

		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newIdleActivity(expectedIdle.taskId, timeService.now(), expectedDuration.seconds)
				.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<IdleActivityEntity> idleEntities = persistenceService.getIdleActivityList(expectedIdle.taskId)
		comparator.assertEquals(expectedIdle, idleEntities.last())
		assert idleEntities.last().id != null
	}

	def "SHOULD post external activity"() {
		Duration expectedDuration = aRandom.duration()
		ExternalActivityEntity expectedExternal = aRandom.externalActivityEntity()
				.start(timeService.javaNow().minus(expectedDuration))
				.end(timeService.javaNow())
				.taskId(taskId)
				.build()

		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newExternalActivity(expectedExternal.taskId, timeService.now(), expectedDuration.seconds, expectedExternal.comment)
				.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedExternal.taskId)
		comparator.assertEquals(expectedExternal, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post block activity"() {
		Duration expectedDuration = aRandom.duration()
		BlockActivityEntity expectedActivity = aRandom.blockActivityEntity()
				.start(timeService.javaNow().minus(expectedDuration))
				.end(timeService.javaNow())
				.taskId(taskId)
				.build()

		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newBlockActivity(taskId, timeService.now(), expectedDuration.seconds, expectedActivity.comment)
				.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedActivity.taskId)
		comparator.assertEquals(expectedActivity, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post execution activity"() {
		Duration expectedDuration = aRandom.duration()
		ExecutionActivityEntity expectedExecution = aRandom.executionActivityEntity()
				.start(timeService.javaNow().minus(expectedDuration))
				.end(timeService.javaNow())
				.taskId(taskId)
				.build()

		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newExecutionActivity(expectedExecution.taskId, timeService.now(), expectedDuration.seconds,
					expectedExecution.processName, expectedExecution.executionTaskType, expectedExecution.exitCode, expectedExecution.isDebug())
				.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedExecution.taskId)
		comparator.assertEquals(expectedExecution, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post modification activity"() {
		Duration expectedDuration = aRandom.duration()
		ModificationActivityEntity expectedModification = aRandom.modificationActivityEntity()
				.start(timeService.javaNow().minus(expectedDuration))
				.end(timeService.javaNow())
				.taskId(taskId)
				.build()

		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newModificationActivity(expectedModification.taskId, timeService.now(), expectedDuration.seconds,
				expectedModification.modificationCount)
				.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedModification.taskId)
		comparator.assertEquals(expectedModification, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post events"() {
		given:
		EventEntity expectedEvent = aRandom.eventEntity()
			.position(timeService.javaNow())
			.taskId(taskId)
			.build()

		NewIFMBatch batch = aRandom.batch()
				.timeSent(timeService.now())
				.newEvent(expectedEvent.taskId, timeService.now(), expectedEvent.type, expectedEvent.comment)
				.build()

		when:
		client.addIFMBatch(batch)

		then:
		List<EventEntity> entities = persistenceService.getEventList(expectedEvent.taskId)
		comparator.assertEquals(expectedEvent, entities.last())
		assert entities.last().id != null

	}

}
