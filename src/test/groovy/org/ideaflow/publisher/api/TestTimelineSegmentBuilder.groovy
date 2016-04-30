package org.ideaflow.publisher.api

import org.ideaflow.publisher.core.MockTimeService
import org.ideaflow.publisher.core.activity.IdleTimeBand
import org.ideaflow.publisher.core.timeline.IdleTimeProcessor

import static org.ideaflow.publisher.api.IdeaFlowStateType.CONFLICT

class TestTimelineSegmentBuilder {

	private IdeaFlowBand activeIdeaFlowBand = null
	private TimeBandGroup activeTimeBandGroup = null
	private List<IdeaFlowBand> ideaFlowBands = []
	private List<TimeBandGroup> timeBandGroups = []
	private List<IdleTimeBand> idleTimeBands = []
	private MockTimeService timeService

	TestTimelineSegmentBuilder() {
		this(new MockTimeService())
	}

	TestTimelineSegmentBuilder(MockTimeService timeService) {
		this.timeService = timeService
	}

	TimelineSegment build() {
		TimelineSegment segment = TimelineSegment.builder()
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

	private IdeaFlowBand createIdeaFlowBand(IdeaFlowStateType type, int startHour, int endHour) {
		IdeaFlowBand.builder()
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
		IdeaFlowBand nestedBand = createIdeaFlowBand(CONFLICT, startHour, endHour)
		activeIdeaFlowBand.addNestedBand(nestedBand)
		this
	}

	TestTimelineSegmentBuilder linkedIdeaFlowBand(IdeaFlowStateType type, int startHour, int endHour) {
		if (activeTimeBandGroup == null) {
			activeTimeBandGroup = TimeBandGroup.builder()
					.linkedTimeBands([])
					.build()
			timeBandGroups << activeTimeBandGroup
		}
		IdeaFlowBand linkedBand = createIdeaFlowBand(type, startHour, endHour)
		activeTimeBandGroup.addLinkedTimeBand(linkedBand)
		activeIdeaFlowBand = linkedBand
		this
	}

	TestTimelineSegmentBuilder idle(int startHour, int endHour) {
		idleTimeBands << IdleTimeBand.builder()
				.start(timeService.inFuture(startHour))
				.end(timeService.inFuture(endHour))
				.build()
		this
	}

}