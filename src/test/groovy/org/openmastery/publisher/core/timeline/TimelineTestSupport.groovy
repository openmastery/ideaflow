package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.time.MockTimeService
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.IdeaFlowInMemoryPersistenceService
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateMachine

import java.time.LocalDateTime

import static IdeaFlowStateType.CONFLICT
import static IdeaFlowStateType.LEARNING
import static IdeaFlowStateType.REWORK

class TimelineTestSupport {

	private IdeaFlowStateMachine stateMachine
	private MockTimeService timeService = new MockTimeService()
	private IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()
	private long taskId

	long getTaskId() {
		return taskId
	}

	IdeaFlowInMemoryPersistenceService getPersistenceService() {
		return persistenceService
	}

	LocalDateTime now() {
		timeService.now()
	}

	List<IdeaFlowStateEntity> getStateListWithActiveCompleted() {
		List<IdeaFlowStateEntity> stateList = new ArrayList(persistenceService.getStateList(taskId))
		completeAndAddStateIfNotNull(stateList, persistenceService.activeState)
		completeAndAddStateIfNotNull(stateList, persistenceService.containingState)
		stateList
	}

	List<IdleActivityEntity> getIdleActivityList() {
		persistenceService.getIdleActivityList(taskId)
	}

	List<EventEntity> getEventList() {
		persistenceService.getEventList(taskId)
	}

	private void completeAndAddStateIfNotNull(List<IdeaFlowStateEntity> stateList, IdeaFlowPartialStateEntity state) {
		if (state) {
			stateList << IdeaFlowStateEntity.from(state)
					.taskId(taskId)
					.end(timeService.now())
					.endingComment("")
					.build();
		}
	}

	void startTaskAndAdvanceHours(int hours) {
		startTask("task", "task description")
		timeService.plusHours(hours)
	}

	void startTask(String name, String description) {
		TaskEntity task = TaskEntity.builder()
				.name(name)
				.description(description)
				.build();

		task = persistenceService.saveTask(task)
		taskId = task.id
		stateMachine = new IdeaFlowStateMachine(taskId, timeService, persistenceService)
		stateMachine.startTask()
	}

	void startSubtaskAndAdvanceHours(int hours) {
		startSubtaskAndAdvanceHours(null, hours)
	}

	void startSubtaskAndAdvanceHours(String comment, int hours) {
		EventEntity event = EventEntity.builder()
				.taskId(taskId)
				.type(EventType.SUBTASK)
				.position(timeService.now())
				.comment(comment)
				.build()
		persistenceService.saveEvent(event)
		timeService.plusHours(hours)
	}

	void advanceHours(int hours) {
		timeService.plusHours(hours)
	}

	void idle(int hours) {
		LocalDateTime start = timeService.now()
		timeService.plusHours(hours)
		IdleActivityEntity idleActivity = IdleActivityEntity.builder()
				.taskId(taskId)
				.start(start)
				.end(timeService.now()).build()
		persistenceService.saveIdleActivity(idleActivity)
	}

	void editor() {
		EditorActivityEntity editorActivity = EditorActivityEntity.builder()
				.taskId(taskId)
				.start(timeService.now())
				.end(timeService.now())
				.filePath("/some/path")
				.build()
		persistenceService.saveEditorActivity(editorActivity)
	}

	void note() {
		note("")
	}

	void note(String comment) {
		EventEntity event = EventEntity.builder()
				.taskId(taskId)
				.comment(comment)
				.position(now())
				.type(EventType.NOTE)
				.build()
		persistenceService.saveEvent(event)
	}

	void startBand(IdeaFlowStateType type, String comment) {
		if (type == LEARNING) {
			stateMachine.startLearning(comment)
		} else if (type == REWORK) {
			stateMachine.startRework(comment)
		} else if (type == CONFLICT) {
			stateMachine.startConflict(comment)
		} else {
			throw new RuntimeException("Unknown type: ${type}")
		}
	}

	void startBand(IdeaFlowStateType type) {
		startBand(type, "")
	}

	void startBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
		startBand(type)
		timeService.plusHours(hours)
	}

	void endBand(IdeaFlowStateType type) {
		endBand(type, "")
	}

	void endBand(IdeaFlowStateType type, String comment) {
		if (type == LEARNING) {
			stateMachine.endLearning(comment)
		} else if (type == REWORK) {
			stateMachine.endRework(comment)
		} else if (type == CONFLICT) {
			stateMachine.endConflict(comment)
		} else {
			throw new RuntimeException("Unknown type: ${type}")
		}
	}


	void endBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
		endBand(type)
		timeService.plusHours(hours)
	}

}
