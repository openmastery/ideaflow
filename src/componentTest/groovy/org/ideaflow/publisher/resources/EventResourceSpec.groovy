/*
 * Copyright 2015 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ideaflow.publisher.resources

import org.ideaflow.publisher.ComponentTest
import org.ideaflow.publisher.client.EventClient
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
