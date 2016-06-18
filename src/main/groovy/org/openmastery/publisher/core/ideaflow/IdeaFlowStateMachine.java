package org.openmastery.publisher.core.ideaflow;

import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.time.TimeService;

/**
 * TOOD: investigate http://projects.spring.io/spring-statemachine/
 */
public class IdeaFlowStateMachine {

	private Long taskId;
	private TimeService timeService;
	private IdeaFlowPersistenceService ideaFlowPersistenceService;

	public IdeaFlowStateMachine(Long taskId, TimeService timeService, IdeaFlowPersistenceService ideaFlowPersistenceService) {
		this.taskId = taskId;
		this.timeService = timeService;
		this.ideaFlowPersistenceService = ideaFlowPersistenceService;
	}

	private IdeaFlowStateEntity getActiveState() {
		IdeaFlowStateEntity state = ideaFlowPersistenceService.getActiveState(taskId);
		if (state == null) {
			state = createStartProgress();
		}
		return state;
	}

	private IdeaFlowStateEntity getContainingState() {
		return ideaFlowPersistenceService.getContainingState(taskId);
	}

	private IdeaFlowStateEntity createStartProgress() {
		return IdeaFlowStateEntity.builder()
				.taskId(taskId)
				.type(IdeaFlowStateType.PROGRESS)
				.start(timeService.now())
				.build();
	}

	private IdeaFlowStateEntity createEndProgress(IdeaFlowStateEntity startState) {
		return createEndState(startState, null);
	}

	private IdeaFlowStateEntity createStartState(IdeaFlowStateType type, String startingComment) {
		return IdeaFlowStateEntity.builder()
				.taskId(taskId)
				.type(type)
				.startingComment(startingComment)
				.start(timeService.now())
				.build();
	}

	private IdeaFlowStateEntity createEndState(IdeaFlowStateEntity startState, String endingComment) {
		return IdeaFlowStateEntity.from(startState)
				.taskId(startState.getTaskId())
				.end(timeService.now())
				.endingComment(endingComment)
				.build();
	}

	public void startTask() {
		ideaFlowPersistenceService.saveActiveState(createStartProgress());
	}

	public void startConflict(String question) {
		IdeaFlowStateEntity oldActiveState = getActiveState();
		IdeaFlowStateEntity newActiveState = createStartState(IdeaFlowStateType.CONFLICT, question);

		if (oldActiveState.isOfType(IdeaFlowStateType.PROGRESS)) {
			IdeaFlowStateEntity stateToSave = createEndProgress(oldActiveState);
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else if (oldActiveState.isOfType(IdeaFlowStateType.LEARNING, IdeaFlowStateType.REWORK)) {
			newActiveState.setNested(true);
			ideaFlowPersistenceService.saveActiveState(newActiveState, oldActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	public void endConflict(String resolution) {
		IdeaFlowStateEntity oldActiveState = getActiveState();

		if (oldActiveState.isOfType(IdeaFlowStateType.CONFLICT)) {
			IdeaFlowStateEntity stateToSave = createEndState(oldActiveState, resolution);
			IdeaFlowStateEntity containingState = getContainingState();
			IdeaFlowStateEntity newActiveState = containingState != null ? containingState : createStartProgress();
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	public void startLearning(String comment) {
		startLearningOrRework(IdeaFlowStateType.LEARNING, comment);
	}

	public void startRework(String comment) {
		startLearningOrRework(IdeaFlowStateType.REWORK, comment);
	}

	public void endLearning(String resolution) {
		endLearningOrRework(IdeaFlowStateType.LEARNING, resolution);
	}

	public void endRework(String resolution) {
		endLearningOrRework(IdeaFlowStateType.REWORK, resolution);
	}

	private void startLearningOrRework(IdeaFlowStateType type, String comment) {
		IdeaFlowStateEntity oldActiveState = getActiveState();
		IdeaFlowStateEntity newActiveState = createStartState(type, comment);

		if (getContainingState() != null) {
			// TODO: log warning
			throw new InvalidTransitionException();
		}

		IdeaFlowStateType otherNonConflictType = type == IdeaFlowStateType.LEARNING ? IdeaFlowStateType.REWORK : IdeaFlowStateType.LEARNING;
		if (oldActiveState.isOfType(IdeaFlowStateType.PROGRESS)) {
			IdeaFlowStateEntity stateToSave = createEndProgress(oldActiveState);
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else if (oldActiveState.isOfType(IdeaFlowStateType.CONFLICT, otherNonConflictType)) {
			IdeaFlowStateEntity previousState = createEndState(oldActiveState, comment);
			newActiveState.setLinkedToPrevious(true);
			ideaFlowPersistenceService.saveTransition(previousState, newActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	private void endLearningOrRework(IdeaFlowStateType type, String resolution) {
		IdeaFlowStateEntity oldActiveState = getActiveState();

		if (oldActiveState.isOfType(type)) {
			IdeaFlowStateEntity stateToSave = createEndState(oldActiveState, resolution);
			ideaFlowPersistenceService.saveTransition(stateToSave, createStartProgress());
		} else if (oldActiveState.isOfType(IdeaFlowStateType.CONFLICT)) {
			IdeaFlowStateEntity containingState = getContainingState();
			if (containingState != null) {
				IdeaFlowStateEntity stateToSave = createEndState(containingState, resolution);
				oldActiveState.setNested(false);
				oldActiveState.setLinkedToPrevious(true);
				ideaFlowPersistenceService.saveTransition(stateToSave, oldActiveState);
			} else {
				// TODO: log warning
				throw new InvalidTransitionException();
			}
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}


	public static class InvalidTransitionException extends RuntimeException {
		public InvalidTransitionException() {
		}
	}

}