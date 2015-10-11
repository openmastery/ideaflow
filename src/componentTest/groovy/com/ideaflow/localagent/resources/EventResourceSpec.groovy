package com.ideaflow.localagent.resources

import com.ideaflow.localagent.ComponentTest
import com.ideaflow.localagent.client.EventClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class EventResourceSpec extends Specification {

	@Autowired
	private EventClient eventClient

    def "event methods should not explode"() {
        when:
        eventClient.startConflict("task", "my question")
        eventClient.stopConflict("task", "my resolution")
        eventClient.startLearning("task", "learning comment")
        eventClient.stopLearning("task")
        eventClient.startRework("task", "rework comment")
        eventClient.stopRework("task")
        eventClient.addNote("task", "my note")
        eventClient.addCommit("task", "my message")

        then:
        notThrown(Throwable)
    }

}
