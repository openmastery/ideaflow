package org.openmastery.storyweb.core

import org.joda.time.Duration
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.storyweb.api.SPCChart
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification


import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class SPCSeriesGeneratorSpec extends Specification {

	@Autowired
	SPCChartGenerator spcSeriesGenerator

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

		saveIdeaFlow()

		when:
		List<SPCChartGenerator.TaskData> taskDataList =
				spcSeriesGenerator.generateTaskData(-1, builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

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

		saveIdeaFlow()

		when:
		SPCChart chart = spcSeriesGenerator.generateChart(-1, builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert chart.graphPoints.size() == 1
		assert chart.graphPoints.get(0).durationInSeconds == new DurationInSeconds(66 * 60)
		assert chart.graphPoints.get(0).totalFirstDegree == 1
		assert chart.graphPoints.get(0).totalSecondDegree == 3
		assert chart.graphPoints.get(0).totalThirdDegree == 5

	}

	void saveIdeaFlow() {
		builder.eventList.each { Event event ->
			EventEntity entity = entityMapper.mapIfNotNull(event, EventEntity)
			entity.taskId = taskId
			entity.ownerId = -1
			persistenceService.saveEvent(entity)
		}

		builder.idleTimeBands.each { IdleTimeBandModel idle ->
			IdleActivityEntity entity = entityMapper.mapIfNotNull(idle, IdleActivityEntity)
			entity.taskId = taskId
			entity.ownerId = -1
			persistenceService.saveActivity(entity)
		}

		builder.executionEventList.each { ExecutionEvent executionEvent ->
			ExecutionActivityEntity entity = entityMapper.mapIfNotNull(executionEvent, ExecutionActivityEntity)
			entity.taskId = taskId
			entity.ownerId = -1
			persistenceService.saveActivity(entity)
		}
	}
}
