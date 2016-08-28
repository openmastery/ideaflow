package org.openmastery.publisher.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.client.*
import org.openmastery.publisher.core.timeline.ActivityTimelineValidator
import org.openmastery.publisher.core.timeline.TimelinePrettyPrinter
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.Duration
import java.time.LocalDateTime

import static org.openmastery.publisher.api.ideaflow.IdeaFlowStateType.LEARNING

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
	private ActivityClient activityClient
	@Autowired
	private TimelineClient timelineClient
	private LocalDateTime start

	def setup() {
		start = timeService.now()
	}

	def "activity timeline SHOULD break up timeband start and end AND sort with activity"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with file activity")
		Long taskId = task.id

		addEditorActivityAndAdvanceTime(taskId, 15, "first.txt", false)
		ideaFlowClient.startLearning(taskId, "How does this code work?")

		addEditorActivityAndAdvanceTime(taskId, 10, "second.txt", false)
		addEditorActivityAndAdvanceTime(taskId, 20, "third.txt", false)

		ideaFlowClient.endLearning(taskId, null)

		addEditorActivityAndAdvanceTime(taskId, 50, "fourth.txt", true)
		addEditorActivityAndAdvanceTime(taskId, 15, "fifth.txt", true)

		when:
		ActivityTimeline activityTimeline = timelineClient.getActivityTimelineForTask(taskId)

		then:
		TimelinePrettyPrinter.printTimeline(activityTimeline)

		ActivityTimelineValidator validator = new ActivityTimelineValidator(activityTimeline)
		validator.assertFileActivity(0, "first.txt", false, Duration.ofSeconds(15))
		validator.assertBandStart(15, LEARNING, "How does this code work?")
		validator.assertFileActivity(15, "second.txt", false, Duration.ofSeconds(10))
		validator.assertFileActivity(25, "third.txt", false, Duration.ofSeconds(20))
		validator.assertBandEnd(45, LEARNING, null)
		validator.assertFileActivity(45, "fourth.txt", true, Duration.ofSeconds(50))
		validator.assertFileActivity(95, "fifth.txt", true, Duration.ofSeconds(15))
		validator.assertValidationComplete()
	}

	def "activity timeline SHOULD extract idle activity AND sort with file activity"() {
		given:
		Task task = taskClient.createTask("basic", "create basic timeline with file activity")
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



	void addEditorActivityAndAdvanceTime (Long taskId, Long durationInSeconds, String fileName, boolean isModified) {
		timeService.advanceTime(0, 0, durationInSeconds.toInteger())
		// editor activity is submitted at the end time rather than the start time
		activityClient.addEditorActivity(taskId, durationInSeconds, fileName, isModified)
	}

	void addIdleActivityAndAdvanceTime(Long taskId, Long durationInSeconds) {
		timeService.advanceTime(0, 0, durationInSeconds.toInteger())
		activityClient.addIdleActivity(taskId, 30)
	}

}
