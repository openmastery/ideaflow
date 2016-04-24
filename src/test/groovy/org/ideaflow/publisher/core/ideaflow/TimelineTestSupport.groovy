package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleActivityEntity

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

	List<IdleActivityEntity> getIdleActivityList() {
		persistenceService.getIdleActivityList()
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

	void advanceHours(int hours) {
		timeService.plusHours(hours)
	}

	void idle(int hours) {
		LocalDateTime start = timeService.now()
		timeService.plusHours(hours)
		IdleActivityEntity idleActivity = IdleActivityEntity.builder()
				.start(start)
				.end(timeService.now()).build()
		persistenceService.saveIdleActivity(idleActivity)
	}

	void startBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
		if (type == LEARNING) {
			stateMachine.startLearning("")
		} else if (type == REWORK) {
			stateMachine.startRework("")
		} else if (type == CONFLICT) {
			stateMachine.startConflict("")
		} else {
			throw new RuntimeException("Unknown type: ${type}")
		}
		timeService.plusHours(hours)
	}

	void endBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
		if (type == LEARNING) {
			stateMachine.stopLearning("")
		} else if (type == REWORK) {
			stateMachine.stopRework("")
		} else if (type == CONFLICT) {
			stateMachine.stopConflict("")
		} else {
			throw new RuntimeException("Unknown type: ${type}")
		}
		timeService.plusHours(hours)
	}

}
