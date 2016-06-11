package org.openmastery.publisher.resources

import com.bancvue.rest.exception.ConflictingEntityException
import com.bancvue.rest.exception.NotFoundException
import org.openmastery.testsupport.BeanCompare
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.LocalDateTime

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class TaskResourceSpec extends Specification {

	@Autowired
	private TaskClient taskClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	@Autowired
	private TimeService timeService

	private BeanCompare taskComparator = new BeanCompare().excludeFields("id")

	def "SHOULD create task"() {
		given:
		String name = aRandom.text(10)
		String description = "task description"
		LocalDateTime creationDate = timeService.now()

		when:
		Task createdTask = taskClient.createTask(name, description)

		then:
		Task expectedTask = Task.builder()
				.name(name)
				.description(description)
				.creationDate(creationDate)
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
		Task expectedConflict = Task.builder()
				.name("task")
				.description("task description")
				.creationDate(timeService.now()).build()
		taskClient.createTask("task", "task description")

		when:
		taskClient.createTask("task", "other description")

		then:
		ConflictingEntityException ex = thrown()
		taskComparator.assertEquals(ex.entity, expectedConflict)
	}

}
