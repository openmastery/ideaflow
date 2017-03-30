package org.openmastery.publisher.resources

import com.bancvue.rest.exception.ConflictException
import com.bancvue.rest.exception.NotFoundException
import org.joda.time.LocalDateTime
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.PagedResult
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.task.TaskPatch
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.testsupport.BeanCompare
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class TaskResourceSpec extends Specification {

	@Autowired
	private TaskClient taskClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	@Autowired
	private MockTimeService timeService

	private BeanCompare taskComparator = new BeanCompare().excludeFields("id", "ownerId")

	def "SHOULD create task"() {
		given:
		String name = aRandom.text(10)
		String description = "task description"
		String project = "project"
		LocalDateTime creationDate = timeService.now()

		when:
		Task createdTask = taskClient.createTask(name, description, project)

		then:
		Task expectedTask = Task.builder()
				.name(name)
				.description(description)
				.project(project)
				.creationDate(creationDate)
				.modifyDate(creationDate)
				.build()
		taskComparator.assertEquals(expectedTask, createdTask)
		assert createdTask.id != null
	}

	def "SHOULD find task with name"() {
		given:
		String name = aRandom.text(10)
		Task createdTask = taskClient.createTask(name, "task description", "project")

		when:
		Task foundTask = taskClient.findTaskWithName(name)

		then:
		taskComparator.assertEquals(createdTask, foundTask)

		when:
		taskClient.findTaskWithName(name + "x")

		then:
		thrown(NotFoundException)
	}

	def "SHOULD update existing task"() {
		given:
		String name = aRandom.text(10)
		String description = "task description"
		String project = "project"
		LocalDateTime creationDate = timeService.now()

		when:
		Task createdTask = taskClient.createTask(name, description, project)
		TaskPatch patch = new TaskPatch(description: "new description")

		Task updatedTask = taskClient.updateTask(createdTask.id, patch)

		then:
		Task expectedTask = Task.builder()
				.name(name)
				.description("new description")
				.project(project)
				.creationDate(creationDate)
				.modifyDate(creationDate)
				.build()
		taskComparator.assertEquals(expectedTask, updatedTask)
		assert createdTask.id != null
	}


	def "SHOULD return http conflict (409) if creating task with same name"() {
		given:
		Task expectedConflict = Task.builder()
				.name("task")
				.description("task description")
				.project("project")
				.creationDate(timeService.now())
				.modifyDate(timeService.now()).build()
		taskClient.createTask("task", "task description", "project")

		when:
		taskClient.createTask("task", "other description", "project")

		then:
		ConflictException ex = thrown()
		taskComparator.assertEquals(ex.entity, expectedConflict)
	}

	def "SHOULD return most recent tasks"() {
		given:
		for (int i = 0; i < 10; i++) {
			taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50), aRandom.text(50))
			timeService.plusMinutes(10)
		}
		timeService.plusHours(1)
		Task secondMostRecent = taskClient.createTask("recent1", "description", "project")
		timeService.plusHours(1)
		Task mostRecent = taskClient.createTask("recent2", "description", "project")

		when:
		PagedResult<Task> taskPage = taskClient.findRecentTasks(0, 2)

		then:
		assert taskPage.contents == [mostRecent, secondMostRecent]
		assert taskPage.totalPages == 6
		assert taskPage.totalElements == 12
		assert taskPage.pageNumber == 0
	}

	def "findRecentTasks SHOULD return page 2 of tasks"() {
		given:
		List<Task> expectedTasks = []
		for (int i = 0; i < 10; i++) {
			Task task = taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50), aRandom.text(50))
			expectedTasks.add(task)
			timeService.plusMinutes(10)
		}
		expectedTasks = expectedTasks.reverse()

		when:
		PagedResult<Task> taskPage = taskClient.findRecentTasks(1, 5)

		then:
		assert taskPage.contents == expectedTasks.subList(5, 10)
		assert taskPage.totalPages == 2
		assert taskPage.totalElements == 10
		assert taskPage.pageNumber == 1
		assert taskPage.hasPrevious == true
		assert taskPage.hasNext == false
	}

	def "findRecentTasks SHOULD filter by project"() {
		given:
		for (int i = 0; i < 10; i++) {
			taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50), aRandom.text(50))
			timeService.plusMinutes(10)
		}
		timeService.plusHours(1)
		Task secondMostRecent = taskClient.createTask("recent1", "description", "project")
		timeService.plusHours(1)
		Task mostRecent = taskClient.createTask("recent2", "description", "project")

		when:
		PagedResult<Task> taskPage = taskClient.findRecentTasksForProject("project", 0, 5)

		then:
		assert taskPage.contents == [mostRecent, secondMostRecent]
		assert taskPage.totalPages == 1
		assert taskPage.totalElements == 2
		assert taskPage.pageNumber == 0
		assert taskPage.hasNext == false
		assert taskPage.hasPrevious == false
	}

	def "findRecentTasksMatchingTags SHOULD filter tasks according to #tags in FAQ or event comments"() {
		given:
		for (int i = 0; i < 10; i++) {
			taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50), aRandom.text(50))
			timeService.plusMinutes(10)
		}
		timeService.plusHours(1)
		Task mostRecent = taskClient.createTask("recent2", "description", "project")

		EventEntity event = aRandom.eventEntity().taskId(mostRecent.id).comment("Comment with #tag").build()
		persistenceService.saveEvent(event)

		when:
		PagedResult<Task> taskPage = taskClient.findRecentTasksMatchingTags(["#tag"], 0, 2)

		then:
		assert taskPage.contents == [mostRecent]
		assert taskPage.totalPages == 1
		assert taskPage.totalElements == 1
		assert taskPage.pageNumber == 0
		assert taskPage.hasNext == false
		assert taskPage.hasPrevious == false

	}

	def "findRecentTasksMatchingTags SHOULD return the second page of results"() {
		given:
		for (int i = 0; i < 10; i++) {
			Task task = taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50), aRandom.text(50))
			timeService.plusMinutes(10)

			EventEntity event = aRandom.eventEntity().taskId(task.id).comment("Comment with #tag").build()
			persistenceService.saveEvent(event)
		}

		when:
		PagedResult<Task> taskPage = taskClient.findRecentTasksMatchingTags(["#tag"], 1, 5)

		then:
		assert taskPage.contents.size() == 5
		assert taskPage.totalPages == 2
		assert taskPage.totalElements == 10
		assert taskPage.pageNumber == 1
		assert taskPage.hasNext == false
		assert taskPage.hasPrevious == true

	}

	def "findRecentTasksMatchingTags SHOULD filter by project"() {
		given:
		for (int i = 0; i < 10; i++) {
			Task task = taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50), aRandom.text(50))
			timeService.plusMinutes(10)

			EventEntity event = aRandom.eventEntity().taskId(task.id).comment("Comment with #tag").build()
			persistenceService.saveEvent(event)
		}

		timeService.plusHours(1)
		Task mostRecent = taskClient.createTask("recent2", "description", "project")
		EventEntity event = aRandom.eventEntity().taskId(mostRecent.id).comment("Comment with #tag").build()
		persistenceService.saveEvent(event)

		when:
		PagedResult<Task> taskPage = taskClient.findRecentTasksMatchingTagsAndProject(["#tag"], "project", 0, 5)

		then:
		assert taskPage.contents.size() == 1
		assert taskPage.totalPages == 1
		assert taskPage.totalElements == 1
		assert taskPage.pageNumber == 0
		assert taskPage.hasNext == false
		assert taskPage.hasPrevious == false

	}

}
