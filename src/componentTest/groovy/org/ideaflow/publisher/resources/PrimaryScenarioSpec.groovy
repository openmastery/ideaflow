package org.ideaflow.publisher.resources

import org.openmastery.time.MockTimeService
import org.ideaflow.publisher.ComponentTest
import org.ideaflow.publisher.api.event.EventType
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand
import org.ideaflow.publisher.api.task.Task
import org.ideaflow.publisher.api.timeline.BandTimeline
import org.ideaflow.publisher.core.timeline.BandTimelineSegment
import org.ideaflow.publisher.client.ActivityClient
import org.ideaflow.publisher.client.EventClient
import org.ideaflow.publisher.client.IdeaFlowClient
import org.ideaflow.publisher.client.TaskClient
import org.ideaflow.publisher.client.TimelineClient
import org.ideaflow.publisher.core.timeline.TimelineSegmentValidator
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.CONFLICT
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.LEARNING
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS
import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.REWORK

// TODO: this test needs to be updated to account for the change in how timelines work - specifically,
// band timeline is no longer segmented
@Ignore
@ComponentTest
class PrimaryScenarioSpec extends Specification {

	@Autowired
	private MockTimeService timeService
	@Autowired
	private TaskClient taskClient
	@Autowired
	private IdeaFlowClient ideaFlowClient
	@Autowired
	private EventClient eventClient
	@Autowired
	private ActivityClient activityClient
	@Autowired
	private TimelineClient timelineClient
	private TimelineSegmentValidator validator = new TimelineSegmentValidator()
	private LocalDateTime start

	def setup() {
		start = timeService.now()
	}

	def createBasicTimelineWithAllBandTypes() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with all band types")
		Long taskId = task.id

		timeService.advanceTime(0, 0, 15)
		ideaFlowClient.startLearning(taskId, "How should I break down this task?")
		timeService.advanceTime(0, 45, 0)
		ideaFlowClient.endLearning(taskId, "Starting with the RangeBuilder class")
		timeService.advanceTime(0, 1, 0)

		eventClient.startSubtask(taskId, "Write up test cases for RangeBuilder")
		timeService.advanceTime(1, 13, 0)
		ideaFlowClient.startRework(taskId, "Refactoring RangeBuilder")
		timeService.advanceTime(0, 40, 12)
		ideaFlowClient.endRework(taskId, "")
		timeService.advanceTime(1, 15, 30)

