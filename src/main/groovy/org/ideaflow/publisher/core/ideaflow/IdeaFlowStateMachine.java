package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType;
import org.ideaflow.publisher.core.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.CONFLICT;
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.LEARNING;
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS;
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.REWORK;

/**
 * TOOD: investigate http://projects.spring.io/spring-statemachine/
 */
//@Component
public class IdeaFlowStateMachine {

	@Autowired
	private TimeService timeService;
	@Autowired
	private IdeaFlowPersistenceService ideaFlowPersistenceService;

	private IdeaFlowStateEntity getActiveState() {
		IdeaFlowStateEntity state = ideaFlowPersistenceService.getActiveState();
		if (state == null) {
			state = createStartProgress();
		}
		return state;
	}

	private IdeaFlowStateEntity getContainingState() {
		return ideaFlowPersistenceService.getContainingState();
	}

	private IdeaFlowStateEntity createStartProgress() {
		return IdeaFlowStateEntity.builder()
				.type(PROGRESS)
				.start(timeService.now())
				.build();
	}

	private IdeaFlowStateEntity createEndProgress(IdeaFlowStateEntity startState) {
		return createEndState(startState, null);
	}

	private IdeaFlowStateEntity createStartState(IdeaFlowStateType type, String startingComment) {
		return IdeaFlowStateEntity.builder()
				.type(type)
				.startingComment(startingComment)
				.start(timeService.now())
				.build();
	}

	private IdeaFlowStateEntity createEndState(IdeaFlowStateEntity startState, String endingComment) {
		return IdeaFlowStateEntity.from(startState)
				.end(timeService.now())
				.endingComment(endingComment)
				.build();
	}

	public void startTask() {
		ideaFlowPersistenceService.saveActiveState(createStartProgress());
	}

	public void startConflict(String question) {
		IdeaFlowStateEntity oldActiveState = getActiveState();
		IdeaFlowStateEntity newActiveState = createStartState(CONFLICT, question);

		if (oldActiveState.isOfType(PROGRESS)) {
			IdeaFlowStateEntity stateToSave = createEndProgress(oldActiveState);
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else if (oldActiveState.isOfType(LEARNING, REWORK)) {
			newActiveState.setNested(true);
			ideaFlowPersistenceService.saveActiveState(newActiveState, oldActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	public void stopConflict(String resolution) {
		IdeaFlowStateEntity oldActiveState = getActiveState();

		if (oldActiveState.isOfType(CONFLICT)) {
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
		startLearningOrRework(LEARNING, comment);
	}

	public void startRework(String comment) {
		startLearningOrRework(REWORK, comment);
	}

	public void stopLearning(String resolution) {
		stopLearningOrRework(LEARNING, resolution);
	}

	public void stopRework(String resolution) {
		stopLearningOrRework(REWORK, resolution);
	}

	private void startLearningOrRework(IdeaFlowStateType type, String comment) {
		IdeaFlowStateEntity oldActiveState = getActiveState();
		IdeaFlowStateEntity newActiveState = createStartState(type, comment);

		if (getContainingState() != null) {
			// TODO: log warning
			throw new InvalidTransitionException();
		}

		IdeaFlowStateType otherNonConflictType = type == LEARNING ? REWORK : LEARNING;
		if (oldActiveState.isOfType(PROGRESS)) {
			IdeaFlowStateEntity stateToSave = createEndProgress(oldActiveState);
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else if (oldActiveState.isOfType(CONFLICT, otherNonConflictType)) {
			IdeaFlowStateEntity previousState = createEndState(oldActiveState, comment);
			newActiveState.setLinkedToPrevious(true);
			ideaFlowPersistenceService.saveTransition(previousState, newActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	private void stopLearningOrRework(IdeaFlowStateType type, String resolution) {
		IdeaFlowStateEntity oldActiveState = getActiveState();

		if (oldActiveState.isOfType(type)) {
			IdeaFlowStateEntity stateToSave = createEndState(oldActiveState, resolution);
			ideaFlowPersistenceService.saveTransition(stateToSave, createStartProgress());
		} else if (oldActiveState.isOfType(CONFLICT)) {
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
