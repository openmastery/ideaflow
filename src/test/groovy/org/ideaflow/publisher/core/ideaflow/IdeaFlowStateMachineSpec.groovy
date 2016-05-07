package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType
import org.ideaflow.publisher.core.TimeService
import spock.lang.Specification

import java.time.LocalDateTime

import static IdeaFlowStateType.CONFLICT
import static IdeaFlowStateType.LEARNING
import static IdeaFlowStateType.PROGRESS
import static IdeaFlowStateType.REWORK

class IdeaFlowStateMachineSpec extends Specification {

	static class TestTimeService implements TimeService {
		@Override
		LocalDateTime now() {
			// the tests rely on ordering based on start time - put in a sleep so that all start times are
			// different by at least one
			Thread.sleep(1)
			LocalDateTime.now()
		}
	}


	IdeaFlowStateMachine stateMachine
	IdeaFlowInMemoryPersistenceService persistenceService

	def setup() {
		persistenceService = new IdeaFlowInMemoryPersistenceService()
		stateMachine = new IdeaFlowStateMachine(123L, new TestTimeService(), persistenceService)
	}

	private IdeaFlowStateEntity getPersistedState(IdeaFlowStateType type) {
		persistenceService.stateList.find { it.type == type }
	}

	private List<IdeaFlowStateEntity> getPersistedStatesOrderdByStartTime() {
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

	private void assertExpectedStates(IdeaFlowStateType... expectedTypes) {
		List states = getPersistedStatesOrderdByStartTime()
		for (int i = 0; i < expectedTypes.length; i++) {
			assert states.size() > i: "Expected types=${expectedTypes}, actual states=${states}"
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
		stateMachine.endConflict("resolution")

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
		assert getPersistedState(LEARNING).endingComment == "rework" //TODO is this what we really want to do?
	}

	def "WHEN Rework then start Learning SHOULD link Learning state to previous Rework state"() {
		when:
		stateMachine.startTask()
		stateMachine.startRework("rework")
		stateMachine.startLearning("learning")
		stateMachine.endLearning("learning")

		then:
		assertExpectedStates(PROGRESS, REWORK, LEARNING)
		assertActiveState(PROGRESS)
		assert getPersistedState(LEARNING).isLinkedToPrevious()
	}

	def "WHEN Conflict then start Learning SHOULD link Learning state to previous Conflict state"() {
		when:
		stateMachine.startTask()
		stateMachine.startConflict("conflict")
		stateMachine.startLearning("learning")
		stateMachine.endLearning("learning")

		then:
		assertExpectedStates(PROGRESS, CONFLICT, LEARNING)
		assert getPersistedState(LEARNING).isLinkedToPrevious()
		assert getPersistedState(CONFLICT).endingComment == "learning" //TODO is this what we want? conflict resolution is learning comment
	}

	def "WHEN Conflict then start Rework SHOULD link Rework state to previous Conflict state"() {
		when:
		stateMachine.startTask()
		stateMachine.startConflict("conflict")
		stateMachine.startRework("rework")
		stateMachine.endRework("rework")

		then:
		assertExpectedStates(PROGRESS, CONFLICT, REWORK)
		assert getPersistedState(REWORK).isLinkedToPrevious()
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
		stateMachine.endConflict("conflict stop")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
		assert getPersistedState(CONFLICT).isNested()
		assertActiveState(LEARNING)
		assertContainingState(null)
	}

	def "WHEN Rework then start Conflict SHOULD transition to a ReworkNestedConflict state"() {
		when:
		stateMachine.startTask()
		stateMachine.startRework("rework")
		stateMachine.startConflict("conflict")
		stateMachine.endConflict("conflict")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
		assertActiveState(REWORK)
		assert getPersistedState(CONFLICT).isNested()

		when:
		stateMachine.startConflict("conflict")
		stateMachine.endConflict("conflict")
		stateMachine.endRework("rework")

		then:
		assertExpectedStates(PROGRESS, REWORK, CONFLICT, CONFLICT)
		assertActiveState(PROGRESS)
		assertContainingState(null)
	}

	/* Explicitly ending states */

	def "WHEN Learning then stop Learning SHOULD transition to Progress"() {
		when:
		stateMachine.startTask()
		stateMachine.startLearning("learning start")
		stateMachine.endLearning("learning stop")

		then:
		assertExpectedStates(PROGRESS, LEARNING)
		assertActiveState(PROGRESS)
		assertContainingState(null)
	}

	def "WHEN Rework then stop Rework SHOULD transition to Progress"() {
		when:
		stateMachine.startTask()
		stateMachine.startRework("rework start")
		stateMachine.endRework("rework stop")

		then:
		assertExpectedStates(PROGRESS, REWORK)
		assertActiveState(PROGRESS)
		assertContainingState(null)
	}

	def "WHEN Conflict then stop Conflict SHOULD transition to Progress"() {
		when:
		stateMachine.startTask()
		stateMachine.startConflict("conflict")
		stateMachine.endConflict("conflict")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
		assertActiveState(PROGRESS)
	}

	def "WHEN LearningNestedConflict then stop Conflict SHOULD transition to prior Learning state"() {
		when:
		stateMachine.startTask()
		stateMachine.startLearning("learning")
		stateMachine.startConflict("conflict")
		stateMachine.endConflict("conflict")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
		assertActiveState(LEARNING)
	}

	def "WHEN ReworkNestedConflict then stop Conflict SHOULD transition to prior Rework state"() {
		when:
		stateMachine.startTask()
		stateMachine.startRework("rework")
		stateMachine.startConflict("conflict")
		stateMachine.endConflict("conflict")

		then:
		assertExpectedStates(PROGRESS, CONFLICT)
		assertActiveState(REWORK)
	}

	def "WHEN LearningNestedConflict then stop Learning SHOULD unnest the Conflict (same conflict)"() {
		when:
		stateMachine.startTask()
		stateMachine.startLearning("learning")
		stateMachine.startConflict("conflict")

		then:
		assertContainingState(LEARNING)
		assertActiveState(CONFLICT)
		assert persistenceService.activeState.isNested()

		when:
		stateMachine.endLearning("learning")

		then:
		assertExpectedStates(PROGRESS, LEARNING)
		assertActiveState(CONFLICT)
		assertContainingState(null);
		assert persistenceService.activeState.isNested() == false
		assert persistenceService.activeState.isLinkedToPrevious() == true

	}

	def "WHEN ReworkNestedConflict then stop Rework SHOULD unnest the Conflict (same conflict)"() {
		when:
		stateMachine.startTask()
		stateMachine.startRework("rework")
		stateMachine.startConflict("conflict")

		then:
		assertContainingState(REWORK)
		assertActiveState(CONFLICT)
		assert persistenceService.activeState.isNested()

		when:
		stateMachine.endRework("rework")

		then:
		assertExpectedStates(PROGRESS, REWORK)
		assertActiveState(CONFLICT)
		assertContainingState(null);
		assert persistenceService.activeState.isNested() == false
		assert persistenceService.activeState.isLinkedToPrevious() == true
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