		eventClient.startSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 15, 10)
		ideaFlowClient.startConflict(taskId, "Why is the chart throwing a NPE?")
		timeService.advanceTime(0, 10, 43)
		ideaFlowClient.endConflict(taskId, "chartData wasn't initialized.")
		timeService.advanceTime(0, 05, 45)
		ideaFlowClient.startConflict(taskId, "Why is the chart not displaying?")
		timeService.advanceTime(0, 10, 43)
		ideaFlowClient.endConflict(taskId, "jqPlot parameters were wrong.  Passing in [] instead of [[]]")
		timeService.advanceTime(0, 5, 4)
		activityClient.addEditorActivity(taskId, "/some/path", true, Duration.ofSeconds(10))

		when:
		BandTimeline timeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		List<BandTimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofSeconds(15))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofMinutes(45))
		validator.assertTimeBand(segments[0].ideaFlowBands, 2, PROGRESS, Duration.ofMinutes(1))
		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusMinutes(46).plusSeconds(15))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1).plusMinutes(13))
		validator.assertTimeBand(segments[1].ideaFlowBands, 1, REWORK, Duration.ofMinutes(40).plusSeconds(12))
		validator.assertTimeBand(segments[1].ideaFlowBands, 2, PROGRESS, Duration.ofHours(1).plusMinutes(15).plusSeconds(30))
		validator.assertEvent(segments[2], 0, EventType.SUBTASK, start.plusHours(3).plusMinutes(54).plusSeconds(57))
		validator.assertTimeBand(segments[2].ideaFlowBands, 0, PROGRESS, Duration.ofMinutes(15).plusSeconds(10))
		validator.assertTimeBand(segments[2].ideaFlowBands, 1, CONFLICT, Duration.ofMinutes(10).plusSeconds(43))
		validator.assertTimeBand(segments[2].ideaFlowBands, 2, PROGRESS, Duration.ofMinutes(5).plusSeconds(45))
		validator.assertTimeBand(segments[2].ideaFlowBands, 3, CONFLICT, Duration.ofMinutes(10).plusSeconds(43))
		validator.assertTimeBand(segments[2].ideaFlowBands, 4, PROGRESS, Duration.ofMinutes(5).plusSeconds(4))
		validator.assertValidationComplete(segments, 3)
	}

	def createTrialAndErrorMap() {
		given:
		Task task = taskClient.createTask("trial", "create trial and error map")
		Long taskId = task.id

		timeService.advanceTime(0, 0, 15)
		ideaFlowClient.startLearning(taskId, "How does the existing QueryBuilder work?")
		timeService.advanceTime(0, 45, 0)
		ideaFlowClient.endLearning(taskId, "QueryBuilder is sliced by filter types, lots of duplication")
		timeService.advanceTime(0, 14, 0)

		eventClient.startSubtask(taskId, "Implement new QueryBuilder replacement")
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
		eventClient.startSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 0, 10)
		ideaFlowClient.startConflict(taskId, "Why isn't the dropdown populating?")
		timeService.advanceTime(0, 30, 43)
		ideaFlowClient.endConflict(taskId, "Bunch of little bugs.")
		timeService.advanceTime(0, 0, 10)
		activityClient.addEditorActivity(taskId, "/some/path", true, Duration.ofSeconds(5))

		when:
		BandTimeline timeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		List<BandTimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofSeconds(15))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofMinutes(45))
		validator.assertTimeBand(segments[0].ideaFlowBands, 2, PROGRESS, Duration.ofMinutes(14))

		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusMinutes(59).plusSeconds(15))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, PROGRESS, Duration.ofHours(2).plusMinutes(15).plusSeconds(10))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 0, CONFLICT, Duration.ofMinutes(15))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 1, LEARNING, Duration.ofMinutes(42))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 2, REWORK, Duration.ofHours(1).plusMinutes(18))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 3, CONFLICT, Duration.ofMinutes(5).plusSeconds(26))
		// TODO: there's 10 seconds of the rework band that disappears, is that expected?
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 4, LEARNING, Duration.ofMinutes(35).plusSeconds(4))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 5, REWORK, Duration.ofHours(1).plusMinutes(14).plusSeconds(34))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 6, CONFLICT, Duration.ofMinutes(15).plusSeconds(10))
		// TODO: there's 10 seconds of the rework band that disappears, is that expected?
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 7, LEARNING, Duration.ofMinutes(5).plusSeconds(24))
		validator.assertLinkedTimeBand(segments[1].timeBandGroups[0].linkedTimeBands, 8, REWORK, Duration.ofMinutes(32).plusSeconds(12))
		validator.assertTimeBand(segments[1].ideaFlowBands, 1, PROGRESS, Duration.ofMinutes(50).plusSeconds(3))

		validator.assertEvent(segments[2], 0, EventType.SUBTASK, start.plusHours(9).plusMinutes(7).plusSeconds(18))
		validator.assertTimeBand(segments[2].ideaFlowBands, 0, PROGRESS, Duration.ofSeconds(10))
		validator.assertTimeBand(segments[2].ideaFlowBands, 1, CONFLICT, Duration.ofMinutes(30).plusSeconds(43))
		validator.assertTimeBand(segments[2].ideaFlowBands, 2, PROGRESS, Duration.ofSeconds(10))

		validator.assertValidationComplete(segments, 3)
	}

	def createLearningNestedConflictMap() {
		given:
		Task task = taskClient.createTask("learning", "create learning nested conflict map")
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
		eventClient.startSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 32, 3)
		activityClient.addEditorActivity(taskId, "/some/path", true, Duration.ofSeconds(10))

		when:
		BandTimeline timeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		List<BandTimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofMinutes(1).plusSeconds(30))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofHours(5).plusMinutes(44).plusSeconds(2))
		validator.assertNestedTimeBand(segments[0].ideaFlowBands[1].nestedBands, 0, CONFLICT, Duration.ofMinutes(35))
		validator.assertNestedTimeBand(segments[0].ideaFlowBands[1].nestedBands, 1, CONFLICT, Duration.ofMinutes(46).plusSeconds(30))
		validator.assertTimeBand(segments[0].ideaFlowBands, 2, PROGRESS, Duration.ofMinutes(15).plusSeconds(30))

		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusHours(6).plusMinutes(1).plusSeconds(2))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, PROGRESS, Duration.ofMinutes(32).plusSeconds(3))

		validator.assertValidationComplete(segments, 2)
	}

	def createDetailedConflictMap() {
		given:
		Task task = taskClient.createTask("conflict", "create detailed conflict map")
		Long taskId = task.id

		timeService.advanceTime(0, 5, 10)
		ideaFlowClient.startLearning(taskId, "What's the plan?")
		timeService.advanceTime(0, 20, 22)
		ideaFlowClient.endLearning(taskId, "Rework the ChartVisualizer to use TimeBand abstraction")
		timeService.advanceTime(0, 2, 10)
		eventClient.startSubtask(taskId, "Extract TimeBand class")
		timeService.advanceTime(0, 35, 5)
		eventClient.startSubtask(taskId, "Refactor ChartVisualizer to use new TimeBand")
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

		eventClient.startSubtask(taskId, "Final Validation")
		timeService.advanceTime(0, 0, 30)
		ideaFlowClient.startConflict(taskId, "Why isn't the chart showing up?")
		timeService.advanceTime(0, 15, 43)
		ideaFlowClient.endConflict(taskId, "Flipped if/else condition. #TranspositionMistake")
		timeService.advanceTime(0, 30, 3)
		activityClient.addEditorActivity(taskId, "/some/path", true, Duration.ofSeconds(10))

		when:
		BandTimeline timeline = timelineClient.getBandTimelineForTask(taskId)

		then:
		List<BandTimelineSegment> segments = timeline.timelineSegments
		validator.assertTimeBand(segments[0].ideaFlowBands, 0, PROGRESS, Duration.ofMinutes(5).plusSeconds(10))
		validator.assertTimeBand(segments[0].ideaFlowBands, 1, LEARNING, Duration.ofMinutes(20).plusSeconds(22))
		validator.assertTimeBand(segments[0].ideaFlowBands, 2, PROGRESS, Duration.ofMinutes(2).plusSeconds(10))

		validator.assertEvent(segments[1], 0, EventType.SUBTASK, start.plusMinutes(27).plusSeconds(42))
		validator.assertTimeBand(segments[1].ideaFlowBands, 0, PROGRESS, Duration.ofMinutes(35).plusSeconds(5))

		validator.assertEvent(segments[2], 0, EventType.SUBTASK, start.plusHours(1).plusMinutes(2).plusSeconds(47))
		validator.assertTimeBand(segments[2].ideaFlowBands, 0, PROGRESS, Duration.ofHours(1).plusMinutes(15))
		List<IdeaFlowBand> linkedTimeBands = segments[2].timeBandGroups[0].linkedTimeBands
		validator.assertLinkedTimeBand(linkedTimeBands, 0, CONFLICT, Duration.ofMinutes(14).plusSeconds(9))
		validator.assertLinkedTimeBand(linkedTimeBands, 1, REWORK, Duration.ofHours(1).plusMinutes(14).plusSeconds(38))
		validator.assertNestedTimeBand(linkedTimeBands[1].nestedBands, 0, CONFLICT, Duration.ofMinutes(15).plusSeconds(43))
		validator.assertNestedTimeBand(linkedTimeBands[1].nestedBands, 1, CONFLICT, Duration.ofMinutes(7).plusSeconds(26))
		validator.assertTimeBand(segments[2].ideaFlowBands, 1, PROGRESS, Duration.ofMinutes(3).plusSeconds(5))

		validator.assertEvent(segments[3], 0, EventType.SUBTASK, start.plusHours(3).plusMinutes(49).plusSeconds(39))
		validator.assertTimeBand(segments[3].ideaFlowBands, 0, PROGRESS, Duration.ofSeconds(30))
		validator.assertTimeBand(segments[3].ideaFlowBands, 1, CONFLICT, Duration.ofMinutes(15).plusSeconds(43))
		validator.assertTimeBand(segments[3].ideaFlowBands, 2, PROGRESS, Duration.ofMinutes(30).plusSeconds(3))

		validator.assertValidationComplete(segments, 4)
	}

}
