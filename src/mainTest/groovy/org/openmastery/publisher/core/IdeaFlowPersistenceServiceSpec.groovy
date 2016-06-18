package org.openmastery.publisher.core

import org.openmastery.publisher.core.task.RandomTaskEntityBuilder
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.time.MockTimeService
import spock.lang.Ignore
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@Ignore
abstract class IdeaFlowPersistenceServiceSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()

	protected abstract IdeaFlowPersistenceService getPersistenceService()

	private TaskEntity saveTask(TaskEntity.TaskEntityBuilder builder) {
		persistenceService.saveTask(builder.build())
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

}
