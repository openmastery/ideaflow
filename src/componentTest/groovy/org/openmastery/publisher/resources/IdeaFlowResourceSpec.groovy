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
package org.openmastery.publisher.resources

import org.openmastery.publisher.api.ideaflow.IdeaFlowPartialCompositeState
import org.openmastery.testsupport.BeanCompare
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.ideaflow.IdeaFlowState
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

@ComponentTest
class IdeaFlowResourceSpec extends Specification {

	@Autowired
	private IdeaFlowClient ideaFlowClient
	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BeanCompare ifmStateComparator = new BeanCompare().excludeFields("id", "start", "end")
	private long taskId = 123

	private void assertActiveState(IdeaFlowStateType expectedType, String expectedStartingComment) {
		IdeaFlowState expectedState = IdeaFlowState.builder()
				.taskId(taskId)
				.startingComment(expectedStartingComment)
				.type(expectedType)
				.build()

		IdeaFlowPartialCompositeState compositeState = ideaFlowClient.getActiveState(taskId)
		IdeaFlowState activeState = compositeState.activeState
		ifmStateComparator.assertEquals(expectedState, activeState)
		assert activeState.start != null
		assert activeState.end == null
		assert activeState.endingComment == null
	}

	private void assertStateTransition(IdeaFlowStateType expectedType, String expectedStartingComment, String expectedEndingComment) {
		IdeaFlowStateEntity expectedState = IdeaFlowStateEntity.builder()
				.taskId(taskId)
				.startingComment(expectedStartingComment)
				.endingComment(expectedEndingComment)
				.type(expectedType)
				.build()

		IdeaFlowStateEntity actualState = persistenceService.getStateList(taskId).last()
		ifmStateComparator.assertEquals(expectedState, actualState)
		assert actualState.id != null
		assert actualState.start != null
		assert actualState.end != null
	}

	def "SHOULD start and end conflict"() {
		String question = "my question"
		String answer = "the answer"

		when:
		ideaFlowClient.startConflict(taskId, question)

		then:
		assertActiveState(IdeaFlowStateType.CONFLICT, question)

		when:
		ideaFlowClient.endConflict(taskId, answer)

		then:
		assertStateTransition(IdeaFlowStateType.CONFLICT, question, answer)
		assertActiveState(IdeaFlowStateType.PROGRESS, null)
	}

	def "SHOULD start and end learning"() {
		String startingComment = "start learning"
		String endingComment = "end learning"

		when:
		ideaFlowClient.startLearning(taskId, startingComment)

		then:
		assertActiveState(IdeaFlowStateType.LEARNING, startingComment)

		when:
		ideaFlowClient.endLearning(taskId, endingComment)

		then:
		assertStateTransition(IdeaFlowStateType.LEARNING, startingComment, endingComment)
		assertActiveState(IdeaFlowStateType.PROGRESS, null)
	}

	def "SHOULD start and end rework"() {
		String startingComment = "start rework"
		String endingComment = "end rework"

		when:
		ideaFlowClient.startRework(taskId, startingComment)

		then:
		assertActiveState(IdeaFlowStateType.REWORK, startingComment)

		when:
		ideaFlowClient.endRework(taskId, endingComment)

		then:
		assertStateTransition(IdeaFlowStateType.REWORK, startingComment, endingComment)
		assertActiveState(IdeaFlowStateType.PROGRESS, null)
	}

}
