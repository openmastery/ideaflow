package org.ideaflow.publisher.resources

import org.ideaflow.publisher.api.IdeaFlowStateType
import org.ideaflow.publisher.api.Timeline
import org.ideaflow.publisher.core.TimeService
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowInMemoryPersistenceService
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateMachine
import org.ideaflow.publisher.core.timeline.TimelineGenerator

import java.time.LocalDateTime

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.IdeaFlowStateType.REWORK

class TestDataSupport {

	Timeline createBasicTimelineWithAllBandTypes() {
		TimelineTestSupport testSupport = new TimelineTestSupport()

		testSupport.startTask()
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
		testSupport.startBand(CONFLICT, "Why is the chart throwing a NPE?")
		testSupport.advanceTime(0, 10, 43)
		testSupport.endBand(CONFLICT, "chartData wasn't initialized.")
		testSupport.advanceTime(0, 05, 45)
		testSupport.startBand(CONFLICT, "Why is the chart not displaying?")
		testSupport.advanceTime(0, 10, 43)
		testSupport.endBand(CONFLICT, "jqPlot parameters were wrong.  Passing in [] instead of [[]]")
		testSupport.advanceTime(0, 5, 4)

		testSupport.createTimeline()
	}

	Timeline createTrialAndErrorMap() {
		TimelineTestSupport testSupport = new TimelineTestSupport()

		testSupport.startTask()
		testSupport.advanceTime(0, 0, 15)
		testSupport.startBand(LEARNING, "How does the existing QueryBuilder work?")
		testSupport.advanceTime(0, 45, 0)
		testSupport.endBand(LEARNING, "QueryBuilder is sliced by filter types, lots of duplication")
		testSupport.advanceTime(0, 14, 0)

		testSupport.startSubtask("Implement new QueryBuilder replacement")
		testSupport.advanceTime(2, 15, 10)

		testSupport.startBand(CONFLICT, "This isn't going to work... Filter types overlap")
		testSupport.advanceTime(0, 15, 0)
		testSupport.startBand(LEARNING, "How can I make the overlapping filters work?")
		testSupport.advanceTime(0, 42, 0)
		testSupport.startBand(REWORK, "Creating a factory for assembling composite filters")
		testSupport.advanceTime(1, 18, 0)

		//unnest this conflict, then link to next trial
		testSupport.startBand(CONFLICT, "Crap, this isn't going to work either.")
		testSupport.advanceTime(0, 0, 10)
		testSupport.endBand(REWORK)
		testSupport.advanceTime(0, 5, 16)
		testSupport.startBand(LEARNING, "How can I make the composite filters work?")
		testSupport.advanceTime(0, 35, 4)
		testSupport.startBand(REWORK, "Refactoring to use a CompositeFilterChain")
		testSupport.advanceTime(1, 14, 34)

		//unnest this conflict, then link to next trial
		testSupport.startBand(CONFLICT, "The CompositeFilterChain won't support the LagFilter")
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
		testSupport.startBand(CONFLICT, "Why isn't the dropdown populating?")
		testSupport.advanceTime(0, 30, 43)
		testSupport.endBand(CONFLICT, "Bunch of little bugs.")
		testSupport.advanceTime(0, 0, 10)

		testSupport.createTimeline()
	}

	Timeline createLearningNestedConflictMap() {
		TimelineTestSupport testSupport = new TimelineTestSupport()

		testSupport.startTask()
		testSupport.advanceTime(0, 1, 30)
		testSupport.startBand(LEARNING, "Where do I need to change the ReportingEngine code? #LackOfFamiliarity")
		testSupport.advanceTime(1, 45, 0)
		testSupport.startBand(CONFLICT, "Why is the ReportingEngine sending notifications to Dispatch service?")
		testSupport.advanceTime(0, 35, 0)
		testSupport.endBand(CONFLICT, "Dispatch emails the reports out to users.")
		testSupport.advanceTime(1, 14, 2)
		testSupport.startBand(CONFLICT, "Why is the ReportingEngine dependent on ProdDB?  Should only be ReportingDB")
		testSupport.advanceTime(0, 46, 30)
		testSupport.endBand(CONFLICT, "Looks like dependency was added for real-time dashboard. hmm.")
		testSupport.advanceTime(1, 23, 30)
		testSupport.endBand(LEARNING, "Need to update the NotificationTemplate")
		testSupport.advanceTime(0, 15, 30)
		testSupport.startSubtask("Final Validation")
		testSupport.advanceTime(0, 32, 3)

		testSupport.createTimeline()
	}

