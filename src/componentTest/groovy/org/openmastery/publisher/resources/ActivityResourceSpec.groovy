package org.openmastery.publisher.resources

import org.openmastery.publisher.core.activity.IdleTimeBandEntity
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
	private BeanCompare comparator = new BeanCompare().excludeFields("id")

	def "SHOULD post editor activity"() {
		given:
		Duration expectedDuration = aRandom.duration()
		EditorActivityEntity expectedActivity = aRandom.editorActivityEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		client.addEditorActivity(expectedActivity.taskId, expectedActivity.filePath, expectedActivity.modified, expectedDuration.seconds)

		then:
		List<EditorActivityEntity> activityEntities = persistenceService.getEditorActivityList(expectedActivity.taskId)
		comparator.assertEquals(expectedActivity, activityEntities.last())
		assert activityEntities.last().id != null
	}

	def "SHOULD post idle activity"() {
		given:
		Duration expectedDuration = aRandom.duration()
		IdleTimeBandEntity expectedIdle = aRandom.idleTimeBandEntity()
				.start(timeService.now().minus(expectedDuration))
				.end(timeService.now())
				.build()

		when:
		client.addIdleActivity(expectedIdle.taskId, expectedIdle.comment, expectedIdle.auto, expectedDuration.seconds)

		then:
		List<IdleTimeBandEntity> idleEntities = persistenceService.getIdleTimeBandList(expectedIdle.taskId)
		comparator.assertEquals(expectedIdle, idleEntities.last())
		assert idleEntities.last().id != null
	}

}
