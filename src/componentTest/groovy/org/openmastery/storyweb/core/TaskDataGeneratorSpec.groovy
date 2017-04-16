package org.openmastery.storyweb.core

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.storyweb.core.metrics.spc.TaskData
import org.openmastery.storyweb.core.metrics.spc.TaskDataGenerator
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import java.time.Duration

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class TaskDataGeneratorSpec extends Specification {

	@Autowired
	TaskDataGenerator taskDataGenerator

	@Autowired
	FixturePersistenceHelper fixturePersistenceHelper

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	MockTimeService mockTimeService = new MockTimeService()
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	Long taskId

	def setup() {
		taskId = persistenceService.saveTask(aRandom.taskEntity().ownerId(-1).build()).id

	}

	def "generateTaskData SHOULD generate bands with collapsed idle"() {
		given:
		builder.activate()
				.wtf()
				.advanceMinutes(30)
				.wtf()
				.advanceMinutes(30)
				.idleDays(1)
				.advanceMinutes(5)
				.awesome()
				.advanceMinutes(5)
				.deactivate()

		fixturePersistenceHelper.saveIdeaFlow(-1, taskId, builder)

		when:
		List<TaskData> taskDataList =
				taskDataGenerator.generate(-1, builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert taskDataList.size() == 1
		assert taskDataList.get(0).troubleshootingBands.size() == 1
		assert taskDataList.get(0).troubleshootingBands.get(0).duration == Duration.ofMinutes(65)
		assert taskDataList.get(0).task != null
	}


	def "generateTaskData SHOULD not explode when no data"() {
		given:

		when:
		List<TaskData> taskDataList =
				taskDataGenerator.generate(-1, mockTimeService.now().toLocalDate(), mockTimeService.daysInFuture(7).toLocalDate())

		then:
		assert taskDataList.size() == 0
	}
}
