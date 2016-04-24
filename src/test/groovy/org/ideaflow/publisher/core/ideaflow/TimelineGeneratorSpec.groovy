package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowState
import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBandGroup
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleActivityEntity
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK

class TimelineGeneratorSpec extends Specification {

	static class TimelineStateMachine {

		private IdeaFlowStateMachine delegate
		private MockTimeService timeService

		TimelineStateMachine(IdeaFlowPersistenceService persistenceService, MockTimeService timeService) {
			this.delegate = new IdeaFlowStateMachine()
			delegate.timeService = timeService
			delegate.ideaFlowPersistenceService = persistenceService
			this.timeService = timeService
		}

		void startTaskAndAdvanceHours(int hours) {
			delegate.startTask()
			timeService.plusHours(hours)
		}

		void advanceHours(int hours) {
			timeService.plusHours(hours)
		}

		IdleActivityEntity idle(int hours) {
			LocalDateTime start = timeService.now()
			timeService.plusHours(hours)
			IdleActivityEntity.builder()
					.start(start)
					.end(timeService.now()).build()
		}

		void startBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
			if (type == LEARNING) {
				delegate.startLearning("")
			} else if (type == REWORK) {
				delegate.startRework("")
			} else if (type == CONFLICT) {
				delegate.startConflict("")
			} else {
				throw new RuntimeException("Unknown type: ${type}")
			}
			timeService.plusHours(hours)
		}

