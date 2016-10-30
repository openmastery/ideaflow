package org.openmastery.publisher.resources

import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.testsupport.BeanCompare
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.client.ActivityClient
import org.openmastery.time.TimeService
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.Duration

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class ActivityResourceSpec extends Specification {

	@Autowired
	private ActivityClient client
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	@Autowired
	private TimeService timeService
	private BeanCompare comparator = new BeanCompare().excludeFields("id", "ownerId", "metadata", "metadataContainer")

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

}
