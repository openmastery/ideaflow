package com.ideaflow.localagent.resources

import com.ideaflow.localagent.ComponentTest
import com.ideaflow.localagent.client.ActivityClient
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class AccountResourceSpec extends Specification {

    @Autowired
    private ActivityClient activityClient

	def "startConflict should not explode"() {
		when:
		activityClient.startConflict("fubar")

		then:
		notThrown(Throwable)
	}

	def "stopConflict should not explode"() {
		when:
		activityClient.stopConflict("fubar")

		then:
		notThrown(Throwable)
	}

}
