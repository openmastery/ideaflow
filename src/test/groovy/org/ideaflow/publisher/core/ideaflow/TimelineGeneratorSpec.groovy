package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowState
import org.ideaflow.publisher.api.IdeaFlowState
import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleActivity
import org.ideaflow.publisher.core.activity.IdleActivity
import spock.lang.Specification
import spock.lang.Specification

import java.time.Duration
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime

import static org.ideaflow.publisher.api.IdeaFlowStateType.*
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

	IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()
	MockTimeService timeService = new MockTimeService()
	TimelineGenerator generator = new TimelineGenerator()
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

	private void assertTimeBands(List<TimeBand> timeBands, List... expectedStateAndDuration) {
		for (int i = 0; i < expectedStateAndDuration.length; i++) {
			IdeaFlowStateType expectedType = expectedStateAndDuration[i][0]
			Duration expectedDuration = expectedStateAndDuration[i][1]

			assert timeBands.size() > i
			assert timeBands[i].type == expectedType
			assert timeBands[i].duration == expectedDuration
		}
		assert timeBands.size() == expectedStateAndDuration.length
	}

	def "SHOULD calculate duration for all TimeBands"() {
		given:
		stateMachine.startBandAndAdvanceHours(REWORK, 2)
		stateMachine.endBandAndAdvanceHours(REWORK, 1)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		assertTimeBands(segment.timeBands,
		                [PROGRESS, Duration.ofHours(1)],
		                [REWORK, Duration.ofHours(2)],
		                [PROGRESS, Duration.ofHours(1)])
		assert segment.duration == Duration.ofHours(4)
		assert segment.timeBandGroups.isEmpty()
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
		assertTimeBands(segment.timeBands,
		                [PROGRESS, Duration.ofHours(1)],
		                [REWORK, Duration.ofHours(8)])
		assertTimeBands(segment.timeBands[1].nestedBands,
		                [CONFLICT, Duration.ofHours(2)],
		                [CONFLICT, Duration.ofHours(3)])
		assert segment.duration == Duration.ofHours(9)
		assert segment.timeBandGroups.isEmpty()
	}

	def "WHEN IdeaFlowStates are linked SHOULD group bands into a TimeBandGroup"() {
		given:
		stateMachine.startBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.startBandAndAdvanceHours(LEARNING, 3)
		stateMachine.startBandAndAdvanceHours(REWORK, 2)

		when:
		TimelineSegment segment = generatePrimaryTimeline()

		then:
		assertTimeBands(segment.timeBands, [PROGRESS, Duration.ofHours(1)])
		assertTimeBands(segment.timeBandGroups[0].linkedTimeBands,
		                [CONFLICT, Duration.ofHours(1)],
		                [LEARNING, Duration.ofHours(3)],
		                [REWORK, Duration.ofHours(2)])
		assert segment.timeBands.size() == 1
		assert segment.timeBandGroups.size() == 1
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
		assertTimeBands(segment.timeBands, [PROGRESS, Duration.ofHours(1)])
		assertTimeBands(segment.timeBandGroups[0].linkedTimeBands,
		                [REWORK, Duration.ofHours(6)],
		                [LEARNING, Duration.ofHours(4)])
		assertTimeBands(segment.timeBandGroups[0].linkedTimeBands[0].nestedBands,
		                [CONFLICT, Duration.ofHours(1)])
		assert segment.timeBands.size() == 1
		assert segment.timeBandGroups.size() == 1
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
		assertTimeBands(segment.timeBands,
		                [PROGRESS, Duration.ofHours(1)],
		                [PROGRESS, Duration.ofHours(2)]
		)
		assertTimeBands(segment.timeBandGroups[0].linkedTimeBands,
		                [CONFLICT, Duration.ofHours(1)],
		                [LEARNING, Duration.ofHours(2)],
		                [REWORK, Duration.ofHours(1)],
		                [CONFLICT, Duration.ofHours(7)],
		                [LEARNING, Duration.ofHours(5)]
		)

		assert segment.timeBands.size() == 2
		assert segment.timeBandGroups.size() == 1
	}

	def "WHEN idle time is within a Timeband SHOULD subtract relative time from band"() {
		given:
		stateMachine.startBandAndAdvanceHours(CONFLICT, 1)
		stateMachine.endBandAndAdvanceHours(CONFLICT, 2)
		LocalDateTime idleStart = timeService.now()
		stateMachine.startBandAndAdvanceHours(LEARNING, 5)

		IdleActivity idleActivity = IdleActivity.builder()
				.start(idleStart)
				.end(idleStart.plusHours(3)).build()

		when:
		TimelineSegment segment = generatePrimaryTimeline()
		segment = generator.collapseIdleTime(segment, [idleActivity])

		then:
		assertTimeBands(segment.timeBands,
		                [PROGRESS, Duration.ofHours(1)],
		                [CONFLICT, Duration.ofHours(1)],
		                [PROGRESS, Duration.ofHours(2)],
		                [LEARNING, Duration.ofHours(2)]
		)

	}

	def "WHEN idle time is within a nested Timeband SHOULD subtract relative time from parent and child band"() {
		expect:
		assert false
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