	Timeline createDetailedConflictMap() {
		TimelineTestSupport testSupport = new TimelineTestSupport()

		testSupport.startTask()
		testSupport.advanceTime(0, 5, 10)
		testSupport.startBand(LEARNING, "What's the plan?")
		testSupport.advanceTime(0, 20, 22)
		testSupport.endBand(LEARNING, "Rework the ChartVisualizer to use TimeBand abstraction")
		testSupport.advanceTime(0, 2, 10)
		testSupport.startSubtask("Extract TimeBand class")
		testSupport.advanceTime(0, 35, 5)
		testSupport.startSubtask("Refactor ChartVisualizer to use new TimeBand")
		testSupport.advanceTime(1, 15, 0)

		testSupport.startBand(CONFLICT, "Why isn't the RangeBuilder working anymore?")
		testSupport.advanceTime(0, 14, 9)
		testSupport.startBand(REWORK, "Range builder uses the duration details to calculate range.  Need to refactor.")
		testSupport.advanceTime(0, 45, 14)
		testSupport.startBand(CONFLICT, "Why isn't the RangeBuilder working NOW?")
		testSupport.advanceTime(0, 15, 43)
		testSupport.endBand(CONFLICT, "Flipped || with && in the duration code #TranspositionMistake")
		testSupport.advanceTime(0, 1, 12)
		testSupport.startBand(CONFLICT, "Why is the duration 5?")
		testSupport.advanceTime(0, 15, 43)
		testSupport.endBand(CONFLICT, "Missing a condition for excluding out of range values.")
		testSupport.advanceTime(0, 5, 3)
		testSupport.endBand(REWORK, "Okay, RangeBuilder is working again.")
		testSupport.advanceTime(0, 3, 5)

		testSupport.startSubtask("Final Validation")
		testSupport.advanceTime(0, 0, 30)
		testSupport.startBand(CONFLICT, "Why isn't the chart showing up?")
		testSupport.advanceTime(0, 15, 43)
		testSupport.endBand(CONFLICT, "Flipped if/else condition. #TranspositionMistake")
		testSupport.advanceTime(0, 30, 3)

		testSupport.createTimeline()
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

		private IdeaFlowStateMachine stateMachine
		private MockTimeService timeService = new MockTimeService()
		private IdeaFlowInMemoryPersistenceService persistenceService = new IdeaFlowInMemoryPersistenceService()

		TimelineTestSupport() {
			this.stateMachine = new IdeaFlowStateMachine()
			stateMachine.timeService = timeService
			stateMachine.ideaFlowPersistenceService = persistenceService
		}

		LocalDateTime now() {
			timeService.now()
		}

		List<IdeaFlowStateEntity> getStateListWithActiveCompleted() {
			List<IdeaFlowStateEntity> stateList = new ArrayList(persistenceService.getStateList())
			completeAndAddStateIfNotNull(stateList, persistenceService.activeState)
			completeAndAddStateIfNotNull(stateList, persistenceService.containingState)
			stateList
		}

		List<IdleTimeBandEntity> getIdleActivityList() {
			persistenceService.getIdleTimeBandList()
		}

		List<EventEntity> getEventList() {
			persistenceService.getEventList()
		}

		private void completeAndAddStateIfNotNull(List<IdeaFlowStateEntity> stateList, IdeaFlowStateEntity state) {
			if (state) {
				stateList << IdeaFlowStateEntity.from(state)
						.end(timeService.now())
						.endingComment("")
						.build();
			}
		}

		void startTask() {
			stateMachine.startTask()
		}

		void startSubtask(String comment) {
			EventEntity event = EventEntity.builder()
					.eventType(EventEntity.Type.SUBTASK)
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

		void idle(int hours) {
			LocalDateTime start = timeService.now()
			timeService.plusHours(hours)
			IdleTimeBandEntity idleActivity = IdleTimeBandEntity.builder()
					.start(start)
					.end(timeService.now()).build()
			persistenceService.saveIdleActivity(idleActivity)
		}

		void startBand(IdeaFlowStateType type, String comment) {
			if (type == LEARNING) {
				stateMachine.startLearning(comment)
			} else if (type == REWORK) {
				stateMachine.startRework(comment)
			} else if (type == CONFLICT) {
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
				stateMachine.stopLearning(comment)
			} else if (type == REWORK) {
				stateMachine.stopRework(comment)
			} else if (type == CONFLICT) {
				stateMachine.stopConflict(comment)
			} else {
				throw new RuntimeException("Unknown type: ${type}")
			}
		}

		Timeline createTimeline() {
			TimelineGenerator generator = new TimelineGenerator()
			generator.createTimeline(persistenceService.stateList, persistenceService.idleTimeBandList, persistenceService.eventList)
		}
	}

}
