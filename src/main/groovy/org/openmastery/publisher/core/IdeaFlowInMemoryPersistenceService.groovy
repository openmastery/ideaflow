package org.openmastery.publisher.core

import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.springframework.dao.DataIntegrityViolationException

import java.time.LocalDateTime

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private long ideaFlowStateId = 1L
	private long eventId = 1L
	private long taskId = 1L
	private long activityId = 1L
	private long idleActivityId = 1L
	private IdeaFlowPartialStateEntity activeState
	private IdeaFlowPartialStateEntity containingState
	private List<IdeaFlowStateEntity> stateList = []
	private List<IdleActivityEntity> idleActivityList = []
	private List<EventEntity> eventList = []
	private List<EditorActivityEntity> editorActivityList = []
	private List<TaskEntity> taskList = []

	@Override
	public IdeaFlowPartialStateEntity getActiveState(long taskId) {
		activeState
	}

	@Override
	public IdeaFlowPartialStateEntity getContainingState(long taskId) {
		containingState
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		stateList.findAll { it.taskId == taskId }
	}

	@Override
	public List<IdleActivityEntity> getIdleActivityList(long taskId) {
		idleActivityList.findAll { it.taskId == taskId }
	}

	@Override
	List<EditorActivityEntity> getEditorActivityList(long taskId) {
		editorActivityList.findAll { it.taskId == taskId }
	}

	@Override
	LocalDateTime getMostRecentActivityEnd(long taskId) {
		LocalDateTime mostRecentActivity = null
		editorActivityList.each { EditorActivityEntity activity ->
			if ((mostRecentActivity == null) || (mostRecentActivity.isBefore(activity.end))) {
				mostRecentActivity = activity.end
			}
		}
		if (mostRecentActivity == null) {
			TaskEntity taskEntity = findTaskWithId(taskId);
			mostRecentActivity = taskEntity.creationDate
		}

		mostRecentActivity
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		eventList.findAll { it.taskId == taskId }
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState) {
		saveActiveState(activeState, null)
	}

	@Override
	public void saveActiveState(IdeaFlowPartialStateEntity activeState, IdeaFlowPartialStateEntity containingState) {
		this.activeState = activeState
		this.containingState = containingState
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowPartialStateEntity activeState) {
		stateToSave.id = ideaFlowStateId++
		stateList.add(stateToSave)
		saveActiveState(activeState)
	}

	@Override
	public IdleActivityEntity saveIdleActivity(IdleActivityEntity idleActivity) {
		idleActivity.id = idleActivityId++
		idleActivityList.add(idleActivity)
		idleActivity
	}

	@Override
	public EventEntity saveEvent(EventEntity event) {
		event.id = eventId++
		eventList.add(event)
		event
	}

	@Override
	public EditorActivityEntity saveEditorActivity(EditorActivityEntity activity) {
		activity.id = activityId++
		editorActivityList.add(activity)
		activity
	}

	@Override
	TaskEntity saveTask(TaskEntity task) {
		if (taskList.find { it.name == task.name }) {
			throw new DataIntegrityViolationException("Duplicate task");
		}

		task.id = taskId++
		taskList.add(task)
		task
	}

	@Override
	TaskEntity findTaskWithId(long taskId) {
		taskList.find {
			it.id == taskId
		}
	}

	@Override
	TaskEntity findTaskWithName(String taskName) {
		taskList.find {
			it.name == taskName
		}
	}

	@Override
	List<TaskEntity> findRecentTasks(int limit) {
		if (limit < 1) {
			return []
		} else if (limit >= taskList.size()) {
			return taskList.asImmutable()
		} else {
			List<TaskEntity> sortedList = taskList.toSorted { a, b -> b.creationDate <=> a.creationDate }
			return sortedList.subList(0, limit)
		}
	}

}
