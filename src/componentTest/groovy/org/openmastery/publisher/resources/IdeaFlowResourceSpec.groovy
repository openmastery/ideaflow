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

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.SubtaskMetrics
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class IdeaFlowResourceSpec extends Specification {

	@Autowired
	private IdeaFlowClient ideaFlowClient

	@Autowired
	private TaskClient taskClient
	@Autowired
	private BatchClient batchClient

	@Autowired
	private MockTimeService timeService

	@Autowired
	private IdeaFlowPersistenceService persistenceService



	def "getTimelineForTask SHOULD generate IdeaFlow timeline with all data types"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with all band types", "project")

		NewIFMBatch batch = aRandom.batch().timeSent(timeService.now())
			.newEvent(task.id, timeService.now(), EventType.ACTIVATE, "unpause")
			.newExecutionActivity(task.id, timeService.now(), 15, "TestMe", "JUnit", 0, false)
			.newModificationActivity(task.id, timeService.inFuture(1), 30, 80)
			.newBlockActivity(task.id, timeService.inFuture(2), 500, "Waiting on stuff")
			.newEvent(task.id, timeService.inFuture(3), EventType.DEACTIVATE, "pause")
			.build()

		batchClient.addIFMBatch(batch)

		when:
		IdeaFlowTimeline timeline = ideaFlowClient.getTimelineForTask(task.id)

		then:
		assert timeline != null
		assert timeline.executionEvents.size() == 1
		assert timeline.modificationActivities.size() == 1
		assert timeline.blockActivities.size() == 1
		assert timeline.events.size() == 3 //calendar event generated too


	}

	def "generateRiskSummariesBySubtask SHOULD generate metrics for each subtask"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with a couple subtasks", "project")

		NewIFMBatch batch = aRandom.batch().timeSent(timeService.now())
				.newEvent(task.id, timeService.now(), EventType.ACTIVATE, "unpause")

				.newEvent(task.id, timeService.now(), EventType.SUBTASK, "Subtask 1")
				.newExecutionActivity(task.id, timeService.now(), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.inFuture(1), 30, 80)
				.newExecutionActivity(task.id, timeService.now(), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.inFuture(1), 30, 80)

				.newEvent(task.id, timeService.now(), EventType.SUBTASK, "Subtask 2")
				.newExecutionActivity(task.id, timeService.now(), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.inFuture(1), 30, 80)
				.newExecutionActivity(task.id, timeService.now(), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.inFuture(1), 30, 80)

				.newEvent(task.id, timeService.inFuture(3), EventType.DEACTIVATE, "pause")
				.build()

		batchClient.addIFMBatch(batch)

		when:
		List<SubtaskMetrics> metrics = ideaFlowClient.generateRiskSummariesBySubtask(task.id)

		then:
		assert metrics != null
		assert metrics.size() == 1
		assert metrics.get(0).description == "Subtask 1"
		assert metrics.get(0).metrics.size() == 6
	}

}
