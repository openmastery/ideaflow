package org.openmastery.publisher.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.client.*
import org.openmastery.publisher.core.timeline.ActivityTimelineValidator
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

		addEditorActivityAndAdvanceTime(taskId, 15, "file1.txt", false)
		ideaFlowClient.startLearning(taskId, "How does this code work?")

		addEditorActivityAndAdvanceTime(taskId, 10, "file1.txt", false)
		addEditorActivityAndAdvanceTime(taskId, 20, "file2.txt", false)

		ideaFlowClient.endLearning(taskId, null)

		addEditorActivityAndAdvanceTime(taskId, 50, "file1.txt", true)
		addEditorActivityAndAdvanceTime(taskId, 15, "file2.txt", true)

		when:
		ActivityTimeline activityTimeline = timelineClient.getActivityTimelineForTask(taskId)

		then:
		ActivityTimelineValidator validator = new ActivityTimelineValidator(activityTimeline)
		validator.assertFileActivity(0, "file1.txt", false, Duration.ofSeconds(15))
		validator.assertBandStart(15, LEARNING, "How does this code work?")
		validator.assertFileActivity(15, "file1.txt", false, Duration.ofSeconds(10))
		validator.assertFileActivity(25, "file2.txt", false, Duration.ofSeconds(20))
		validator.assertBandEnd(45, LEARNING, null)
		validator.assertFileActivity(45, "file1.txt", true, Duration.ofSeconds(50))
		validator.assertFileActivity(95, "file2.txt", true, Duration.ofSeconds(15))
		validator.assertValidationComplete()
	}

	void addEditorActivityAndAdvanceTime (Long taskId, Long durationInSeconds, String fileName, boolean isModified) {
		timeService.advanceTime(0, 0, durationInSeconds.toInteger())
		// editor activity is submitted at the end time rather than the start time
		activityClient.addEditorActivity(taskId, durationInSeconds, fileName, isModified)
	}

}
