package com.ideaflow.localagent.resources

import com.ideaflow.localagent.ComponentTest
import com.ideaflow.localagent.client.EventClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class EventResourceSpec extends Specification {

	@Autowired
	private EventClient activityClient

	def "startConflict should not explode"() {
		when:
		activityClient.startConflict()

		then:
		notThrown(Throwable)
	}

	def "stopConflict should not explode"() {
		when:
		activityClient.stopConflict()

		then:
		notThrown(Throwable)
	}

}
