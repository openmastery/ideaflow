package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.api.IdeaFlowState
import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.TimeBand
import org.ideaflow.publisher.api.TimelineSegment
import org.ideaflow.publisher.core.MockTimeService
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK

class TimelineGeneratorSpec extends Specification {

    IdeaFlowStateMachine stateMachine = new IdeaFlowStateMachine()
    IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()
    MockTimeService timeService = new MockTimeService()
    TimelineGenerator generator = new TimelineGenerator()
    LocalDateTime startTime

    def setup() {
        stateMachine.timeService = timeService
        stateMachine.ideaFlowPersistenceService = persistenceService

        startTime = LocalDateTime.from(timeService.now())
        stateMachine.startTask()
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

    private List<TimelineSegment> generateTimelineSegments() {
        List<IdeaFlowState> stateList = getStateListWithActiveCompleted()
        generator.createTimelineSegments(stateList)
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
        timeService.plusHour()
        stateMachine.startRework("")
        timeService.plusHours(2)
        stateMachine.stopRework("")
        timeService.plusMinutes(30)

        when:
        List<TimelineSegment> segmentList = generateTimelineSegments()

        then:
        TimelineSegment segment = segmentList[0]
        assertTimeBands(segment.timeBands,
                        [PROGRESS, Duration.ofHours(1)],
                        [REWORK, Duration.ofHours(2)],
                        [PROGRESS, Duration.ofMinutes(30)])
        assert segment.duration == Duration.parse("PT3h30m")
        assert segmentList.size() == 1
        assert segment.timeBandGroups.isEmpty()
    }

    def "WHEN IdeaFlowStates are nested SHOULD create nested TimeBands"() {
        given:
        timeService.plusHour()
        stateMachine.startRework("")
        timeService.plusHour()
        stateMachine.startConflict("")
        timeService.plusMinutes(30)
        stateMachine.stopConflict("")
        timeService.plusHour()
        stateMachine.startConflict("")
        timeService.plusMinutes(45)
        stateMachine.stopConflict("")
        timeService.plusHour()

        when:
        List<TimelineSegment> segmentList = generateTimelineSegments()

        then:
        TimelineSegment segment = segmentList[0]
        assertTimeBands(segment.timeBands,
                        [PROGRESS, Duration.ofHours(1)],
                        [REWORK, Duration.parse("PT4h15m")])
        assertTimeBands(segment.timeBands[1].nestedBands,
                        [CONFLICT, Duration.ofMinutes(30)],
                        [CONFLICT, Duration.ofMinutes(45)])
        assert segment.duration == Duration.parse("PT5h15m")
        assert segmentList.size() == 1
        assert segment.timeBandGroups.isEmpty()
    }

    def "WHEN IdeaFlowStates are linked SHOULD group bands into a TimeBandGroup"() {
        given:
        timeService.plusHour()
        stateMachine.startConflict("")
        timeService.plusHour()
        stateMachine.startLearning("")
        timeService.plusHours(3)
        stateMachine.startRework("")
        timeService.plusHours(2)

        when:
        List<TimelineSegment> segmentList = generateTimelineSegments()

        then:
        TimelineSegment segment = segmentList[0]
        assertTimeBands(segment.timeBands, [PROGRESS, Duration.ofHours(1)])
        assertTimeBands(segment.timeBandGroups[0].linkedTimeBands,
                        [CONFLICT, Duration.ofHours(1)],
                        [LEARNING, Duration.ofHours(3)],
                        [REWORK, Duration.ofHours(2)])
        assert segment.timeBands.size() == 1
        assert segment.timeBandGroups.size() == 1
        assert segmentList.size() == 1
    }

    def "WHEN IdeaFlowStates are linked AND first state has nested conflicts SHOULD create TimeBandGroup including all bands"() {
        given:
        timeService.plusHour()
        stateMachine.startRework("")
        timeService.plusHours(2)
        stateMachine.startConflict("")
        timeService.plusHour()
        stateMachine.stopConflict("")
        timeService.plusHours(3)
        stateMachine.startLearning("")
        timeService.plusHours(4)

        when:
        List<TimelineSegment> segmentList = generateTimelineSegments()

        then:
        TimelineSegment segment = segmentList[0]
        assertTimeBands(segment.timeBands, [PROGRESS, Duration.ofHours(1)])
        assertTimeBands(segment.timeBandGroups[0].linkedTimeBands,
                        [REWORK, Duration.ofHours(6)],
                        [LEARNING, Duration.ofHours(4)])
        assertTimeBands(segment.timeBandGroups[0].linkedTimeBands[0].nestedBands,
                        [CONFLICT, Duration.ofHours(1)])
        assert segment.timeBands.size() == 1
        assert segment.timeBandGroups.size() == 1
        assert segmentList.size() == 1
    }

    def "WHEN conflict is unnested should be considered linked AND shouldn't fuck up durations"() {
        //conflict <- learning <-rework <- unnested conflict (rework ends after conflict start) <- learning
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


    def "WHEN idle time is within a Timeband SHOULD subtract relative time from band"() {
        expect:
        assert false
    }

    def "WHEN idle time is within a nested Timeband SHOULD subtract relative time from parent and child band"() {
        expect:
        assert false
    }


}
