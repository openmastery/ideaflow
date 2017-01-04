package org.openmastery.publisher.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.timeline.BandTimeline
import org.openmastery.publisher.client.BatchClient
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.client.TimelineClient
import org.openmastery.publisher.client.TaskClient

import org.openmastery.publisher.core.timeline.TimelineValidator
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.TROUBLESHOOTING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.REWORK

@Ignore
@ComponentTest
class PrimaryScenarioSpec extends Specification {

	@Autowired
	private MockTimeService timeService
	@Autowired
	private TaskClient taskClient
	@Autowired
	private TimelineClient ideaFlowClient
	@Autowired
	private EventClient eventClient
	@Autowired
	private BatchClient activityClient

	private LocalDateTime start

	def setup() {
		start = timeService.javaNow()
	}

	def createBasicTimelineWithAllBandTypes() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with all band types", "project")
		Long taskId = task.id

		timeService.advanceTime(0, 0, 15)
		ideaFlowClient.startLearning(taskId, "How should I break down this task?")
		timeService.advanceTime(0, 45, 0)
		ideaFlowClient.endLearning(taskId, "Starting with the RangeBuilder class")
		timeService.advanceTime(0, 1, 0)

		eventClient.createSubtask(taskId, "Write up test cases for RangeBuilder")
		timeService.advanceTime(1, 13, 0)
		ideaFlowClient.startRework(taskId, "Refactoring RangeBuilder")
		timeService.advanceTime(0, 40, 12)
		ideaFlowClient.endRework(taskId, "")
		timeService.advanceTime(1, 15, 30)

