/**
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.metrics.machine;

import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;
import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.publisher.ideaflow.IdeaFlowPartialStateEntity;
import org.openmastery.publisher.ideaflow.IdeaFlowStateEntity;
import org.openmastery.publisher.security.InvocationContext;
import org.openmastery.time.TimeService;

/**
 * TOOD: investigate http://projects.spring.io/spring-statemachine/
 */
public class IdeaFlowStateMachine {

	private Long taskId;
	private TimeService timeService;
	private IdeaFlowPersistenceService ideaFlowPersistenceService;
	private InvocationContext invocationContext;

	public IdeaFlowStateMachine(Long taskId, TimeService timeService, InvocationContext invocationContext, IdeaFlowPersistenceService ideaFlowPersistenceService) {
		this.taskId = taskId;
		this.timeService = timeService;
		this.invocationContext = invocationContext;
		this.ideaFlowPersistenceService = ideaFlowPersistenceService;
	}

	private IdeaFlowPartialStateEntity getActiveState() {
		IdeaFlowPartialStateEntity state = ideaFlowPersistenceService.getActiveState(taskId);
		if (state == null) {
			state = createStartProgress();
		}
		return state;
	}

	private IdeaFlowPartialStateEntity getContainingState() {
		return ideaFlowPersistenceService.getContainingState(taskId);
	}

	private IdeaFlowPartialStateEntity createStartProgress() {
		return IdeaFlowPartialStateEntity.builder()
				.ownerId(invocationContext.getUserId())
				.taskId(taskId)
				.type(IdeaFlowStateType.PROGRESS)
				.start(timeService.javaNow())
				.build();
	}

	private IdeaFlowStateEntity createEndProgress(IdeaFlowPartialStateEntity startState) {
		return createEndState(startState, null);
	}

	private IdeaFlowPartialStateEntity createStartState(IdeaFlowStateType type, String startingComment) {
		return IdeaFlowPartialStateEntity.builder()
				.ownerId(invocationContext.getUserId())
				.taskId(taskId)
				.type(type)
				.startingComment(startingComment)
				.start(timeService.javaNow())
				.build();
	}

	private IdeaFlowStateEntity createEndState(IdeaFlowPartialStateEntity startState, String endingComment) {
		return IdeaFlowStateEntity.from(startState)
				.ownerId(invocationContext.getUserId())
				.taskId(startState.getTaskId())
				.end(timeService.javaNow())
				.endingComment(endingComment)
				.build();
	}

	public void startTask() {
		ideaFlowPersistenceService.saveActiveState(createStartProgress());
	}

	public void startConflict(String question) {
		IdeaFlowPartialStateEntity oldActiveState = getActiveState();
		IdeaFlowPartialStateEntity newActiveState = createStartState(IdeaFlowStateType.TROUBLESHOOTING, question);

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
		IdeaFlowPartialStateEntity oldActiveState = getActiveState();

		if (oldActiveState.isOfType(IdeaFlowStateType.TROUBLESHOOTING)) {
			IdeaFlowStateEntity stateToSave = createEndState(oldActiveState, resolution);
			IdeaFlowPartialStateEntity containingState = getContainingState();
			IdeaFlowPartialStateEntity newActiveState = containingState != null ? containingState : createStartProgress();
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
		IdeaFlowPartialStateEntity oldActiveState = getActiveState();
		IdeaFlowPartialStateEntity newActiveState = createStartState(type, comment);

		if (getContainingState() != null) {
			// TODO: log warning
			throw new InvalidTransitionException();
		}

		IdeaFlowStateType otherNonConflictType = type == IdeaFlowStateType.LEARNING ? IdeaFlowStateType.REWORK : IdeaFlowStateType.LEARNING;
		if (oldActiveState.isOfType(IdeaFlowStateType.PROGRESS)) {
			IdeaFlowStateEntity stateToSave = createEndProgress(oldActiveState);
			ideaFlowPersistenceService.saveTransition(stateToSave, newActiveState);
		} else if (oldActiveState.isOfType(IdeaFlowStateType.TROUBLESHOOTING, otherNonConflictType)) {
			IdeaFlowStateEntity previousState = createEndState(oldActiveState, comment);
			newActiveState.setLinkedToPrevious(true);
			ideaFlowPersistenceService.saveTransition(previousState, newActiveState);
		} else {
			// TODO: log warning
			throw new InvalidTransitionException();
		}
	}

	private void endLearningOrRework(IdeaFlowStateType type, String resolution) {
		IdeaFlowPartialStateEntity oldActiveState = getActiveState();

		if (oldActiveState.isOfType(type)) {
			IdeaFlowStateEntity stateToSave = createEndState(oldActiveState, resolution);
			ideaFlowPersistenceService.saveTransition(stateToSave, createStartProgress());
		} else if (oldActiveState.isOfType(IdeaFlowStateType.TROUBLESHOOTING)) {
			IdeaFlowPartialStateEntity containingState = getContainingState();
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
