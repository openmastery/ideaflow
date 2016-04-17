package org.ideaflow.publisher

import spock.lang.Specification


class IdeaFlowStateMachineSpec extends Specification {

    /* Starting new states without ending old states */

    def "WHEN Progress SHOULD transition to any state"() {

    }

    def "WHEN Learning then start Rework SHOULD link Rework state to previous Learning state"() {

    }

    def "WHEN Rework then start Learning SHOULD link Learning state to previous Rework state"() {

    }

    def "WHEN Conflict then start Learning SHOULD link Learning state to previous Conflict state"() {

    }

    def "WHEN Conflict then start Rework SHOULD link Rework state to previous Conflict state"() {

    }

    def "WHEN Learning then start Conflict SHOULD transition to a LearningNestedConflict state."() {

    }

    def "WHEN Rework then start Conflict SHOULD transition to a ReworkNestedConflict state."() {

    }

    /* Explicitly ending states */


    def "WHEN Learning then stop Learning SHOULD transition to Progress."() {

    }

    def "WHEN Rework then stop Rework SHOULD transition to Progress."() {

    }

    def "WHEN Conflict then stop Conflict SHOULD transition to Progress."() {

    }

    def "WHEN LearningNestedConflict then stop Conflict SHOULD transition to Learning."() {

    }

    def "WHEN ReworkNestedConflict then stop Conflict SHOULD transition to Rework."() {

    }

    def "WHEN LearningNestedConflict then stop Learning SHOULD transition to Conflict (unnested) state."() {

    }

    def "WHEN ReworkNestedConflict then stop Rework SHOULD transition to Conflict (unnested) state."() {

    }

    def "WHEN LearningNestedConflict SHOULD NOT allow start Rework."() {

    }

    def "WHEN ReworkNestedConflict SHOULD NOT allow start Learning."() {

    }
}
