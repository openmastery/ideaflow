package org.openmastery.storyweb.resources

import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.user.UserEntity
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.storyweb.api.metrics.SPCChart
import org.openmastery.storyweb.client.MetricsClient
import org.openmastery.storyweb.core.FixturePersistenceHelper
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class MetricsResourceSpec extends Specification {

	@Autowired
	private UserEntity testUser;

	@Autowired
	private MetricsClient metricsClient

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Autowired
	private FixturePersistenceHelper fixturePersistenceHelper

	MockTimeService mockTimeService = new MockTimeService()
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	Long taskId
	Long userId

	def setup() {
		userId = testUser.id
		taskId = persistenceService.saveTask(aRandom.taskEntity().ownerId(userId).build()).id

	}
	def "generateSPCChart SHOULD populate a chart with graph points"() {
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

		fixturePersistenceHelper.saveIdeaFlow(userId, taskId, builder)

		when:
		SPCChart chart = metricsClient.generateChart(builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert chart != null
		assert chart.graphPoints.size()
		assert chart.painThresholds.size() == 5
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

		fixturePersistenceHelper.saveIdeaFlow(userId, taskId, builder)

		when:
		SPCChart chart = metricsClient.generateChart(builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert chart.graphPoints.size() == 5
		assert chart.meta.totalFirstDegree == 5
		assert chart.meta.totalSecondDegree == 4
		assert chart.meta.totalThirdDegree == 3
		assert chart.meta.totalFourthDegree == 6

	}
}
