package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowState
import org.ideaflow.publisher.api.IdeaFlowStateType
import spock.lang.Specification

import static org.ideaflow.publisher.api.IdeaFlowStateType.*;

class IdeaFlowStateMachineSpec extends Specification {

	IdeaFlowStateMachine stateMachine = new IdeaFlowStateMachine()
	IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()

	def setup() {
		stateMachine.ideaFlowPersistenceService = persistenceService;
	}

	private IdeaFlowState getPersistedState(IdeaFlowStateType type) {
		persistenceService.stateList.find { it.type == type }
	}

	private List<IdeaFlowState> getPersistedStatesOrderdByStartTime() {
		persistenceService.stateList.sort { it.start }
	}

	private void assertActiveState(IdeaFlowStateType expectedType) {
		assert persistenceService.activeState.isOfType(expectedType)
	}

	private void assertContainingState(IdeaFlowStateType expectedType) {
		if (expectedType == null) {
			assert persistenceService.containingState == null
		} else {
			assert persistenceService.containingState.isOfType(expectedType)
		}
	}

	private void assertExpectedStates(IdeaFlowStateType ... expectedTypes) {
		List<IdeaFlowState> states = getPersistedStatesOrderdByStartTime()
		for (int i = 0; i < expectedTypes.length; i++) {
			assert states.size() > i : "Expected types=${expectedTypes}, actual states=${states}"
			assert states[i].type == expectedTypes[i]
			assert states[i].end != null
		}
		assert states.size() == expectedTypes.length
	}

	/* Starting new states without ending old states */

	def "WHEN Progress then start Conflict SHOULD end Progress and start Conflict"() {
		when:
		stateMachine.startTask()
		stateMachine.startConflict("question")
		stateMachine.stopConflict("resolution")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
	}

	def "WHEN Learning then start Rework SHOULD link Rework state to previous Learning state"() {
		when:
		stateMachine.startTask()
		stateMachine.startLearning("learning")
		stateMachine.startRework("rework")

		then:
		assertExpectedStates(PROGRESS, LEARNING)
		assertActiveState(REWORK)
		assert persistenceService.activeState.isLinkedToPrevious()
	}

	def "WHEN Rework then start Learning SHOULD link Learning state to previous Rework state"() {
		expect:
		false
	}

	def "WHEN Conflict then start Learning SHOULD link Learning state to previous Conflict state"() {
		expect:
		false
	}

	def "WHEN Conflict then start Rework SHOULD link Rework state to previous Conflict state"() {
		expect:
		false
	}

	def "WHEN Learning then start Conflict SHOULD transition to a LearningNestedConflict state"() {
		when:
		stateMachine.startTask()
		stateMachine.startLearning("learning start")
		stateMachine.startConflict("conflict start")

		then:
		assertExpectedStates(PROGRESS)
		assertActiveState(CONFLICT)
		assertContainingState(LEARNING)

		when:
		stateMachine.stopConflict("conflict stop")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
		assert getPersistedState(CONFLICT).isNested()
		assertActiveState(LEARNING)
		assertContainingState(null)
	}

	def "WHEN Rework then start Conflict SHOULD transition to a ReworkNestedConflict state"() {
		expect:
		false
	}

	/* Explicitly ending states */

	def "WHEN Learning then stop Learning SHOULD transition to Progress"() {
		when:
		stateMachine.startTask()
		stateMachine.startLearning("learning start")
		stateMachine.stopLearning("learning stop")

		then:
		assertExpectedStates(PROGRESS, LEARNING)
		assertActiveState(PROGRESS)
		assertContainingState(null)
	}

	def "WHEN Rework then stop Rework SHOULD transition to Progress"() {
		when:
		stateMachine.startTask()
		stateMachine.startRework("rework start")
		stateMachine.stopRework("rework stop")

		then:
		assertExpectedStates(PROGRESS, REWORK)
		assertActiveState(PROGRESS)
		assertContainingState(null)
	}

	def "WHEN Conflict then stop Conflict SHOULD transition to Progress"() {
		expect:
		false
	}

	def "WHEN LearningNestedConflict then stop Conflict SHOULD transition to prior Learning state"() {
		expect:
		false
	}

	def "WHEN ReworkNestedConflict then stop Conflict SHOULD transition to prior Rework state"() {
		expect:
		false
	}

	def "WHEN LearningNestedConflict then stop Learning SHOULD unnest the Conflict (same conflict)"() {
		expect:
		false
	}

	def "WHEN ReworkNestedConflict then stop Rework SHOULD unnest the Conflict (same conflict)"() {
		expect:
		false
	}

	def "WHEN LearningNestedConflict SHOULD NOT allow start Rework (disabled)"() {
		given:
		stateMachine.startTask()
		stateMachine.startLearning("learning start")
		stateMachine.startConflict("conflict start")

		when:
		stateMachine.startRework("rework start")

		then:
		thrown(IdeaFlowStateMachine.InvalidTransitionException)
	}

	def "WHEN ReworkNestedConflict SHOULD NOT allow start Learning (disabled)"() {
		given:
		stateMachine.startTask()
		stateMachine.startRework("rework start")
		stateMachine.startConflict("conflict start")

		when:
		stateMachine.startLearning("learning start")

		then:
		thrown(IdeaFlowStateMachine.InvalidTransitionException)
	}

}
