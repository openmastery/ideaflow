package org.ideaflow.publisher.resources

import com.bancvue.rest.exception.ConflictException
import com.bancvue.rest.exception.ConflictingEntityException
import com.bancvue.rest.exception.NotFoundException
import org.openmastery.testsupport.BeanCompare
import org.ideaflow.publisher.ComponentTest
import org.ideaflow.publisher.api.task.Task
import org.ideaflow.publisher.client.TaskClient
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import static org.ideaflow.publisher.ARandom.aRandom

@ComponentTest
class TaskResourceSpec extends Specification {

	@Autowired
	private TaskClient taskClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BeanCompare taskComparator = new BeanCompare().excludeFields("id")

	def "SHOULD create task"() {
		given:
		String name = aRandom.text(10)
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
		String name = aRandom.text(10)
		Task createdTask = taskClient.createTask(name, "task description")

		when:
		Task foundTask = taskClient.findTaskWithName(name)

		then:
		taskComparator.assertEquals(createdTask, foundTask)

		when:
		taskClient.findTaskWithName(name + "x")

		then:
		thrown(NotFoundException)
	}

	def "SHOULD return http conflict (409) if creating task with same name"() {
		given:
		Task expectedConflict = Task.builder().name("task").description("task description").build()
		taskClient.createTask("task", "task description")

		when:
		taskClient.createTask("task", "other description")

		then:
		ConflictingEntityException ex = thrown()
		taskComparator.assertEquals(ex.entity, expectedConflict)
	}

}
