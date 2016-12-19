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

	def "should create learning band if no modification made"() {
		given:
		builder.activate()
				.advanceHours(1)
				.deactivate()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		ideaFlowBands[0].type == IdeaFlowStateType.LEARNING
		ideaFlowBands[0].start == startTime
		ideaFlowBands[0].end == startTime.plusMinutes(60)

		and:
		assert ideaFlowBands.size() == 1
	}

	def "should create learning band when not modifying and progress band when modifying"() {
		given:
		builder.activate()
				.readCodeAndAdvance(60)
				.modifyCodeAndAdvance(30)
				.readCodeAndAdvance(60)
				.deactivate()

		when:
		List<IdeaFlowBandModel> ideaFlowBands = generateIdeaFlowBands()

		then:
		ideaFlowBands[0].type == IdeaFlowStateType.LEARNING
		ideaFlowBands[0].start == startTime
		ideaFlowBands[0].end == startTime.plusMinutes(58)

		and:
		ideaFlowBands[1].type == IdeaFlowStateType.PROGRESS
		ideaFlowBands[1].start == ideaFlowBands[0].end
		ideaFlowBands[1].end == ideaFlowBands[0].end.plusMinutes(29)

		and:
		ideaFlowBands[2].type == IdeaFlowStateType.LEARNING
		ideaFlowBands[2].start == ideaFlowBands[1].end
		ideaFlowBands[2].end == startTime.plusHours(2).plusMinutes(30)

		and:
		ideaFlowBands.size() == 3
	}

}