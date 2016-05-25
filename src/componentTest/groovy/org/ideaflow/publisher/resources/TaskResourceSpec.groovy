package org.ideaflow.publisher.resources

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.testsupport.BeanCompare
import org.ideaflow.publisher.ComponentTest
import org.ideaflow.publisher.api.task.Task
import org.ideaflow.publisher.client.TaskClient
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class TaskResourceSpec extends Specification {

	@Autowired
	private TaskClient taskClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BeanCompare taskComparator = new BeanCompare().excludeFields("id")

	def "SHOULD create task"() {
		given:
		String name = "task123"
		String description = "task description"

		when:
		Task createdTask = taskClient.createTask(name, description)

		then:
		Task expectedTask = Task.builder()
				.name(name)
				.description(description)
				.build()
		taskComparator.assertEquals(expectedTask, createdTask)
		assert createdTask.id != null
	}

	def "SHOULD find task with name"() {
		given:
		Task createdTask = taskClient.createTask("task123", "task description")

		when:
		Task foundTask = taskClient.findTaskWithName("task123")

		then:
		taskComparator.assertEquals(createdTask, foundTask)

		when:
		taskClient.findTaskWithName("task124")

		then:
		thrown(NotFoundException)
	}

}
