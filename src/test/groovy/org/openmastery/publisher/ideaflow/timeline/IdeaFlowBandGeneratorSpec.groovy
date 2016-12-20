package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.time.MockTimeService;
import spock.lang.Specification;

public class IdeaFlowBandGeneratorSpec extends Specification {

	MockTimeService mockTimeService = new MockTimeService()
	LocalDateTime startTime = mockTimeService.now()
	IdeaFlowTimelineElementBuilder builder = new IdeaFlowTimelineElementBuilder(mockTimeService)
	IdeaFlowBandGenerator generator = new IdeaFlowBandGenerator()

	private List<IdeaFlowBandModel> generateIdeaFlowBands() {
		List positionableList = builder.eventList + builder.modificationActivityList + builder.idleTimeBands
		generator.generateIdeaFlowBands(positionableList)
	}

	private void assertBand(IdeaFlowBandModel band, IdeaFlowStateType expectedType, LocalDateTime expectedStartTime, LocalDateTime expectedEndTime) {
		assert band.type == expectedType
		assert band.start == expectedStartTime
		assert band.end == expectedEndTime
	}

	private void assertProgressBand(IdeaFlowBandModel band, LocalDateTime expectedStartTime, LocalDateTime expectedEndType) {
		assertBand(band, IdeaFlowStateType.PROGRESS, expectedStartTime, expectedEndType)
	}

	private void assertStrategyBand(IdeaFlowBandModel band, LocalDateTime expectedStartTime, LocalDateTime expectedEndType) {
		assertBand(band, IdeaFlowStateType.LEARNING, expectedStartTime, expectedEndType)
	}

	private void assertTroubleshootingBand(IdeaFlowBandModel band, LocalDateTime expectedStartTime, LocalDateTime expectedEndType) {
		assertBand(band, IdeaFlowStateType.TROUBLESHOOTING, expectedStartTime, expectedEndType)
	}

	def "should create strategy band if no modification made"() {
		given:
		builder.activate()
				.advanceHours(1)
				.deactivate()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertStrategyBand(ideaFlowBands[0], startTime, startTime.plusMinutes(60))

		and:
		assert ideaFlowBands.size() == 1
	}

	def "should create strategy band when not modifying and progress band when modifying"() {
		given:
		builder.activate()
				.readCodeAndAdvance(60)
				.modifyCodeAndAdvance(30)
				.readCodeAndAdvance(60)
				.deactivate()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertStrategyBand(ideaFlowBands[0], startTime, startTime.plusMinutes(58))
		assertProgressBand(ideaFlowBands[1], ideaFlowBands[0].end, ideaFlowBands[0].end.plusMinutes(29))
		assertStrategyBand(ideaFlowBands[2], ideaFlowBands[1].end, startTime.plusHours(2).plusMinutes(30))
		ideaFlowBands.size() == 3
	}

	def "should create troubleshooting band when wtf event followed by awesome"() {
		given:
		builder.activate()
				.wtf()
				.modifyCodeAndAdvance(30)
				.awesome()
				.deactivate()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertTroubleshootingBand(ideaFlowBands[0], startTime, startTime.plusMinutes(30))
		assert ideaFlowBands.size() == 1
	}

	def "should create troubleshooting band at first wtf when multiple wtf events followed by awesome"() {
		given:
		builder.wtf()
				.advanceMinutes(30)
				.wtf()
				.advanceMinutes(30)
				.awesome()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertTroubleshootingBand(ideaFlowBands[0], startTime, startTime.plusMinutes(60))
		assert ideaFlowBands.size() == 1
	}

	def "should stop strategy band at start of troubleshooting if troubleshooting starts during but ends after strategy"() {
		given:
		builder.readCodeAndAdvance(30)
				.wtf()
				.readCodeAndAdvance(30)
				.modifyCodeAndAdvance(30)
				.awesome()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertStrategyBand(ideaFlowBands[0], startTime, startTime.plusMinutes(28))
		assertTroubleshootingBand(ideaFlowBands[1], ideaFlowBands[0].end, startTime.plusMinutes(90))
		assert ideaFlowBands.size() == 2
	}

	def "should nest troubleshooting within strategy if troubleshooting starts and ends within strategy band"() {
		given:
		builder.readCodeAndAdvance(30)
				.wtf()
				.readCodeAndAdvance(30)
				.awesome()
				.readCodeAndAdvance(30)

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertStrategyBand(ideaFlowBands[0], startTime, startTime.plusMinutes(90))
		assertTroubleshootingBand(ideaFlowBands[0].nestedBands[0], startTime.plusMinutes(30), startTime.plusMinutes(60))
		assert ideaFlowBands.nestedBands.size() == 1
		assert ideaFlowBands.size() == 1
	}

	def "should create progress bands to fill band gaps"() {
		given:
		builder.activate()
				.modifyCodeAndAdvance(30)
				.wtf()
				.modifyCodeAndAdvance(30)
				.awesome()
				.modifyCodeAndAdvance(30)
				.deactivate()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		assertProgressBand(ideaFlowBands[0], startTime, startTime.plusMinutes(30))
		assertTroubleshootingBand(ideaFlowBands[1], ideaFlowBands[0].end, startTime.plusMinutes(60))
		assertProgressBand(ideaFlowBands[2], ideaFlowBands[1].end, startTime.plusMinutes(90))
		assert ideaFlowBands.size() == 3
	}

}