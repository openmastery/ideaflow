package org.openmastery.publisher.resources

import com.bancvue.rest.exception.ConflictingEntityException
import com.bancvue.rest.exception.NotFoundException
import org.joda.time.LocalDateTime
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
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
		LocalDateTime creationDate = timeService.jodaNow()

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
				.creationDate(timeService.jodaNow()).build()
		taskClient.createTask("task", "task description")

		when:
		taskClient.createTask("task", "other description")

		then:
		ConflictingEntityException ex = thrown()
		taskComparator.assertEquals(ex.entity, expectedConflict)
	}

	def "SHOULD return recent task list"() {
		given:
		for (int i = 0; i < 10; i++) {
			taskClient.createTask("${aRandom.text(10)}-${i}", aRandom.text(50))
			timeService.plusMinutes(10)
		}
		timeService.plusHours(1)
		Task secondMostRecent = taskClient.createTask("recent1", "description")
		timeService.plusHours(1)
		Task mostRecent = taskClient.createTask("recent2", "description")

		when:
		List<Task> taskList = taskClient.findRecentTasks(2)

		then:
		assert taskList == [mostRecent, secondMostRecent]
	}

	def "activate SHOULD create idle time on resume with start time as the most recent file activity end time"() {
		given:
		java.time.LocalDateTime fileActivityStart = timeService.now()
		java.time.LocalDateTime fileActivityEnd = fileActivityStart.plusHours(1)
		Task recentTask = taskClient.createTask("recent", "description")
		ActivityEntity fileActivity = aRandom.activityEntity()
				.taskId(recentTask.id)
				.start(fileActivityStart)
				.end(fileActivityEnd)
				.build()
		persistenceService.saveActivity(fileActivity)
		timeService.plusHours(5)

		when:
		taskClient.activate(recentTask.id)

		then:
		IdleActivityEntity idleActivityEntity = persistenceService.getIdleActivityList(recentTask.id)[0]
		assert idleActivityEntity.start == fileActivityEnd
	}

	def "activate SHOULD NOT create idle time on resume if there is no file activity associated with task"() {
		given:
		Task recentTask = taskClient.createTask("recent", "description")
		timeService.plusHours(5)

		when:
		taskClient.activate(recentTask.id)

		then:
		assert persistenceService.getIdleActivityList(recentTask.id).size() == 0
	}

}
