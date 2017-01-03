package org.openmastery.storyweb.core

import org.joda.time.Duration
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.storyweb.api.SPCChart
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification


import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class SPCChartGeneratorSpec extends Specification {

	@Autowired
	SPCChartGenerator spcChartGenerator

	@Autowired
	FixturePersistenceHelper fixturePersistenceHelper

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	EntityMapper entityMapper = new EntityMapper()

	MockTimeService mockTimeService = new MockTimeService()
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	Long taskId

	def setup() {
		taskId = persistenceService.saveTask(aRandom.taskEntity().build()).id

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
		List<SPCChartGenerator.TaskData> taskDataList =
				spcChartGenerator.generateTaskData(-1, builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert taskDataList.size() == 1
		assert taskDataList.get(0).troubleshootingBands.size() == 1
		assert taskDataList.get(0).troubleshootingBands.get(0).duration == Duration.standardMinutes(65)
	}

	def "generateSPCChart SHOULD generate graph points for each task"() {
		given:
		builder.activate()
				.wtf()
				.advanceMinutes(30)
				.executeCode()
				.executeCode()
				.executeCode()
				.advanceMinutes(1)
				.wtf()
				.advanceMinutes(30)
				.executeCode()
				.executeCode()
				.idleDays(1)
				.advanceMinutes(5)
				.awesome()
				.advanceMinutes(5)
				.deactivate()

		fixturePersistenceHelper.saveIdeaFlow(-1, taskId, builder)

		when:
		SPCChart chart = spcChartGenerator.generateChart(-1, builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert chart.graphPoints.size() == 1
		assert chart.graphPoints.get(0).durationInSeconds == new DurationInSeconds(66 * 60)
		assert chart.graphPoints.get(0).totalFirstDegree == 1
		assert chart.graphPoints.get(0).totalSecondDegree == 3
		assert chart.graphPoints.get(0).totalThirdDegree == 5

	}


}
