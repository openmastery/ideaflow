package org.openmastery.publisher.api

import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.core.timeline.TimeBandGroupModel
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.time.MockTimeService
import org.openmastery.publisher.core.activity.IdleTimeBandEntity
import org.openmastery.publisher.core.timeline.IdleTimeProcessor

import static IdeaFlowStateType.CONFLICT

class TestTimelineSegmentBuilder {

	private IdeaFlowBandModel activeIdeaFlowBand = null
	private TimeBandGroupModel activeTimeBandGroup = null
	private List<IdeaFlowBandModel> ideaFlowBands = []
	private List<TimeBandGroupModel> timeBandGroups = []
	private List<IdleTimeBandEntity> idleTimeBands = []
	private MockTimeService timeService

	TestTimelineSegmentBuilder() {
		this(new MockTimeService())
	}

	TestTimelineSegmentBuilder(MockTimeService timeService) {
		this.timeService = timeService
	}

	BandTimelineSegment build() {
		BandTimelineSegment segment = BandTimelineSegment.builder()
				.ideaFlowBands(ideaFlowBands)
				.timeBandGroups(timeBandGroups)
				.build()
		new IdleTimeProcessor().collapseIdleTime(segment, idleTimeBands)

		activeIdeaFlowBand = null
		activeTimeBandGroup = null
		ideaFlowBands = []
		idleTimeBands = []
		segment
	}

	private IdeaFlowBandModel createIdeaFlowBand(IdeaFlowStateType type, int startHour, int endHour) {
		IdeaFlowBandModel.builder()
				.type(type)
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.idleBands([])
				.nestedBands([])
				.build()
	}

	TestTimelineSegmentBuilder ideaFlowBand(IdeaFlowStateType type, int startHour, int endHour) {
		activeTimeBandGroup = null
		activeIdeaFlowBand = createIdeaFlowBand(type, startHour, endHour)
		ideaFlowBands << activeIdeaFlowBand
		this
	}

	TestTimelineSegmentBuilder nestedConflict(int startHour, int endHour) {
		IdeaFlowBandModel nestedBand = createIdeaFlowBand(CONFLICT, startHour, endHour)
		activeIdeaFlowBand.addNestedBand(nestedBand)
		this
	}

	TestTimelineSegmentBuilder linkedIdeaFlowBand(IdeaFlowStateType type, int startHour, int endHour) {
		if (activeTimeBandGroup == null) {
			activeTimeBandGroup = TimeBandGroupModel.builder()
					.linkedTimeBands([])
					.build()
			timeBandGroups << activeTimeBandGroup
		}
		IdeaFlowBandModel linkedBand = createIdeaFlowBand(type, startHour, endHour)
		activeTimeBandGroup.addLinkedTimeBand(linkedBand)
		activeIdeaFlowBand = linkedBand
		this
	}

	TestTimelineSegmentBuilder idle(int startHour, int endHour) {
		idleTimeBands << IdleTimeBandEntity.builder()
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.build()
		this
	}

}