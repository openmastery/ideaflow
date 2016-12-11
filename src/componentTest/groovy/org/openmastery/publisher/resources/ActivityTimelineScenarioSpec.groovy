package org.openmastery.publisher.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.client.*
import org.openmastery.publisher.core.timeline.ActivityTimelineValidator
import org.openmastery.publisher.core.timeline.TimelinePrettyPrinter
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.TROUBLESHOOTING
import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING

@Ignore
@ComponentTest
class ActivityTimelineScenarioSpec  extends Specification {

	@Autowired
	private MockTimeService timeService
	@Autowired
	private TaskClient taskClient
	@Autowired
	private IdeaFlowClient ideaFlowClient
	@Autowired
	private EventClient eventClient
	@Autowired
	private BatchClient batchClient
	@Autowired
	private TimelineClient timelineClient
	private LocalDateTime start

	def setup() {
		start = timeService.now()
	}


	def "activity timeline SHOULD break up timeband start and end AND sort with activity"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with file activity", "project")
		Long taskId = task.id

		addEditorActivityAndAdvanceTime(taskId, 15, "first.txt", false)
		ideaFlowClient.startLearning(taskId, "How does this code work?")

		addEditorActivityAndAdvanceTime(taskId, 10, "second.txt", false)

		ideaFlowClient.startConflict(taskId, "What is this dependency?")
		addEditorActivityAndAdvanceTime(taskId, 20, "third.txt", false)
		ideaFlowClient.endConflict(taskId, "Red Herring.")
		addEditorActivityAndAdvanceTime(taskId, 5, "fourth.txt", false)
		ideaFlowClient.endLearning(taskId, null)

		addEditorActivityAndAdvanceTime(taskId, 50, "fifth.txt", true)
		addEditorActivityAndAdvanceTime(taskId, 15, "sixth.txt", true)

		when:
		ActivityTimeline activityTimeline = timelineClient.getActivityTimelineForTask(taskId)

		then:
		TimelinePrettyPrinter.printTimeline(activityTimeline)

