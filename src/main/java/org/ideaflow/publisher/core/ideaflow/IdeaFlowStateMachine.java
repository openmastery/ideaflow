package org.ideaflow.publisher.core.ideaflow;

import javax.inject.Inject;
import org.ideaflow.publisher.api.IdeaFlowState;
import org.ideaflow.publisher.api.IdeaFlowStateType;
import org.ideaflow.publisher.core.TimeService;
import org.springframework.stereotype.Component;

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT;
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING;
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS;
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK;

/**
 * TOOD: investigate http://projects.spring.io/spring-statemachine/
 */
@Component
public class IdeaFlowStateMachine {

	@Inject
	private TimeService timeService;
	@Inject
	private IdeaFlowPersistenceService ideaFlowPersistenceService;

	private IdeaFlowState getActiveState() {
		IdeaFlowState state = ideaFlowPersistenceService.getActiveState();
		if (state == null) {
			state = createStartProgress();
		}
		return state;
	}

	private IdeaFlowState getContainingState() {
		return ideaFlowPersistenceService.getContainingState();
	}

	private IdeaFlowState createStartProgress() {
		return IdeaFlowState.builder()
				.type(PROGRESS)
				.start(timeService.now())
				.build();
	}

	private IdeaFlowState createEndProgress(IdeaFlowState startState) {
		return createEndState(startState, null);
	}

	private IdeaFlowState createStartState(IdeaFlowStateType type, String startingComment) {
		return IdeaFlowState.builder()
				.type(type)
				.startingComment(startingComment)
				.start(timeService.now())
				.build();
	}

	private IdeaFlowState createEndState(IdeaFlowState startState, String endingComment) {
		return IdeaFlowState.from(startState)
				.end(timeService.now())
				.endingComment(endingComment)
				.build();
	}

	public void startTask() {
		ideaFlowPersistenceService.saveActiveState(createStartProgress());
	}

	public void startConflict(String question) {
		IdeaFlowState oldActiveState = getActiveState();
		IdeaFlowState newActiveState = createStartState(CONFLICT, question);

		if (oldActiveState.isOfType(PROGRESS)) {
			IdeaFlowState stateToSave = createEndProgress(oldActiveState);
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
		IdeaFlowState oldActiveState = getActiveState();

		if (oldActiveState.isOfType(CONFLICT)) {
			IdeaFlowState stateToSave = createEndState(oldActiveState, resolution);
			IdeaFlowState containingState = getContainingState();
			IdeaFlowState newActiveState = containingState != null ? containingState : createStartProgress();
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	public void startLearning(String comment) {
		startNonConflict(LEARNING, comment);
	}

	public void startRework(String comment) {
		startNonConflict(REWORK, comment);
	}

	public void stopLearning(String resolution) {
		stopNonConflict(LEARNING, resolution);
	}

	public void stopRework(String resolution) {
		stopNonConflict(REWORK, resolution);
	}

	private void startNonConflict(IdeaFlowStateType type, String comment) {
		IdeaFlowState oldActiveState = getActiveState();
		IdeaFlowState newActiveState = createStartState(type, comment);

		if (getContainingState() != null) {
			// TODO: log warning
			throw new InvalidTransitionException();
		}

		IdeaFlowStateType otherNonConflictType = type == LEARNING ? REWORK : LEARNING;
		if (oldActiveState.isOfType(PROGRESS)) {
			IdeaFlowState stateToSave = createEndProgress(oldActiveState);
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else if (oldActiveState.isOfType(CONFLICT, otherNonConflictType)) {
			IdeaFlowState previousState = createEndState(oldActiveState, comment);
			newActiveState.setLinkedToPrevious(true);
			ideaFlowPersistenceService.saveTransition(previousState, newActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	private void stopNonConflict(IdeaFlowStateType type, String resolution) {
		IdeaFlowState oldActiveState = getActiveState();

		if (oldActiveState.isOfType(type)) {
			IdeaFlowState stateToSave = createEndState(oldActiveState, resolution);
			ideaFlowPersistenceService.saveTransition(stateToSave, createStartProgress());
		} else if (oldActiveState.isOfType(CONFLICT)) {
			IdeaFlowState containingState = getContainingState();
			if (containingState != null) {
				IdeaFlowState stateToSave = createEndState(containingState, resolution);
				oldActiveState.setNested(false);
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
		public InvalidTransitionException() {}
	}

}
