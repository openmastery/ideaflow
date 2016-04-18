package org.ideaflow.publisher.core.ideaflow;

import java.time.LocalDateTime;
import javax.inject.Inject;
import org.ideaflow.publisher.api.IdeaFlowState;
import org.ideaflow.publisher.api.IdeaFlowStateType;
import org.springframework.stereotype.Component;


@Component
public class IdeaFlowStateMachine {

	@Inject
	private IdeaFlowPersistenceService ideaFlowPersistenceService;

	private IdeaFlowState getActiveState() {
		IdeaFlowState state = ideaFlowPersistenceService.getActiveState();
		if (state == null) {
			state = IdeaFlowState.builder().type(IdeaFlowStateType.PROGRESS).build();
		}
		return state;
	}

	private IdeaFlowState getLastActiveState() {
		IdeaFlowState state = ideaFlowPersistenceService.getLastActiveState();
		if (state == null) {
			state = IdeaFlowState.builder().type(IdeaFlowStateType.PROGRESS).build();
		}
		return state;
	}

	public void startConflict(String question) {
		IdeaFlowState activeState = getActiveState();
		IdeaFlowStateType targetType;

		if (activeState.getType() == IdeaFlowStateType.PROGRESS) {
			targetType = IdeaFlowStateType.CONFLICT;
		} else if (activeState.getType() == IdeaFlowStateType.LEARNING) {
			targetType = IdeaFlowStateType.LEARNING_CONFLICT;
		} else if (activeState.getType() == IdeaFlowStateType.REWORK) {
			targetType = IdeaFlowStateType.REWORK_CONFLICT;
		} else {
			throw new InvalidStateException(activeState.getType(), IdeaFlowStateType.CONFLICT);
		}

		IdeaFlowState conflict = IdeaFlowState.builder()
				.type(targetType)
				.startingComment(question)
				.start(LocalDateTime.now())
				.build();
		ideaFlowPersistenceService.save(conflict);
	}

	public void stopConflict(String resolution) {
		IdeaFlowState activeState = getActiveState();
		IdeaFlowState lastActiveState = getLastActiveState();
		IdeaFlowStateType targetType;

		if (activeState.getType() == IdeaFlowStateType.CONFLICT) {
			targetType = IdeaFlowStateType.PROGRESS;
		} else if (activeState.getType() == IdeaFlowStateType.LEARNING_CONFLICT) {

		} else if (activeState.getType() == IdeaFlowStateType.REWORK_CONFLICT) {

		} else {

		}

/*
def "WHEN Conflict then stop Conflict SHOULD transition to Progress."() {
def "WHEN LearningNestedConflict then stop Conflict SHOULD transition to prior Learning state."() {
def "WHEN ReworkNestedConflict then stop Conflict SHOULD transition to prior Rework state."() {
*/
		BandEnd end = new BandEnd(IdeaFlowStateType.CONFLICT, resolution);
	}

	public void startLearning(String comment) {

	}

	public void stopLearning(String resolution) {

	}

	public void startRework(String comment) {

	}

	public void stopRework(String resolution) {

	}


	public static class InvalidStateException extends RuntimeException {
		public InvalidStateException(IdeaFlowStateType from, IdeaFlowStateType to) {
			super("Invalid state transition, from=" + from + ", to=" + to);
		}
	}

}
