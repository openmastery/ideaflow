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

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimelineValidator
import org.openmastery.publisher.api.ideaflow.SubtaskTimelineOverview
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview
import org.openmastery.publisher.api.ideaflow.TaskTimelineWithAllSubtasks
import org.openmastery.publisher.api.metrics.SubtaskOverview
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.TimelineClient
import org.openmastery.publisher.client.TaskClient
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.user.UserEntity
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class TimelineResourceSpec extends Specification {

	@Autowired
	private TimelineClient ideaFlowClient

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

	def "getTimelineForTask SHOULD return 404 if there is no task"() {
		when:
		ideaFlowClient.getTimelineOverviewForTask(Integer.MIN_VALUE)

		then:
		thrown(NotFoundException)
	}

	def "getTimelineForTask SHOULD not explode if the task is empty"() {
		given:
		Task task = taskClient.createTask("empty", "empty task", "project")

		when:
		TaskTimelineOverview overview = ideaFlowClient.getTimelineOverviewForTask(task.id)

		then:
		assert overview.task.id == task.id
		assert overview.timeline == null
		assert overview.ideaFlowStory == null
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

	def "generateTimelineWithAllSubtasks SHOULD generate IdeaFlow timeline with all subtasks populated"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with a couple subtasks", "project")

		NewIFMBatch batch = aRandom.batch().timeSent(timeService.now())
				.newEvent(task.id, timeService.minutesInFuture(1), EventType.WTF, "WTF is this?!")
				.newExecutionActivity(task.id, timeService.minutesInFuture(5), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(10), 30, 80)
				.newExecutionActivity(task.id, timeService.minutesInFuture(15), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(17), 30, 80)
				.newEvent(task.id, timeService.minutesInFuture(18), EventType.AWESOME, "Yay!")

				.newEvent(task.id, timeService.minutesInFuture(19), EventType.SUBTASK, "Subtask 2")
				.newEvent(task.id, timeService.minutesInFuture(20), EventType.WTF, "WTF is this?!")
				.newExecutionActivity(task.id, timeService.minutesInFuture(22), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(24), 30, 80)
				.newExecutionActivity(task.id, timeService.minutesInFuture(27), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(30), 30, 80)
				.newEvent(task.id, timeService.minutesInFuture(33), EventType.AWESOME, "Yay!")
				.newEvent(task.id, timeService.hoursInFuture(3), EventType.DEACTIVATE, "pause")
				.build()

		batchClient.addIFMBatch(batch)

		when:
		TaskTimelineWithAllSubtasks overview = ideaFlowClient.getTimelineOverviewForTaskWithAllSubtasks(task.id)

		then:
		assert overview.ideaFlowStory.metrics.size() == 5
		assert overview.task.name == "basic"
		assert overview.timeline != null
		assert overview.subtaskTimelines.size() == 2
	}

	def "generateRiskSummariesBySubtask SHOULD generate metrics for each subtask"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with a couple subtasks", "project")

		NewIFMBatch batch = aRandom.batch().timeSent(timeService.now())
				.newEvent(task.id, timeService.now(), EventType.ACTIVATE, "unpause")

				.newEvent(task.id, timeService.minutesInFuture(1), EventType.WTF, "WTF is this?!")
				.newExecutionActivity(task.id, timeService.minutesInFuture(5), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(10), 30, 80)
				.newExecutionActivity(task.id, timeService.minutesInFuture(15), 15, "TestMe", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(17), 30, 80)
				.newEvent(task.id, timeService.minutesInFuture(18), EventType.AWESOME, "Yay!")

				.newEvent(task.id, timeService.minutesInFuture(19), EventType.SUBTASK, "Subtask 2")
				.newEvent(task.id, timeService.minutesInFuture(20), EventType.WTF, "WTF is this?!")
				.newExecutionActivity(task.id, timeService.minutesInFuture(22), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(24), 30, 80)
				.newExecutionActivity(task.id, timeService.minutesInFuture(27), 15, "TestYou", "JUnit", 0, false)
				.newModificationActivity(task.id, timeService.minutesInFuture(30), 30, 80)
				.newEvent(task.id, timeService.minutesInFuture(33), EventType.AWESOME, "Yay!")
				.newEvent(task.id, timeService.hoursInFuture(3), EventType.DEACTIVATE, "pause")
				.build()

		batchClient.addIFMBatch(batch)

		when:
		TaskTimelineOverview overview = ideaFlowClient.getTimelineOverviewForTask(task.id)
		List<Metric<?>> metrics = overview.ideaFlowStory.metrics


		then:
		assert metrics != null
		assert metrics.size() == 5

		when:
		SubtaskTimelineOverview subtaskTimelineOverview = ideaFlowClient.getTimelineOverviewForSubtask(task.id, -1)
		List<Metric<?>> subtaskMetrics = subtaskTimelineOverview.ideaFlowStory.metrics

		then:
		assert subtaskMetrics != null
		assert subtaskMetrics.size() == 5
	}

}