		eventClient.createSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 15, 10)
		ideaFlowClient.startConflict(taskId, "Why is the chart throwing a NPE?")
		timeService.advanceTime(0, 10, 43)
		ideaFlowClient.endConflict(taskId, "chartData wasn't initialized.")
		timeService.advanceTime(0, 05, 45)
		ideaFlowClient.startConflict(taskId, "Why is the chart not displaying?")
		timeService.advanceTime(0, 10, 43)
		ideaFlowClient.endConflict(taskId, "jqPlot parameters were wrong.  Passing in [] instead of [[]]")
		timeService.advanceTime(0, 5, 4)
		activityClient.addEditorActivity(taskId, timeService.now(), 10l, "/some/path", true)

		when:
		BandTimeline bandTimeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		TimelineValidator validator = new TimelineValidator(bandTimeline)
		validator.assertIdeaFlowBand(PROGRESS, Duration.ofSeconds(15))
		validator.assertIdeaFlowBand(LEARNING, Duration.ofMinutes(45), "How should I break down this task?", "Starting with the RangeBuilder class")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT1h14m"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT1h13m"), "Write up test cases for RangeBuilder")
		validator.assertIdeaFlowBand(REWORK, Duration.parse("PT40m12s"), "Refactoring RangeBuilder", "")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT1h30m40s"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT15m10s"), "Final Validation")
		validator.assertIdeaFlowBand(TROUBLESHOOTING, Duration.parse("PT10m43s"), "Why is the chart throwing a NPE?", "chartData wasn't initialized.")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT5m45s"))
		validator.assertIdeaFlowBand(TROUBLESHOOTING, Duration.parse("PT10m43s"), "Why is the chart not displaying?", "jqPlot parameters were wrong.  Passing in [] instead of [[]]")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT5m4s"))
		validator.assertValidationComplete()
	}

	def createTrialAndErrorMap() {
		given:
		Task task = taskClient.createTask("trial", "create trial and error map", "project")
		Long taskId = task.id

		timeService.advanceTime(0, 0, 15)
		ideaFlowClient.startLearning(taskId, "How does the existing QueryBuilder work?")
		timeService.advanceTime(0, 45, 0)
		ideaFlowClient.endLearning(taskId, "QueryBuilder is sliced by filter types, lots of duplication")
		timeService.advanceTime(0, 14, 0)

		eventClient.createSubtask(taskId, "Implement new QueryBuilder replacement")
		timeService.advanceTime(2, 15, 10)

		ideaFlowClient.startConflict(taskId, "This isn't going to work... Filter types overlap")
		timeService.advanceTime(0, 15, 0)
		ideaFlowClient.startLearning(taskId, "How can I make the overlapping filters work?")
		timeService.advanceTime(0, 42, 0)
		ideaFlowClient.startRework(taskId, "Creating a factory for assembling composite filters")
		timeService.advanceTime(1, 18, 0)

		//unnest this conflict, then link to next trial
		ideaFlowClient.startConflict(taskId, "Crap, this isn't going to work either.")
		timeService.advanceTime(0, 0, 10)
		ideaFlowClient.endRework(taskId, "")
		timeService.advanceTime(0, 5, 16)
		ideaFlowClient.startLearning(taskId, "How can I make the composite filters work?")
		timeService.advanceTime(0, 35, 4)
		ideaFlowClient.startRework(taskId, "Refactoring to use a CompositeFilterChain")
		timeService.advanceTime(1, 14, 34)

		//unnest this conflict, then link to next trial
		ideaFlowClient.startConflict(taskId, "The CompositeFilterChain won't support the LagFilter")
		timeService.advanceTime(0, 0, 10)
		ideaFlowClient.endRework(taskId, "")
		timeService.advanceTime(0, 15, 0)
		ideaFlowClient.startLearning(taskId, "How can I support the LagFilter?")
		timeService.advanceTime(0, 5, 24)
		ideaFlowClient.startRework(taskId, "LagFilter won't be a composite.  Refactoring")
		timeService.advanceTime(0, 32, 12)
		ideaFlowClient.endRework(taskId, "Ended up with a customized composite filter.")
		timeService.advanceTime(0, 50, 3)

		//final validation
		eventClient.createSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 0, 10)
		ideaFlowClient.startConflict(taskId, "Why isn't the dropdown populating?")
		timeService.advanceTime(0, 30, 43)
		ideaFlowClient.endConflict(taskId, "Bunch of little bugs.")
		timeService.advanceTime(0, 0, 10)
		activityClient.addEditorActivity(taskId, timeService.now(), 5l, "/some/path", true)

		when:
		BandTimeline bandTimeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		TimelineValidator validator = new TimelineValidator(bandTimeline)
		validator.assertIdeaFlowBand(PROGRESS, Duration.ofSeconds(15))
		validator.assertIdeaFlowBand(LEARNING, Duration.ofMinutes(45), "How does the existing QueryBuilder work?", "QueryBuilder is sliced by filter types, lots of duplication")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT2h29m10s"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT2h15m10s"), "Implement new QueryBuilder replacement")
		validator.assertLinkedBand(TROUBLESHOOTING, Duration.ofMinutes(15), "This isn't going to work... Filter types overlap", "How can I make the overlapping filters work?")
		validator.assertLinkedBand(LEARNING, Duration.ofMinutes(42), "How can I make the overlapping filters work?", "Creating a factory for assembling composite filters")
		validator.assertLinkedBand(REWORK, Duration.parse("PT1h18m"), "Creating a factory for assembling composite filters", "")
		validator.assertLinkedBand(TROUBLESHOOTING, Duration.parse("PT5m26s"), "Crap, this isn't going to work either.", "How can I make the composite filters work?")
		// TODO: there's 10 seconds of the rework band that disappears, is that expected?
		validator.assertLinkedBand(LEARNING, Duration.parse("PT35m4s"), "How can I make the composite filters work?", "Refactoring to use a CompositeFilterChain")
		validator.assertLinkedBand(REWORK, Duration.parse("PT1h14m34s"), "Refactoring to use a CompositeFilterChain", "")
		validator.assertLinkedBand(TROUBLESHOOTING, Duration.parse("PT15m10s"), "The CompositeFilterChain won't support the LagFilter", "How can I support the LagFilter?")
		// TODO: there's 10 seconds of the rework band that disappears, is that expected?
		validator.assertLinkedBand(LEARNING, Duration.parse("PT5m24s"), "How can I support the LagFilter?", "LagFilter won't be a composite.  Refactoring")
		validator.assertLinkedBand(REWORK, Duration.parse("PT32m12s"), "LagFilter won't be a composite.  Refactoring", "Ended up with a customized composite filter.")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT50m13s"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT10s"), "Final Validation")
		validator.assertIdeaFlowBand(TROUBLESHOOTING, Duration.parse("PT30m43s"), "Why isn't the dropdown populating?", "Bunch of little bugs.")
		validator.assertIdeaFlowBand(PROGRESS, Duration.ofSeconds(10))
		validator.assertValidationComplete()
	}

	def createLearningNestedConflictMap() {
		given:
		Task task = taskClient.createTask("learning", "create learning nested conflict map", "project")
		Long taskId = task.id

		timeService.advanceTime(0, 1, 30)
		ideaFlowClient.startLearning(taskId, "Where do I need to change the ReportingEngine code? #LackOfFamiliarity")
		timeService.advanceTime(1, 45, 0)
		ideaFlowClient.startConflict(taskId, "Why is the ReportingEngine sending notifications to Dispatch service?")
		timeService.advanceTime(0, 35, 0)
		ideaFlowClient.endConflict(taskId, "Dispatch emails the reports out to users.")
		timeService.advanceTime(1, 14, 2)
		ideaFlowClient.startConflict(taskId, "Why is the ReportingEngine dependent on ProdDB?  Should only be ReportingDB")
		timeService.advanceTime(0, 46, 30)
		ideaFlowClient.endConflict(taskId, "Looks like dependency was added for real-time dashboard. hmm.")
		timeService.advanceTime(1, 23, 30)
		ideaFlowClient.endLearning(taskId, "Need to update the NotificationTemplate")
		timeService.advanceTime(0, 15, 30)
		eventClient.createSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 32, 3)
		activityClient.addEditorActivity(taskId, timeService.now(), 10l, "/some/path", true)

		when:
		BandTimeline bandTimeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		TimelineValidator validator = new TimelineValidator(bandTimeline)
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT1m30s"))
		validator.assertIdeaFlowBand(LEARNING, Duration.parse("PT5h44m2s"), "Where do I need to change the ReportingEngine code? #LackOfFamiliarity", "Need to update the NotificationTemplate")
		validator.assertNestedTimeBand(TROUBLESHOOTING, Duration.ofMinutes(35), "Why is the ReportingEngine sending notifications to Dispatch service?", "Dispatch emails the reports out to users.")
		validator.assertNestedTimeBand(TROUBLESHOOTING, Duration.parse("PT46m30s"), "Why is the ReportingEngine dependent on ProdDB?  Should only be ReportingDB", "Looks like dependency was added for real-time dashboard. hmm.")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT47m33s"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT32m3s"), "Final Validation")
		validator.assertValidationComplete()
	}

	def createDetailedConflictMap() {
		given:
		Task task = taskClient.createTask("conflict", "create detailed conflict map", "project")
		Long taskId = task.id

		timeService.advanceTime(0, 5, 10)
		ideaFlowClient.startLearning(taskId, "What's the plan?")
		timeService.advanceTime(0, 20, 22)
		ideaFlowClient.endLearning(taskId, "Rework the ChartVisualizer to use TimeBand abstraction")
		timeService.advanceTime(0, 2, 10)
		eventClient.createSubtask(taskId, "Extract TimeBand class")
		timeService.advanceTime(0, 35, 5)
		eventClient.createSubtask(taskId, "Refactor ChartVisualizer to use new TimeBand")
		timeService.advanceTime(1, 15, 0)

		ideaFlowClient.startConflict(taskId, "Why isn't the RangeBuilder working anymore?")
		timeService.advanceTime(0, 14, 9)
		ideaFlowClient.startRework(taskId, "Range builder uses the duration details to calculate range.  Need to refactor.")
		timeService.advanceTime(0, 45, 14)
		ideaFlowClient.startConflict(taskId, "Why isn't the RangeBuilder working NOW?")
		timeService.advanceTime(0, 15, 43)
		ideaFlowClient.endConflict(taskId, "Flipped || with && in the duration code #TranspositionMistake")
		timeService.advanceTime(0, 1, 12)
		ideaFlowClient.startConflict(taskId, "Why is the duration 5?")
		timeService.advanceTime(0, 7, 26)
		ideaFlowClient.endConflict(taskId, "Missing a condition for excluding out of range values.")
		timeService.advanceTime(0, 5, 3)
		ideaFlowClient.endRework(taskId, "Okay, RangeBuilder is working again.")
		timeService.advanceTime(0, 3, 5)

		eventClient.createSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 0, 30)
		ideaFlowClient.startConflict(taskId, "Why isn't the chart showing up?")
		timeService.advanceTime(0, 15, 43)
		ideaFlowClient.endConflict(taskId, "Flipped if/else condition. #TranspositionMistake")
		timeService.advanceTime(0, 30, 3)
		activityClient.addEditorActivity(taskId, timeService.now(), 10l, "/some/path", true)

		when:
		BandTimeline bandTimeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		TimelineValidator validator = new TimelineValidator(bandTimeline)
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT5m10s"))
		validator.assertIdeaFlowBand(LEARNING, Duration.parse("PT20m22s"), "What's the plan?", "Rework the ChartVisualizer to use TimeBand abstraction")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT1h52m15s"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT35m5s"), "Extract TimeBand class")
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT1h15m"), "Refactor ChartVisualizer to use new TimeBand")
		validator.assertLinkedBand(TROUBLESHOOTING, Duration.parse("PT14m9s"), "Why isn't the RangeBuilder working anymore?", "Range builder uses the duration details to calculate range.  Need to refactor.")
		validator.assertLinkedBand(REWORK, Duration.parse("PT1h14m38s"), "Range builder uses the duration details to calculate range.  Need to refactor.", "Okay, RangeBuilder is working again.")
		validator.assertLinkedNestedTimeBand(TROUBLESHOOTING, Duration.parse("PT15m43s"), "Why isn't the RangeBuilder working NOW?", "Flipped || with && in the duration code #TranspositionMistake")
		validator.assertLinkedNestedTimeBand(TROUBLESHOOTING, Duration.parse("PT7m26s"), "Why is the duration 5?", "Missing a condition for excluding out of range values.")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT3m35s"))
		validator.assertSegmentStartAndProgressNode(Duration.parse("PT30s"), "Final Validation")
		validator.assertIdeaFlowBand(TROUBLESHOOTING, Duration.parse("PT15m43s"), "Why isn't the chart showing up?", "Flipped if/else condition. #TranspositionMistake")
		validator.assertIdeaFlowBand(PROGRESS, Duration.parse("PT30m3s"))
		validator.assertValidationComplete()
	}

}
