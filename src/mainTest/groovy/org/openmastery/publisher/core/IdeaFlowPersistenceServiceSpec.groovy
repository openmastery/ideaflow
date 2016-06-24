package org.openmastery.publisher.core

import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.ActivityEntity.ActivityEntityBuilder
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity.EditorActivityEntityBuilder
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity.ExternalActivityEntityBuilder
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity.IdleActivityEntityBuilder
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.time.MockTimeService
import org.springframework.dao.DataIntegrityViolationException
import spock.lang.Ignore
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@Ignore
abstract class IdeaFlowPersistenceServiceSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private long taskId = aRandom.intBetween(1, 100000)

	protected abstract IdeaFlowPersistenceService getPersistenceService()

	private TaskEntity saveTask(TaskEntity.TaskEntityBuilder builder) {
		persistenceService.saveTask(builder.build())
	}

	private ActivityEntity saveActivity(ActivityEntityBuilder builder) {
		ActivityEntity entity = builder.taskId(taskId).build()
		persistenceService.saveActivity(entity)
	}

	private EditorActivityEntity saveEditorActivity(EditorActivityEntityBuilder builder) {
		EditorActivityEntity entity = builder.taskId(taskId).build()
		persistenceService.saveActivity(entity)
	}

	private IdleActivityEntity saveIdleActivity(IdleActivityEntityBuilder builder) {
		IdleActivityEntity entity = builder.taskId(taskId).build()
		persistenceService.saveActivity(entity)
	}

	private ExternalActivityEntity saveExternalActivity(ExternalActivityEntityBuilder builder) {
		ExternalActivityEntity entity = builder.taskId(taskId).build()
		persistenceService.saveActivity(entity)
	}

	def "findRecentTasks should return empty list if no tasks"() {
		expect:
		assert persistenceService.findRecentTasks(1).isEmpty()
	}

	def "findRecentTasks should return entire list if number of tasks less than limit"() {
		given:
		TaskEntity task = saveTask(aRandom.taskEntity())

		when:
		List<TaskEntity> taskList = persistenceService.findRecentTasks(5)

		then:
		assert taskList == [task]
	}

	def "findRecentTasks should return the most recent tasks added"() {
		given:
		TaskEntity mostRecent = saveTask(aRandom.taskEntity().creationDate(mockTimeService.inFuture(24)))
		for (int i = 0; i < 5; i++) {
			saveTask(aRandom.taskEntity().creationDate(mockTimeService.inFuture(i)))
		}
		TaskEntity secondMostRecent = saveTask(aRandom.taskEntity().creationDate(mockTimeService.inFuture(23)))

		when:
		List<TaskEntity> taskList = persistenceService.findRecentTasks(2)

		then:
		assert taskList == [mostRecent, secondMostRecent]
	}

	def "saveTask should fail if task with existing name is created"() {
		given:
		saveTask(aRandom.taskEntity().name("task"))

		when:
		saveTask(aRandom.taskEntity().name("task"))

		then:
		thrown(DataIntegrityViolationException)
	}

	def "getMostRecentActivityEnd should return the most recent activity for a task"() {
		given:
		for (int i = 0; i < 5; i++) {
			saveActivity(
					aRandom.activityEntity().end(mockTimeService.inFuture(i))
			)
		}
		ActivityEntity mostRecentActivity = saveActivity(
				aRandom.activityEntity().end(mockTimeService.inFuture(24))
		)
		for (int i = 0; i < 5; i++) {
			saveActivity(
					aRandom.activityEntity().end(mockTimeService.inFuture(i + 5))
			)
		}

		expect:
		assert mostRecentActivity.end == persistenceService.getMostRecentActivityEnd(taskId)
	}

	def "getMostRecentActivityEnd SHOULD return task start WHEN there's no activity"() {
		given:
		TaskEntity task = saveTask(aRandom.taskEntity().creationDate(mockTimeService.now()))

		expect:
		assert task.creationDate == persistenceService.getMostRecentActivityEnd(task.id)
	}

	def "saveActivity should persist metadata"() {
		given:
		EditorActivityEntity activity = aRandom.editorActivityEntity().build()

		when:
		EditorActivityEntity savedActivity = persistenceService.saveActivity(activity)

		then:
		assert activity.metadata == savedActivity.metadata
		assert activity.metadata.length() > 2
	}

	def "saveActiveState should save active and containing state"() {
		given:
		IdeaFlowPartialStateEntity activeState = aRandom.ideaFlowPartialStateEntity().build()
		IdeaFlowPartialStateEntity containingState = aRandom.ideaFlowPartialStateEntity()
				.taskId(activeState.taskId)
				.build()

		when:
		persistenceService.saveActiveState(activeState, containingState)

		then:
		assert activeState == persistenceService.getActiveState(activeState.taskId)
		assert containingState == persistenceService.getContainingState(containingState.taskId)

		when:
		persistenceService.saveActiveState(activeState, null)

		then:
		assert persistenceService.getContainingState(containingState.taskId) == null
	}

}
