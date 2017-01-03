package org.openmastery.storyweb.resources

import org.joda.time.Duration
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.ComponentTest
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.user.UserEntity
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.storyweb.api.GlossaryDefinition
import org.openmastery.storyweb.api.SPCChart
import org.openmastery.storyweb.client.SPCClient
import org.openmastery.storyweb.core.SPCChartGenerator
import org.openmastery.storyweb.core.FixturePersistenceHelper
import org.openmastery.time.MockTimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

import static org.openmastery.publisher.ARandom.aRandom

@ComponentTest
class SpcResourceSpec extends Specification {

	@Autowired
	private UserEntity testUser;

	@Autowired
	private SPCClient spcClient

	@Autowired
	private IdeaFlowPersistenceService persistenceService

	@Autowired
	private FixturePersistenceHelper fixturePersistenceHelper

	MockTimeService mockTimeService = new MockTimeService()
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)

	Long taskId
	Long userId

	def setup() {
		taskId = persistenceService.saveTask(aRandom.taskEntity().build()).id
		userId = testUser.id
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
		SPCChart chart = spcClient.generateChart(builder.startTime.toLocalDate(), builder.deactivationTime.toLocalDate())

		then:
		assert chart != null
		assert chart.graphPoints.size() == 1
		assert chart.metricThresholds.size() == 5
	}
}
