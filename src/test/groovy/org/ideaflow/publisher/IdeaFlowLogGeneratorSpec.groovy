package org.ideaflow.publisher

import spock.lang.Specification


class IdeaFlowLogGeneratorSpec extends Specification {

    /* Generates Idea Flow state history in a loggable format  */

    def "WHEN start Conflict, Learning, or Rework SHOULD start corresponding band "() {

    }

    def "WHEN end Conflict, Learning, or Rework SHOULD end corresponding band"() {

    }

    def "WHEN Learning start Rework SHOULD end Learning band and start linked Rework band"() {

    }

    def "WHEN Rework start Learning SHOULD end Rework band and start linked Learning band"() {

    }

    def "WHEN Conflict start Learning SHOULD end Conflict band and start linked Learning band"() {

    }

    def "WHEN Conflict start Rework SHOULD end Conflict band and start linked Rework band"() {

    }

    def "WHEN Learning start Conflict SHOULD continue Learning and start LearningNestedConflict band"() {

    }

    def "WHEN Rework start Conflict SHOULD continue Rework and start ReworkNestedConflict band"() {

    }

    def "WHEN end LearningNestedConflict SHOULD end LearningNestedConflict"() {

    }

    def "WHEN end ReworkNestedConflict SHOULD end ReworkNestedConflict"() {

    }
}
