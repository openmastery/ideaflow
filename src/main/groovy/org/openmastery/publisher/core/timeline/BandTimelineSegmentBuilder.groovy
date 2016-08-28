package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.core.Positionable
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityModel
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityModel
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity

class BandTimelineSegmentBuilder {

	private String description
	private List<IdeaFlowStateEntity> ideaFlowStates
	private List<EventEntity> events
	private List<IdleActivityEntity> idleActivities
	private List<EditorActivityEntity> editorActivities
	private List<ExternalActivityEntity> externalActivities

	BandTimelineSegmentBuilder(List<IdeaFlowStateEntity> ideaFlowStates) {
		this.ideaFlowStates = ideaFlowStates
	}

	BandTimelineSegmentBuilder description(String description) {
		this.description = description
		this
	}

	BandTimelineSegmentBuilder collapseIdleTime(List<IdleActivityEntity> idleActivities) {
		this.idleActivities = idleActivities
		this
	}

	BandTimelineSegmentBuilder events(List<EventEntity> events) {
		this.events = events
		this
	}

	BandTimelineSegmentBuilder editorActivities(List<EditorActivityEntity> editorActivities) {
		this.editorActivities = editorActivities
		this
	}

	BandTimelineSegmentBuilder externalActivities(List<ExternalActivityEntity> externalActivities) {
		this.externalActivities = externalActivities
		this
	}

	BandTimelineSegment build() {
		BandTimelineSegment segment = createTimelineSegmentAndCollapseIdleTime()
		computeRelativeTime(segment.getAllContentsFlattenedAsPositionableList())
		segment
	}

	List<BandTimelineSegment> buildAndSplit() {
		BandTimelineSegment segment = createTimelineSegmentAndCollapseIdleTime()
		BandTimelineSplitter timelineSplitter = new BandTimelineSplitter()
		List<BandTimelineSegment> segments = timelineSplitter.splitTimelineSegment(segment)
		computeRelativeTime(getAllContentsFlattenedAsPositionableList(segments))
		segments
	}

	private void computeRelativeTime(List<Positionable> positionables) {
		RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()
		relativeTimeProcessor.computeRelativeTime(positionables)
	}

	private List<Positionable> getAllContentsFlattenedAsPositionableList(List<BandTimelineSegment> segments) {
		List<Positionable> positionables = []
		for (BandTimelineSegment segment : segments) {
			positionables.addAll(segment.getAllContentsFlattenedAsPositionableList())
		}
		positionables
	}

	private BandTimelineSegment createTimelineSegmentAndCollapseIdleTime() {
		BandTimelineSegment segment = createTimelineSegment()
		if (idleActivities) {
			IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
			idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		}
		segment
	}


	// TODO: refactor... AAHHHHH!!!!!

	private BandTimelineSegment createTimelineSegment() {
		ideaFlowStates = ideaFlowStates.sort(false)

		IdeaFlowBandModel previousBand = null
		TimeBandGroupModel activeTimeBandGroup = null
		List<IdeaFlowBandModel> ideaFlowBands = []
		List<TimeBandGroupModel> ideaFlowBandGroups = []
		for (IdeaFlowStateEntity state : ideaFlowStates) {
			IdeaFlowBandModel timeBand = toIdeaFlowBandModel(state)

			if (state.isNested()) {
				previousBand.addNestedBand(timeBand)
			} else {
				if (state.isLinkedToPrevious() && (ideaFlowBands.isEmpty() == false)) {
					if (activeTimeBandGroup == null) {
						IdeaFlowBandModel firstBandInGroup = ideaFlowBands.remove(ideaFlowBands.size() - 1)
						activeTimeBandGroup = TimeBandGroupModel.builder()
								.id("group-${firstBandInGroup.id}")
								.linkedTimeBands([])
								.build()

						activeTimeBandGroup.addLinkedTimeBand(firstBandInGroup)
						ideaFlowBandGroups.add(activeTimeBandGroup)
					}

					activeTimeBandGroup.addLinkedTimeBand(timeBand)
				} else {
					activeTimeBandGroup = null
					ideaFlowBands.add(timeBand)
				}

				if (previousBand != null) {
					if (previousBand.end.isAfter(timeBand.start)) {
						previousBand.end = timeBand.start
					}
				}

				previousBand = timeBand
			}
		}

		List<EventModel> eventModels = toEventModelList(events)
		List<EditorActivityModel> editorActivityModels = toEditorActivityList(editorActivities)
		List<ExternalActivityModel> externalActivityModels = toExternalActivityList(externalActivities)

		BandTimelineSegment segment = BandTimelineSegment.builder()
				.description(description)
				.ideaFlowBands(ideaFlowBands)
				.timeBandGroups(ideaFlowBandGroups)
				.events(eventModels)
				.activities(editorActivityModels + externalActivityModels)
				.build()

		return segment;
	}

	private IdeaFlowBandModel toIdeaFlowBandModel(IdeaFlowStateEntity state) {
		IdeaFlowBandModel.builder()
				.id(state.id)
				.taskId(state.taskId)
				.type(state.type)
				.start(state.start)
				.end(state.end)
				.startingComment(state.startingComment)
				.endingComent(state.endingComment)
				.idleBands([])
				.nestedBands([])
				.build()
	}

	private List<EventModel> toEventModelList(List<EventEntity> eventEntityList) {
		if (eventEntityList == null) {
			return []
		}

		eventEntityList.collect { EventEntity eventEntity ->
			new EventModel(eventEntity)
		}
	}

	private List<EditorActivityModel> toEditorActivityList(List<EditorActivityEntity> editorActivityList) {
		if (editorActivityList == null) {
			return []
		}

		editorActivityList.collect { EditorActivityEntity editorActivityEntity ->
			new EditorActivityModel(editorActivityEntity)
		}
	}

	private List<ExternalActivityModel> toExternalActivityList(List<ExternalActivityEntity> externalActivityList) {
		if (externalActivityList == null) {
			return []
		}

		externalActivityList.collect { ExternalActivityEntity externalActivityEntity ->
			new ExternalActivityModel(externalActivityEntity)
		}
	}

}
