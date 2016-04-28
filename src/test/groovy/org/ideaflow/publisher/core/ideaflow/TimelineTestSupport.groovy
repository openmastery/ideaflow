package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowBand
import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleTimeBand
import org.ideaflow.publisher.core.event.EventEntity

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

	List<IdleTimeBand> getIdleActivityList() {
		persistenceService.getIdleActivityList()
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

	void startSubtask(String comment) {
		EventEntity event = EventEntity.builder()
				.eventType(EventEntity.Type.SUBTASK)
				.position(timeService.now())
				.message(comment)
				.build()
		persistenceService.saveEvent(event)
	}

	void startSubtaskAndAdvanceHours(int hours) {
		EventEntity event = EventEntity.builder()
				.eventType(EventEntity.Type.SUBTASK)
				.position(timeService.now())
				.build()
		persistenceService.saveEvent(event)
		timeService.plusHours(hours)
	}

	void advanceHours(int hours) {
		timeService.plusHours(hours)
	}

	void advanceTime(int hours, int minutes, int seconds) {
		timeService.plusHours(hours)
		timeService.plusMinutes(minutes)
		timeService.plusSeconds(seconds)
	}

	void idle(int hours) {
		LocalDateTime start = timeService.now()
		timeService.plusHours(hours)
		IdleTimeBand idleActivity = IdleTimeBand.builder()
				.start(start)
				.end(timeService.now()).build()
		persistenceService.saveIdleActivity(idleActivity)
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
