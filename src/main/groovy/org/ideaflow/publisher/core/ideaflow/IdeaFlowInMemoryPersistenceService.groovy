package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.core.activity.EditorActivityEntity
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.task.TaskEntity
import org.springframework.context.annotation.Primary
import org.springframework.dao.DataIntegrityViolationException

import java.time.Duration
import java.time.LocalDateTime

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private long id = 1L;
	private IdeaFlowStateEntity activeState
	private IdeaFlowStateEntity containingState
	private List<IdeaFlowStateEntity> stateList = []
	private List<IdleTimeBandEntity> idleTimeBandList = []
	private List<EventEntity> eventList = []
	private List<EditorActivityEntity> editorActivityList = []
	private List<TaskEntity> taskList = []

	@Override
	public IdeaFlowStateEntity getActiveState(long taskId) {
		activeState
	}

	@Override
	public IdeaFlowStateEntity getContainingState(long taskId) {
		containingState
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		stateList.findAll { it.taskId == taskId }
	}

	@Override
	public List<IdleTimeBandEntity> getIdleTimeBandList(long taskId) {
		idleTimeBandList.findAll { it.taskId == taskId }
	}

	@Override
	List<EditorActivityEntity> getEditorActivityList(long taskId) {
		editorActivityList.findAll { it.taskId == taskId }
	}

	@Override
	LocalDateTime getMostRecentActivityEnd(long taskId) {
		EditorActivityEntity mostRecentActivity = null
		editorActivityList.each { EditorActivityEntity activity ->
			if ((mostRecentActivity == null) || (mostRecentActivity.end.isBefore(activity.end))) {
				mostRecentActivity = activity
			}
		}
		mostRecentActivity?.end
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		eventList.findAll { it.taskId == taskId }
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState) {
		saveActiveState(activeState, null)
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState) {
		this.activeState = activeState
		this.containingState = containingState
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState) {
		stateToSave.id = id++
		stateList.add(stateToSave)
		saveActiveState(activeState)
	}

	@Override
	public IdleTimeBandEntity saveIdleActivity(IdleTimeBandEntity idleActivity) {
		idleActivity.id = id++
		idleTimeBandList.add(idleActivity)
		idleActivity
	}

	@Override
	public EventEntity saveEvent(EventEntity event) {
		event.id = id++
		eventList.add(event)
		event
	}

	@Override
	public EditorActivityEntity saveEditorActivity(EditorActivityEntity activity) {
		activity.id = id++
		editorActivityList.add(activity)
		activity
	}

	@Override
	TaskEntity saveTask(TaskEntity task) {
		if (taskList.find { it.name == task.name }) {
			throw new DataIntegrityViolationException("Duplicate task");
		}

		task.id = id++
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

}