		void endBandAndAdvanceHours(IdeaFlowStateType type, int hours) {
			if (type == LEARNING) {
				delegate.stopLearning("")
			} else if (type == REWORK) {
				delegate.stopRework("")
			} else if (type == CONFLICT) {
				delegate.stopConflict("")
			} else {
				throw new RuntimeException("Unknown type: ${type}")
			}
			timeService.plusHours(hours)
		}

	}

	private static class TimelineSegmentValidator {

		private int expectedTimeBandCount = 0
		private int expectedNestedTimeBandCount = 0
		private int expectedLinkedTimeBandCount = 0

		private void assertExpectedValues(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
			assert timeBands[index].type == expectedType
			assert timeBands[index].duration == expectedDuration
		}

		void assertTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
			assertExpectedValues(timeBands, index, expectedType, expectedDuration)
			expectedTimeBandCount++
		}

		void assertNestedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
			assertExpectedValues(timeBands, index, expectedType, expectedDuration)
			expectedNestedTimeBandCount++
		}

		void assertLinkedTimeBand(List<TimeBand> timeBands, int index, IdeaFlowStateType expectedType, Duration expectedDuration) {
			assertExpectedValues(timeBands, index, expectedType, expectedDuration)
			expectedLinkedTimeBandCount++
		}

		void assertValidationComplete(TimelineSegment segment) {
			assert expectedTimeBandCount == segment.timeBands.size()
			assert expectedLinkedTimeBandCount == countLinkedTimeBands(segment)
			assert expectedNestedTimeBandCount == countNestedBands(segment)
		}

		private int countLinkedTimeBands(TimelineSegment segment) {
			int linkedTimeBandCount = 0
			segment.timeBandGroups.each {  TimeBandGroup group ->
				linkedTimeBandCount += group.linkedTimeBands.size()
			}
			linkedTimeBandCount
		}

		private int countNestedBands(TimelineSegment segment) {
			int nestedBandCount = sumNestedTimeBands(segment.timeBands)
			segment.timeBandGroups.each { TimeBandGroup group ->
				nestedBandCount += sumNestedTimeBands(group.linkedTimeBands)
			}
			nestedBandCount
		}

		private int sumNestedTimeBands(List<TimeBand> timeBands) {
			timeBands.sum { TimeBand timeBand -> timeBand.nestedBands.size() } as int
		}

	}

	IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()
	MockTimeService timeService = new MockTimeService()
	TimelineGenerator generator = new TimelineGenerator()
	TimelineSegmentValidator validator = new TimelineSegmentValidator()
	TimelineStateMachine stateMachine
	LocalDateTime startTime

	def setup() {
		startTime = LocalDateTime.from(timeService.now())
		stateMachine = new TimelineStateMachine(persistenceService, timeService)
		stateMachine.startTaskAndAdvanceHours(1)
	}

	private List<IdeaFlowState> getStateListWithActiveCompleted() {
		List<IdeaFlowState> stateList = new ArrayList(persistenceService.getStateList())
		completeAndAddStateIfNotNull(stateList, persistenceService.activeState)
		completeAndAddStateIfNotNull(stateList, persistenceService.containingState)
		stateList
	}

	private void completeAndAddStateIfNotNull(List<IdeaFlowState> stateList, IdeaFlowState state) {
		if (state) {
			stateList << IdeaFlowState.from(state)
					.end(timeService.now())
					.endingComment("")
					.build();
		}
	}

	private TimelineSegment generatePrimaryTimeline() {
		List<IdeaFlowState> stateList = getStateListWithActiveCompleted()
		generator.createPrimaryTimeline(stateList)
	}

	def "SHOULD calculate duration for all TimeBands"() {
		given:
		stateMachine.startBandAndAdvanceHours(REWORK, 2)
		stateMachine.endBandAndAdvanceHours(REWORK, 1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 1, REWORK, Duration.ofHours(2))
		validator.assertTimeBand(segment.timeBands, 2, PROGRESS, Duration.ofHours(1))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(4)
	}

	def "WHEN IdeaFlowStates are nested SHOULD create nested TimeBands"() {
		given:
		stateMachine.startBandAndAdvanceHours(REWORK, 1)
		stateMachine.startBandAndAdvanceHours(CONFLICT, 2)
		stateMachine.endBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.startBandAndAdvanceHours(CONFLICT, 3)
		stateMachine.endBandAndAdvanceHours(CONFLICT, 1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()


		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 1, REWORK, Duration.ofHours(8))
		List<TimeBand> nestedBands = segment.timeBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, CONFLICT, Duration.ofHours(2))
		validator.assertNestedTimeBand(nestedBands, 1, CONFLICT, Duration.ofHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(9)
	}

	def "WHEN IdeaFlowStates are linked SHOULD group bands into a TimeBandGroup"() {
		given:
		stateMachine.startBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.startBandAndAdvanceHours(LEARNING, 3)
		stateMachine.startBandAndAdvanceHours(REWORK, 2)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		List groupedTimeBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(groupedTimeBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertLinkedTimeBand(groupedTimeBands, 1, LEARNING, Duration.ofHours(3))
		validator.assertLinkedTimeBand(groupedTimeBands, 2, REWORK, Duration.ofHours(2))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(7)
	}

	def "WHEN IdeaFlowStates are linked AND first state has nested conflicts SHOULD create TimeBandGroup including all bands"() {
		given:
		stateMachine.startBandAndAdvanceHours(REWORK, 2)
		stateMachine.startBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.endBandAndAdvanceHours(CONFLICT, 3)
		stateMachine.startBandAndAdvanceHours(LEARNING, 4)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		List linkedTimeBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedTimeBands, 0, REWORK, Duration.ofHours(6))
		validator.assertLinkedTimeBand(linkedTimeBands, 1, LEARNING, Duration.ofHours(4))
		List nestedTimeBands = segment.timeBandGroups[0].linkedTimeBands[0].nestedBands
		validator.assertNestedTimeBand(nestedTimeBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(11)
	}

	def "WHEN conflict is unnested SHOULD be considered linked AND previous duration should be reduced by the band overlap"() {
		given:

		//conflict <- learning <-rework <- unnested conflict (rework ends after conflict start) <- learning
		stateMachine.startBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.startBandAndAdvanceHours(LEARNING, 2)
		stateMachine.startBandAndAdvanceHours(REWORK, 1)
		stateMachine.startBandAndAdvanceHours(CONFLICT, 3) //nested
		stateMachine.endBandAndAdvanceHours(REWORK, 4) //unnest the conflict so it's linkable
		stateMachine.startBandAndAdvanceHours(LEARNING, 5)
		stateMachine.endBandAndAdvanceHours(LEARNING, 2) //finish the group

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		List linkedTimeBands = segment.timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedTimeBands, 0, CONFLICT, Duration.ofHours(1))
		validator.assertLinkedTimeBand(linkedTimeBands, 1, LEARNING, Duration.ofHours(2))
		validator.assertLinkedTimeBand(linkedTimeBands, 2, REWORK, Duration.ofHours(1))
		validator.assertLinkedTimeBand(linkedTimeBands, 3, CONFLICT, Duration.ofHours(7))
		validator.assertLinkedTimeBand(linkedTimeBands, 4, LEARNING, Duration.ofHours(5))
		assert segment.duration == Duration.ofHours(19)
	}

	def "WHEN idle time is within a Timeband SHOULD subtract relative time from band"() {
		given:
		stateMachine.startBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.endBandAndAdvanceHours(CONFLICT, 2)
		stateMachine.startBandAndAdvanceHours(LEARNING, 1)
		IdleActivityEntity idleActivity = stateMachine.idle(3)
		stateMachine.advanceHours(1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()
		generator.collapseIdleTime(segment, [idleActivity])

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 1, CONFLICT, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 2, PROGRESS, Duration.ofHours(2))
		validator.assertTimeBand(segment.timeBands, 3, LEARNING, Duration.ofHours(2))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(6)
	}

	def "WHEN idle time is within a nested Timeband SHOULD subtract relative time from parent and child band"() {
		given:
		stateMachine.startBandAndAdvanceHours(LEARNING, 1)
		stateMachine.startBandAndAdvanceHours(CONFLICT, 2)
		IdleActivityEntity idleActivity = stateMachine.idle(4)
		stateMachine.advanceHours(1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()
		generator.collapseIdleTime(segment, [idleActivity])

		then:
		validator.assertTimeBand(segment.timeBands, 0, PROGRESS, Duration.ofHours(1))
		validator.assertTimeBand(segment.timeBands, 1, LEARNING, Duration.ofHours(4))
		List nestedBands = segment.timeBands[1].nestedBands
		validator.assertNestedTimeBand(nestedBands, 0, CONFLICT, Duration.ofHours(3))
		validator.assertValidationComplete(segment)
		assert segment.duration == Duration.ofHours(5)
	}

	def "SHOULD split timeline into multiple TimelineSegments by subtask"() {
		expect:
		assert false
	}

	def "WHEN subtask start is within Timeband SHOULD split timeband across TimelineSegments"() {
		expect:
		assert false
	}

	def "WHEN subtask start is within nested Timeband SHOULD split containing and nested bands across TimelineSegments"() {
		expect:
		assert false
	}


}
