package org.openmastery.publisher.resources

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.testsupport.BeanCompare
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.client.BatchClient
import org.openmastery.time.TimeService
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
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
	private EntityMapper entityMapper = new EntityMapper()

	def "SHOULD post editor activity"() {
		given:
		Duration expectedDuration = aRandom.duration()
		EditorActivityEntity expectedActivity = aRandom.editorActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		client.addEditorActivity(expectedActivity.taskId, timeService.jodaNow(), expectedDuration.seconds, expectedActivity.filePath, expectedActivity.modified)

		then:
		List<EditorActivityEntity> activityEntities = persistenceService.getEditorActivityList(expectedActivity.taskId)
		comparator.assertEquals(expectedActivity, activityEntities.last())
		assert activityEntities.last().id != null
	}

	def "SHOULD post idle activity"() {
		given:
		Duration expectedDuration = aRandom.duration()
		IdleActivityEntity expectedIdle = aRandom.idleActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		client.addIdleActivity(expectedIdle.taskId, timeService.jodaNow(), expectedDuration.seconds)

		then:
		List<IdleActivityEntity> idleEntities = persistenceService.getIdleActivityList(expectedIdle.taskId)
		comparator.assertEquals(expectedIdle, idleEntities.last())
		assert idleEntities.last().id != null
	}

	def "SHOULD post external activity"() {
		Duration expectedDuration = aRandom.duration()
		ExternalActivityEntity expectedExternal = aRandom.externalActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		client.addExternalActivity(expectedExternal.taskId, timeService.jodaNow(), expectedDuration.seconds, expectedExternal.comment)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedExternal.taskId)
		comparator.assertEquals(expectedExternal, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post block activity"() {
		Duration expectedDuration = aRandom.duration()
		BlockActivityEntity expectedActivity = aRandom.blockActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		client.addExternalActivity(expectedActivity.taskId, timeService.jodaNow(), expectedDuration.seconds, expectedActivity.comment)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedActivity.taskId)
		comparator.assertEquals(expectedActivity, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post execution activity"() {
		Duration expectedDuration = aRandom.duration()
		ExecutionActivityEntity expectedExecution = aRandom.executionActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		// TODO: move this stuff to use test client instead of putting test methods in actual client
		client.addExecutionActivity(expectedExecution.taskId, timeService.jodaNow(), expectedDuration.seconds,
								expectedExecution.processName, expectedExecution.executionTaskType, expectedExecution.exitCode, expectedExecution.isDebug())

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedExecution.taskId)
		comparator.assertEquals(expectedExecution, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post modification activity"() {
		Duration expectedDuration = aRandom.duration()
		ModificationActivityEntity expectedModification = aRandom.modificationActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		// TODO: move this stuff to use test client instead of putting test methods in actual client
		client.addModificationActivity(expectedModification.taskId, timeService.jodaNow(), expectedDuration.seconds,
				expectedModification.modificationCount)

		then:
		List<ActivityEntity> entities = persistenceService.getActivityList(expectedModification.taskId)
		comparator.assertEquals(expectedModification, entities.last())
		assert entities.last().id != null
	}

	def "SHOULD post events"() {
		given:
		EventEntity expectedEvent = aRandom.eventEntity()
			.position(timeService.now())
			.build()

		when:
		client.addBatchEvent(expectedEvent.taskId, timeService.jodaNow(), expectedEvent.type, expectedEvent.comment)

		then:
		List<EventEntity> entities = persistenceService.getEventList(expectedEvent.taskId)
		comparator.assertEquals(expectedEvent, entities.last())
		assert entities.last().id != null

	}

}
