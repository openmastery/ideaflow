package org.ideaflow.publisher.resources

import org.openmastery.testsupport.BeanCompare
import org.ideaflow.publisher.ComponentTest
import org.ideaflow.publisher.client.ActivityClient
import org.openmastery.time.TimeService
import org.ideaflow.publisher.core.activity.EditorActivityEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.Duration

@ComponentTest
class ActivityResourceSpec extends Specification {

	@Autowired
	private ActivityClient client
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	@Autowired
	private TimeService timeService
	private long taskId = 123

	def "SHOULD post editor activity"() {
		given:
		BeanCompare comparator = new BeanCompare().excludeFields("id")
		String filePath = "/some/file/path"
		boolean isModified = true
		Duration duration = Duration.ofMinutes(45)

		when:
		client.addEditorActivity(taskId, filePath, isModified, duration)

		then:
		EditorActivityEntity expectedEditorActivity = EditorActivityEntity.builder()
				.taskId(taskId)
				.filePath(filePath)
				.isModified(isModified)
				.start(timeService.now().minus(duration))
				.end(timeService.now())
				.build()
		EditorActivityEntity actualEditorActivity = persistenceService.getEditorActivityList(taskId).last()
		comparator.assertEquals(expectedEditorActivity, actualEditorActivity)
		assert actualEditorActivity.id != null
	}

}
