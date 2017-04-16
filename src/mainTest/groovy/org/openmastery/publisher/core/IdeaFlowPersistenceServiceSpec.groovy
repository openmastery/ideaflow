package org.openmastery.publisher.core

import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.ActivityEntity.ActivityEntityBuilder
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity.EditorActivityEntityBuilder
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity.ExternalActivityEntityBuilder
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity.IdleActivityEntityBuilder
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.ideaflow.IdeaFlowPartialStateEntity
import org.openmastery.time.MockTimeService
import org.springframework.dao.DataIntegrityViolationException
import spock.lang.Ignore
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@Ignore
abstract class IdeaFlowPersistenceServiceSpec extends Specification {

	private MockTimeService mockTimeService = new MockTimeService()
	private TaskEntity task

	protected abstract IdeaFlowPersistenceService getPersistenceService()

	def setup() {
		task = persistenceService.saveTask(aRandom.taskEntity().build())
	}

	private TaskEntity saveTask(TaskEntity.TaskEntityBuilder builder) {
		persistenceService.saveTask(builder.build())
	}

	private ActivityEntity saveActivity(ActivityEntityBuilder builder) {
		ActivityEntity entity = builder.taskId(task.id).build()
		persistenceService.saveActivity(entity)
	}

	private EditorActivityEntity saveEditorActivity(EditorActivityEntityBuilder builder) {
		EditorActivityEntity entity = builder.taskId(task.id).build()
		persistenceService.saveActivity(entity)
	}

	private IdleActivityEntity saveIdleActivity(IdleActivityEntityBuilder builder) {
		IdleActivityEntity entity = builder.taskId(task.id).build()
		persistenceService.saveActivity(entity)
	}

	private ExternalActivityEntity saveExternalActivity(ExternalActivityEntityBuilder builder) {
		ExternalActivityEntity entity = builder.taskId(task.id).build()
		persistenceService.saveActivity(entity)
	}

	def "saveTask should fail if task with existing name and owner_id is created"() {
		given:
		saveTask(aRandom.taskEntity().name("task").ownerId(1))

		when:
		saveTask(aRandom.taskEntity().name("task").ownerId(2))

		then:
		notThrown(DataIntegrityViolationException)

		when:
		saveTask(aRandom.taskEntity().name("task").ownerId(1))

		then:
		thrown(DataIntegrityViolationException)
	}

	def "getMostRecentActivityEnd should return the most recent activity for a task"() {
		given:
		for (int i = 0; i < 5; i++) {
			saveActivity(
					aRandom.activityEntity().end(mockTimeService.hoursInFuture(i))
			)
		}
		ActivityEntity mostRecentActivity = saveActivity(
				aRandom.activityEntity().end(mockTimeService.hoursInFuture(24))
		)
		for (int i = 0; i < 5; i++) {
			saveActivity(
					aRandom.activityEntity().end(mockTimeService.hoursInFuture(i + 5))
			)
		}

		expect:
		assert mostRecentActivity.end == persistenceService.getMostRecentActivityEnd(task.id)
	}

	def "getMostRecentActivityEnd SHOULD return null WHEN there's no activity"() {
		given:
		TaskEntity task = saveTask(aRandom.taskEntity().creationDate(mockTimeService.now()))

		expect:
		assert null == persistenceService.getMostRecentActivityEnd(task.id)
	}

	def "saveActivity should persist metadata"() {
		given:
		EditorActivityEntity activity = aRandom.editorActivityEntity().taskId(task.id).build()

		when:
		EditorActivityEntity savedActivity = persistenceService.saveActivity(activity)

		then:
		assert activity.metadata == savedActivity.metadata
		assert activity.metadata.length() > 2
	}

	def "saveAnnotation should persist metadata"() {
		given:
		FaqAnnotationEntity annotation = aRandom.faqAnnotationEntity().taskId(task.id).build()

		when:
		FaqAnnotationEntity savedAnnotation = persistenceService.saveAnnotation(annotation)

		then:
		assert annotation.metadata == savedAnnotation.metadata
		assert savedAnnotation.metadata.length() > 2
	}

	def "findAnnotationsByTask should retrieve all available annotations"() {
		given:
		FaqAnnotationEntity annotation = aRandom.faqAnnotationEntity().taskId(task.id).build()

		when:
		persistenceService.saveAnnotation(annotation)
		List<FaqAnnotationEntity> faqs = persistenceService.getFaqAnnotationList(task.id)

		then:
		assert faqs.size() == 1
		assert faqs.get(0).comment == annotation.comment
	}


	def "saveActiveState should save active and containing state"() {
		given:
		IdeaFlowPartialStateEntity activeState = aRandom.ideaFlowPartialStateEntity()
				.taskId(task.id)
				.build()
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

	def "deleteTask should delete referring entities"() {
		given:
		TaskEntity task = persistenceService.saveTask(aRandom.taskEntity().build())
		persistenceService.saveActivity(aRandom.executionActivityEntity().taskId(task.id).build())
		persistenceService.saveEvent(aRandom.eventEntity().taskId(task.id).build())
		persistenceService.saveAnnotation(aRandom.faqAnnotationEntity().taskId(task.id).build())

		when:
		persistenceService.deleteTask(task)

		then:
		assert persistenceService.getExecutionActivityList(task.id) == []
		assert persistenceService.getEventList(task.id) == []
		assert persistenceService.getFaqAnnotationList(task.id) == []
	}

}
