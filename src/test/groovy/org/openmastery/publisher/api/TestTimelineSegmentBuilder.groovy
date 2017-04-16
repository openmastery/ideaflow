package org.openmastery.publisher.api

import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.core.timeline.TimeBandGroupModel
import org.openmastery.publisher.core.timeline.BandTimelineSegment
import org.openmastery.time.MockTimeService
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.timeline.IdleTimeProcessor

import static IdeaFlowStateType.TROUBLESHOOTING

class TestTimelineSegmentBuilder {

	private IdeaFlowBandModel activeIdeaFlowBand = null
	private TimeBandGroupModel activeTimeBandGroup = null
	private List<IdeaFlowBandModel> ideaFlowBands = []
	private List<TimeBandGroupModel> timeBandGroups = []
	private List<IdleActivityEntity> idleActivities = []
	private MockTimeService timeService

	TestTimelineSegmentBuilder() {
		this(new MockTimeService())
	}

	TestTimelineSegmentBuilder(MockTimeService timeService) {
		this.timeService = timeService
	}

	BandTimelineSegment build() {
		BandTimelineSegment segment = BandTimelineSegment.builder()
				.events([])
				.activities([])
				.ideaFlowBands(ideaFlowBands)
				.timeBandGroups(timeBandGroups)
				.build()
		new IdleTimeProcessor().collapseIdleTime(segment, idleActivities)

		activeIdeaFlowBand = null
		activeTimeBandGroup = null
		ideaFlowBands = []
		idleActivities = []
		segment
	}

	private IdeaFlowBandModel createIdeaFlowBand(IdeaFlowStateType type, int startHour, int endHour) {
		IdeaFlowBandModel.builder()
				.type(type)
				.start(timeService.hoursInFuture(startHour))
				.end(timeService.hoursInFuture(endHour))
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
		IdeaFlowBandModel nestedBand = createIdeaFlowBand(TROUBLESHOOTING, startHour, endHour)
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
		idleActivities << IdleActivityEntity.builder()
				.start(timeService.hoursInFuture(startHour))
				.end(timeService.hoursInFuture(endHour))
				.build()
		this
	}

}