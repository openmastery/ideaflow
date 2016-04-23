package org.ideaflow.publisher.core.ideaflow

import spock.lang.Specification


class TimelineGeneratorSpec extends Specification {

    def "SHOULD calculate relative time for all TimeBands"() {
        expect:
        assert false
    }

    def "WHEN IdeaFlowStates are nested SHOULD create nested TimeBands"() {
        expect:
        assert false
    }

    def "WHEN IdeaFlowStates are linked SHOULD group bands into a TimeBandGroup"() {
        expect:
        assert false
    }

    def "WHEN IdeaFlowStates are linked AND nested SHOULD nest Timebands under corresponding parent within TimeBandGroup"() {
        expect:
        assert false
    }

    def "SHOULD split timeline into TimelineSegments by subtask"() {
        expect:
        assert false
    }

    def "WHEN subtask start is within non-Progress IdeaFlowState SHOULD split TimeBand across two TimelineSegments"() {
        //should we have multiple ids?  One id, highlights the same band on hover that spans thing, but contributes time to both segments
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
