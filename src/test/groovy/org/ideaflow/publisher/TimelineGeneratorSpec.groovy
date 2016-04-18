package org.ideaflow.publisher

import spock.lang.Specification


class TimelineGeneratorSpec extends Specification {

    /* Relative Timeline processor, i.e. what UI needs for displaying a visual timeline */

    def "TaskTimeline SHOULD include the startTime and endTime for the task in both absolute time, and relative time in seconds"() {

    }

    def "Timelines SHOULD include a list of non-nested bands with their relative time band positions"() {

    }

    def "Timelines SHOULD include a list of nested bands with their relative time band positions"() {

    }

    def "Timelines SHOULD collapse idle activity so that relative time calculations ignore idle time."() {

    }

    def "Timelines SHOULD include a list of UserNotes for annotating the timeline data with events."() {

    }

    def "TaskTimelines SHOULD include a list of generated DayStart events for annotating the timeline to show when days begin."() {

    }

    def "Timelines SHOULD include a list of generated Disruption events when task-switching occurs during the timeline."() {

    }

    def "Timelines SHOULD include a list of IdleActivities that show relative position + duration during the timeline."() {

    }


    //These are DAYTimeline requirements only

    def "DayTimeline SHOULD include the startTime and endTime for the day in both absolute time, and relative time in seconds"() {

    }

    def "DayTimelines SHOULD include a timeline composite with multiple tasks WHEN there are multiple tasks within a day."() {

    }


}