		ActivityTimelineValidator validator = new ActivityTimelineValidator(activityTimeline)
		validator.assertFileActivity(0, "first.txt", false, Duration.ofSeconds(15))
		validator.assertBandStart(15, LEARNING, "How does this code work?")
		validator.assertFileActivity(15, "second.txt", false, Duration.ofSeconds(10))
		validator.assertBandStart(25, TROUBLESHOOTING, "What is this dependency?")
		validator.assertFileActivity(25, "third.txt", false, Duration.ofSeconds(20))
		validator.assertBandEnd(45, TROUBLESHOOTING, "Red Herring.")
		validator.assertFileActivity(45, "fourth.txt", false, Duration.ofSeconds(5))
		validator.assertBandEnd(50, LEARNING, null)
		validator.assertFileActivity(50, "fifth.txt", true, Duration.ofSeconds(50))
		validator.assertFileActivity(100, "sixth.txt", true, Duration.ofSeconds(15))
		validator.assertValidationComplete()
	}

	def "activity timeline SHOULD extract idle activity AND sort with file activity"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with idles", "project")
		Long taskId = task.id

		ideaFlowClient.startLearning(taskId, "How does this code work?")

		//idle inside a learning band
		addEditorActivityAndAdvanceTime(taskId, 5, "first.txt", false)
		addIdleActivityAndAdvanceTime(taskId, 30)
		addEditorActivityAndAdvanceTime(taskId, 20, "second.txt", false)

		ideaFlowClient.endLearning(taskId, null)

		//idle inside a progress band
		addEditorActivityAndAdvanceTime(taskId, 10, "third.txt", true)
		addIdleActivityAndAdvanceTime(taskId, 30)
		addEditorActivityAndAdvanceTime(taskId, 15, "fourth.txt", true)

		when:
		ActivityTimeline activityTimeline = timelineClient.getActivityTimelineForTask(taskId)

		then:
		TimelinePrettyPrinter.printTimeline(activityTimeline)

		ActivityTimelineValidator validator = new ActivityTimelineValidator(activityTimeline)
		validator.assertBandStart(0, LEARNING, "How does this code work?")
		validator.assertFileActivity(0, "first.txt", false, Duration.ofSeconds(5))
		validator.assertIdleActivity(5, Duration.ofSeconds(30))
		validator.assertFileActivity(5, "second.txt", false, Duration.ofSeconds(20))
		validator.assertBandEnd(25, LEARNING, null)
		validator.assertFileActivity(25, "third.txt", true, Duration.ofSeconds(10))
		validator.assertIdleActivity(35, Duration.ofSeconds(30))
		validator.assertFileActivity(35, "fourth.txt", true, Duration.ofSeconds(15))
		validator.assertValidationComplete()
	}

	def "activity timeline SHOULD treat non-idle exteral activity as positive time like file activity"() {

		Task task = taskClient.createTask("basic", "create basic timeline with external activity", "project")
		Long taskId = task.id

		addEditorActivityAndAdvanceTime(taskId, 15, "first.txt", false)

		ideaFlowClient.startConflict(taskId, "Why is the screen blank?")

		addEditorActivityAndAdvanceTime(taskId, 10, "second.txt", false)
		addExternalActivityAndAdvanceTime(taskId, 15, "browser-stuff")
		addEditorActivityAndAdvanceTime(taskId, 20, "third.txt", false)
		addExternalActivityAndAdvanceTime(taskId, 30, "browser-stuff")

		ideaFlowClient.endConflict(taskId, "I forgot to redeploy the code.")

		when:
		ActivityTimeline activityTimeline = timelineClient.getActivityTimelineForTask(taskId)

		then:
		TimelinePrettyPrinter.printTimeline(activityTimeline)

		ActivityTimelineValidator validator = new ActivityTimelineValidator(activityTimeline)
		validator.assertFileActivity(0, "first.txt", false, Duration.ofSeconds(15))
		validator.assertBandStart(15, TROUBLESHOOTING, "Why is the screen blank?")
		validator.assertFileActivity(15, "second.txt", false, Duration.ofSeconds(10))
		validator.assertExternalActivity(25, "browser-stuff", Duration.ofSeconds(15))
		validator.assertFileActivity(40, "third.txt", false, Duration.ofSeconds(20))
		validator.assertExternalActivity(60, "browser-stuff", Duration.ofSeconds(30))
		validator.assertBandEnd(90, TROUBLESHOOTING, "I forgot to redeploy the code.")

		validator.assertValidationComplete()
	}

	def "activity timeline SHOULD sort events into position when events overlap with the file activity"() {

		Task task = taskClient.createTask("basic", "create basic timeline with events", "project")
		Long taskId = task.id

		addEditorActivityAndAdvanceTime(taskId, 15, "first.txt", false)

		ideaFlowClient.startLearning(taskId, "How does this work?")

		//event in the middle of a file activity
		timeService.advanceTime(0, 0, 5)
		eventClient.createNote(taskId, "Exploring the email engine...")
		timeService.advanceTime(0, 0, 10)
		batchClient.addEditorActivity(taskId, timeService.jodaNow(), 15, "second.txt", false)

		eventClient.createSubtask(taskId, "my subtask")
		addEditorActivityAndAdvanceTime(taskId, 10, "third.txt", false)
		ideaFlowClient.endLearning(taskId, null)

		when:
		ActivityTimeline activityTimeline = timelineClient.getActivityTimelineForTask(taskId)

		then:
		TimelinePrettyPrinter.printTimeline(activityTimeline)

		ActivityTimelineValidator validator = new ActivityTimelineValidator(activityTimeline)
		validator.assertFileActivity(0, "first.txt", false, Duration.ofSeconds(15))
		validator.assertBandStart(15, LEARNING,  "How does this work?")
		validator.assertFileActivity(15, "second.txt", false, Duration.ofSeconds(15))
		validator.assertEvent(20, "Exploring the email engine...")
		validator.assertEvent(30, "my subtask")
		validator.assertFileActivity(30, "third.txt", false, Duration.ofSeconds(10))

		validator.assertBandEnd(40, LEARNING, null)

		validator.assertValidationComplete()
	}


	void addEditorActivityAndAdvanceTime (Long taskId, Long durationInSeconds, String fileName, boolean isModified) {
		timeService.advanceTime(0, 0, durationInSeconds.toInteger())
		// editor activity is submitted at the end time rather than the start time
		batchClient.addEditorActivity(taskId, timeService.jodaNow(), durationInSeconds, fileName, isModified)
	}

	void addIdleActivityAndAdvanceTime(Long taskId, Long durationInSeconds) {
		timeService.advanceTime(0, 0, durationInSeconds.toInteger())
		batchClient.addIdleActivity(taskId, timeService.jodaNow(), durationInSeconds)
	}

	void addExternalActivityAndAdvanceTime(Long taskId, Long durationInSeconds, String comment) {
		timeService.advanceTime(0, 0, durationInSeconds.toInteger())
		batchClient.addExternalActivity(taskId, timeService.jodaNow(), durationInSeconds, comment)
	}

}
