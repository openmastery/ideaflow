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
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimelineValidator
import org.openmastery.publisher.api.ideaflow.SubtaskTimelineOverview
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview
import org.openmastery.publisher.api.metrics.SubtaskOverview
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.user.UserEntity
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

	@Autowired
	public UserEntity testUser

	private IdeaFlowTimelineValidator getTimelineAndValidator(long taskId) {
		TaskTimelineOverview overview = ideaFlowClient.getTimelineOverviewForTask(taskId)
		assert overview != null
		assert overview.task.id == taskId
		new IdeaFlowTimelineValidator(overview.timeline)
	}

	def "getTimelineForTask SHOULD not explode if the task is empty"() {
		given:
		Task task = taskClient.createTask("empty", "empty task", "project")

		when:
		TaskTimelineOverview overview = ideaFlowClient.getTimelineOverviewForTask(task.id)

		then:
		assert overview.task.id == task.id
		assert overview.timeline == null
		assert overview.subtaskOverviews == []
	}

	def "getTimelineForTask SHOULD generate IdeaFlow timeline with all data types"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with all band types", "project")

		NewIFMBatch batch = aRandom.batch().timeSent(timeService.now())
			.newEvent(task.id, timeService.now(), EventType.ACTIVATE, "unpause")
			.newExecutionActivity(task.id, timeService.secondsInFuture(15), 15, "TestMe", "JUnit", 0, false)
			.newModificationActivity(task.id, timeService.hoursInFuture(1), 30, 80)
			.newBlockActivity(task.id, timeService.hoursInFuture(2), 500, "Waiting on stuff")
			.newEvent(task.id, timeService.hoursInFuture(3), EventType.DEACTIVATE, "pause")
			.build()

		batchClient.addIFMBatch(batch)

		when:
		IdeaFlowTimelineValidator validator = getTimelineAndValidator(task.id)

		then:
		validator.assertExecutionEvents(1)
		validator.assertEvents(1, EventType.SUBTASK)
		validator.assertEvents(1, EventType.CALENDAR)
		validator.assertStrategyBand(0, timeService.now(), timeService.hoursInFuture(3))
		validator.assertValidationComplete()
	}

	def "generateRiskSummariesBySubtask SHOULD generate metrics for each subtask"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with a couple subtasks", "project")

		NewIFMBatch batch = aRandom.batch().timeSent(timeService.now())
				.newEvent(task.id, timeService.now(), EventType.ACTIVATE, "unpause")

				.newEvent(task.id, timeService.now(), EventType.SUBTASK, "Subtask 1")
				.newExecutionActivity(task.id, timeService.now(), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.hoursInFuture(1), 30, 80)
				.newExecutionActivity(task.id, timeService.now(), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.hoursInFuture(1), 30, 80)

				.newEvent(task.id, timeService.now(), EventType.SUBTASK, "Subtask 2")
				.newExecutionActivity(task.id, timeService.now(), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.hoursInFuture(1), 30, 80)
				.newExecutionActivity(task.id, timeService.now(), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.hoursInFuture(1), 30, 80)

				.newEvent(task.id, timeService.hoursInFuture(3), EventType.DEACTIVATE, "pause")
				.build()

		batchClient.addIFMBatch(batch)

		when:
		TaskTimelineOverview overview = ideaFlowClient.getTimelineOverviewForTask(task.id)
		List<SubtaskOverview> metrics = overview.subtaskOverviews

		then:
		assert metrics != null
		assert metrics.get(0).description == "Initial Strategy"
		assert metrics.get(0).metrics.size() == 5
		assert metrics.get(1).description == "Subtask 1"
		assert metrics.get(1).metrics.size() == 5
		assert metrics.get(2).description == "Subtask 2"
		assert metrics.get(2).metrics.size() == 5
		assert metrics.size() == 3

		when:
		SubtaskTimelineOverview subtaskTimelineOverview = ideaFlowClient.getTimelineOverviewForSubtask(task.id, -1)

		then:
		assert subtaskTimelineOverview != null
		assert subtaskTimelineOverview.overview.description == "Initial Strategy"
		assert subtaskTimelineOverview.overview.getMetrics().size() == 5
	}

}
