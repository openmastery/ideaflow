package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.EventType
import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowInMemoryPersistenceService
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachine

import java.time.LocalDateTime

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK

class TimelineTestSupport {

	private IdeaFlowStateMachine stateMachine
	private MockTimeService timeService = new MockTimeService()
	private IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()

	TimelineTestSupport() {
		this.stateMachine = new IdeaFlowStateMachine()
		stateMachine.timeService = timeService
		stateMachine.ideaFlowPersistenceService = persistenceService
	}

	LocalDateTime now() {
		timeService.now()
	}

	List<IdeaFlowStateEntity> getStateListWithActiveCompleted() {
		List<IdeaFlowStateEntity> stateList = new ArrayList(persistenceService.getStateList())
		completeAndAddStateIfNotNull(stateList, persistenceService.activeState)
		completeAndAddStateIfNotNull(stateList, persistenceService.containingState)
		stateList
	}

	List<IdleTimeBandEntity> getIdleActivityList() {
		persistenceService.getIdleTimeBandList()
	}

	List<EventEntity> getEventList() {
		persistenceService.getEventList()
	}

	private void completeAndAddStateIfNotNull(List<IdeaFlowStateEntity> stateList, IdeaFlowStateEntity state) {
		if (state) {
			stateList << IdeaFlowStateEntity.from(state)
					.end(timeService.now())
					.endingComment("")
					.build();
		}
	}

	void startTaskAndAdvanceHours(int hours) {
		stateMachine.startTask()
		timeService.plusHours(hours)
	}

	void startTask() {
		stateMachine.startTask()
	}

	void startSubtaskAndAdvanceHours(int hours) {
		EventEntity event = EventEntity.builder()
				.eventType(EventType.SUBTASK)
				.position(timeService.now())
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
		IdleTimeBandEntity idleActivity = IdleTimeBandEntity.builder()
				.start(start)
				.end(timeService.now()).build()
		persistenceService.saveIdleActivity(idleActivity)
	}

	void note() {
		EventEntity event = EventEntity.builder()
				.comment("")
				.position(now())
				.eventType(EventType.NOTE)
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
			stateMachine.stopLearning(comment)
		} else if (type == REWORK) {
			stateMachine.stopRework(comment)
		} else if (type == CONFLICT) {
			stateMachine.stopConflict(comment)
		} else {
			throw new RuntimeException("Unknown type: ${type}")
		}
	}


	void endBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
		endBand(type)
		timeService.plusHours(hours)
	}

}
