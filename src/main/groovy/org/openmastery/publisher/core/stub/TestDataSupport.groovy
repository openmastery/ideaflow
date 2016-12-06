/*
 * Copyright 2016 New Iron Group, Inc.
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
package org.openmastery.publisher.core.stub

import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateMachine
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.time.TimeConverter
import org.openmastery.time.TimeService

import java.time.LocalDateTime

import static IdeaFlowStateType.TROUBLESHOOTING
import static IdeaFlowStateType.LEARNING
import static IdeaFlowStateType.REWORK

class TestDataSupport {

	private IdeaFlowPersistenceService persistenceService;
	private TimelineTestSupport testSupport

	TestDataSupport(IdeaFlowPersistenceService persistenceService) {
		this.persistenceService = persistenceService
		this.testSupport = new TimelineTestSupport(persistenceService)
	}

	private boolean doesTaskExist(String name) {
		persistenceService.findTaskWithName(name)
	}

	void createBasicTimelineWithAllBandTypes() {
		if (doesTaskExist("basic")) {
			return
		}

		testSupport.startTask("basic", "Basic timeline with all band types")
		testSupport.advanceTime(0, 0, 15)
		testSupport.startBand(LEARNING, "How should I break down this task?")
		testSupport.advanceTime(0, 45, 0)
		testSupport.endBand(LEARNING, "Starting with the RangeBuilder class")
		testSupport.advanceTime(0, 1, 0)

		testSupport.startSubtask("Write up test cases for RangeBuilder")
		testSupport.advanceTime(1, 13, 0)
		testSupport.startBand(REWORK, "Refactoring RangeBuilder")
		testSupport.advanceTime(0, 40, 12)
		testSupport.endBand(REWORK, "")
		testSupport.advanceTime(1, 15, 30)

		testSupport.startSubtask("Final Validation")
		testSupport.advanceTime(0, 15, 10)
		testSupport.startBand(TROUBLESHOOTING, "Why is the chart throwing a NPE?")
		testSupport.advanceTime(0, 10, 43)
		testSupport.endBand(TROUBLESHOOTING, "chartData wasn't initialized.")
		testSupport.advanceTime(0, 05, 45)
		testSupport.startBand(TROUBLESHOOTING, "Why is the chart not displaying?")
		testSupport.advanceTime(0, 10, 43)
		testSupport.endBand(TROUBLESHOOTING, "jqPlot parameters were wrong.  Passing in [] instead of [[]]")
		testSupport.advanceTime(0, 5, 4)
	}

	void createTrialAndErrorMap() {
		if (doesTaskExist("trialAndError")) {
			return
		}

		testSupport.startTask("trialAndError", "Trial and error timeline map")
		testSupport.advanceTime(0, 0, 15)
		testSupport.startBand(LEARNING, "How does the existing QueryBuilder work?")
		testSupport.advanceTime(0, 45, 0)
		testSupport.endBand(LEARNING, "QueryBuilder is sliced by filter types, lots of duplication")
		testSupport.advanceTime(0, 14, 0)

		testSupport.note("Implement new QueryBuilder replacement")
		testSupport.advanceTime(2, 15, 10)

		testSupport.startBand(TROUBLESHOOTING, "This isn't going to work... Filter types overlap")
		testSupport.advanceTime(0, 15, 0)
		testSupport.startBand(LEARNING, "How can I make the overlapping filters work?")
		testSupport.advanceTime(0, 42, 0)
		testSupport.startBand(REWORK, "Creating a factory for assembling composite filters")
		testSupport.advanceTime(1, 18, 0)

		//unnest this conflict, then link to next trial
		testSupport.startBand(TROUBLESHOOTING, "Crap, this isn't going to work either.")
		testSupport.advanceTime(0, 0, 10)
		testSupport.endBand(REWORK)
		testSupport.advanceTime(0, 5, 16)
		testSupport.startBand(LEARNING, "How can I make the composite filters work?")
		testSupport.advanceTime(0, 35, 4)
		testSupport.startBand(REWORK, "Refactoring to use a CompositeFilterChain")
		testSupport.advanceTime(1, 14, 34)

		//unnest this conflict, then link to next trial
		testSupport.startBand(TROUBLESHOOTING, "The CompositeFilterChain won't support the LagFilter")
		testSupport.advanceTime(0, 0, 10)
		testSupport.endBand(REWORK)
		testSupport.advanceTime(0, 15, 0)
		testSupport.startBand(LEARNING, "How can I support the LagFilter?")
		testSupport.advanceTime(0, 5, 24)
		testSupport.startBand(REWORK, "LagFilter won't be a composite.  Refactoring")
		testSupport.advanceTime(0, 32, 12)
		testSupport.endBand(REWORK, "Ended up with a customized composite filter.")
		testSupport.advanceTime(0, 50, 3)

		//final validation
		testSupport.startSubtask("Final Validation")
		testSupport.advanceTime(0, 0, 10)
		testSupport.startBand(TROUBLESHOOTING, "Why isn't the dropdown populating?")
		testSupport.advanceTime(0, 30, 43)
		testSupport.endBand(TROUBLESHOOTING, "Bunch of little bugs.")
		testSupport.advanceTime(0, 0, 10)
	}

	void createLearningNestedConflictMap() {
		if (doesTaskExist("nested")) {
			return
		}

		testSupport.startTask("nested", "Learning nested conflict map")
		testSupport.advanceTime(0, 1, 30)
		testSupport.startBand(LEARNING, "Where do I need to change the ReportingEngine code? #LackOfFamiliarity")
		testSupport.advanceTime(1, 45, 0)
		testSupport.startBand(TROUBLESHOOTING, "Why is the ReportingEngine sending notifications to Dispatch service?")
		testSupport.advanceTime(0, 35, 0)
		testSupport.endBand(TROUBLESHOOTING, "Dispatch emails the reports out to users.")
		testSupport.advanceTime(1, 14, 2)
		testSupport.startBand(TROUBLESHOOTING, "Why is the ReportingEngine dependent on ProdDB?  Should only be ReportingDB")
		testSupport.advanceTime(0, 46, 30)
		testSupport.endBand(TROUBLESHOOTING, "Looks like dependency was added for real-time dashboard. hmm.")
		testSupport.advanceTime(1, 23, 30)
		testSupport.endBand(LEARNING, "Need to update the NotificationTemplate")
		testSupport.advanceTime(0, 15, 30)
		testSupport.startSubtask("Final Validation")
		testSupport.advanceTime(0, 32, 3)
	}

	void createDetailedConflictMap() {
		if (doesTaskExist("detail")) {
			return
		}

		testSupport.startTask("detail", "Detailed conflict map")
		testSupport.advanceTime(0, 5, 10)
		testSupport.startBand(LEARNING, "What's the plan?")
		testSupport.advanceTime(0, 20, 22)
		testSupport.endBand(LEARNING, "Rework the ChartVisualizer to use TimeBand abstraction")
		testSupport.advanceTime(0, 2, 10)

		testSupport.note("Create TimeBand class")
		testSupport.advanceTime(0, 35, 5)

		testSupport.startSubtask("Refactor ChartVisualizer to use new TimeBand")
		testSupport.advanceTime(1, 15, 0)
		testSupport.startBand(TROUBLESHOOTING, "Why isn't the RangeBuilder working anymore?")
		testSupport.advanceTime(0, 14, 9)
		testSupport.startBand(REWORK, "Range builder uses the duration details to calculate range.  Need to refactor.")
		testSupport.advanceTime(0, 45, 14)
		testSupport.startBand(TROUBLESHOOTING, "Why isn't the RangeBuilder working NOW?")
		testSupport.advanceTime(0, 15, 43)
		testSupport.endBand(TROUBLESHOOTING, "Flipped || with && in the duration code #TranspositionMistake")
		testSupport.advanceTime(0, 1, 12)
		testSupport.startBand(TROUBLESHOOTING, "Why is the duration 5?")
		testSupport.advanceTime(0, 15, 43)
		testSupport.note("Checking out dependencies...")
		testSupport.advanceTime(0, 05, 10)
		testSupport.endBand(TROUBLESHOOTING, "Missing a condition for excluding out of range values.")
		testSupport.advanceTime(0, 5, 3)
		testSupport.endBand(REWORK, "Okay, RangeBuilder is working again.")
		testSupport.advanceTime(0, 3, 5)

		testSupport.startSubtask("Final Validation")
		testSupport.advanceTime(0, 0, 30)
		testSupport.startBand(TROUBLESHOOTING, "Why isn't the chart showing up?")
		testSupport.advanceTime(0, 15, 43)
		testSupport.endBand(TROUBLESHOOTING, "Flipped if/else condition. #TranspositionMistake")
		testSupport.advanceTime(0, 30, 3)
	}


	static class MockTimeService implements TimeService {

		private LocalDateTime now

		MockTimeService() {
			now = LocalDateTime.of(2016, 1, 1, 0, 0)
		}

		@Override
		LocalDateTime now() {
			return now
		}

		@Override
		org.joda.time.LocalDateTime jodaNow() {
			return TimeConverter.toJodaLocalDateTime(now())
		}

		MockTimeService plusHours(int hours) {
			now = now.plusHours(hours)
			this
		}

		MockTimeService plusMinutes(int minutes) {
			now = now.plusMinutes(minutes)
			this
		}

		MockTimeService plusSeconds(int seconds) {
			now = now.plusSeconds(seconds)
			this
		}

	}

	static class TimelineTestSupport {

		private Long taskId
		private Long ownerId = -1L
		private IdeaFlowStateMachine stateMachine
		private IdeaFlowPersistenceService persistenceService
		private MockTimeService timeService = new MockTimeService()

		TimelineTestSupport(IdeaFlowPersistenceService persistenceService) {
			this.persistenceService = persistenceService
		}

		LocalDateTime now() {
			timeService.now()
		}

		List<EventEntity> getEventList() {
			persistenceService.getEventList(taskId)
		}

		void startTask(String name, String description) {
			TaskEntity task = TaskEntity.builder()
					.ownerId(ownerId)
					.name(name)
					.description(description)
					.creationDate(timeService.now())
					.build();

			task = persistenceService.saveTask(task)
			taskId = task.id
			InvocationContext invocationContext = new InvocationContext(userId: ownerId)
			stateMachine = new IdeaFlowStateMachine(taskId, timeService, invocationContext, persistenceService)
			stateMachine.startTask()
		}

		void startSubtask(String comment) {
			EventEntity event = EventEntity.builder()
					.ownerId(ownerId)
					.taskId(taskId)
					.type(EventType.SUBTASK)
					.position(timeService.now())
					.comment(comment)
					.build()
			persistenceService.saveEvent(event)
		}

		void advanceTime(int hours, int minutes, int seconds) {
			timeService.plusHours(hours)
			timeService.plusMinutes(minutes)
			timeService.plusSeconds(seconds)
		}

		void note(String comment) {
			EventEntity note = EventEntity.builder()
					.ownerId(ownerId)
					.taskId(taskId)
					.type(EventType.NOTE)
					.position(timeService.now())
					.comment(comment)
					.build()
			persistenceService.saveEvent(note)
		}

		void idle(int hours) {
			LocalDateTime start = timeService.now()
			timeService.plusHours(hours)
			IdleActivityEntity idleActivity = IdleActivityEntity.builder()
					.ownerId(ownerId)
					.start(start)
					.end(timeService.now()).build()
			persistenceService.saveActivity(idleActivity)
		}

		void startBand(IdeaFlowStateType type, String comment) {
			if (type == LEARNING) {
				stateMachine.startLearning(comment)
			} else if (type == REWORK) {
				stateMachine.startRework(comment)
			} else if (type == TROUBLESHOOTING) {
				stateMachine.startConflict(comment)
			} else {
				throw new RuntimeException("Unknown type: ${type}")
			}
		}

		void endBand(IdeaFlowStateType type) {
			endBand(type, "")
		}

		void endBand(IdeaFlowStateType type, String comment) {
			if (type == LEARNING) {
				stateMachine.endLearning(comment)
			} else if (type == REWORK) {
				stateMachine.endRework(comment)
			} else if (type == TROUBLESHOOTING) {
				stateMachine.endConflict(comment)
			} else {
				throw new RuntimeException("Unknown type: ${type}")
			}
		}

	}

}
