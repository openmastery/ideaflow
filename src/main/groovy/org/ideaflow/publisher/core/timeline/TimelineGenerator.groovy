package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.timeline.Timeline
import org.ideaflow.publisher.api.timeline.TimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
public class TimelineGenerator {

	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private TimelineSegmentFactory segmentFactory = new TimelineSegmentFactory()
	private IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
	private TimelineSplitter timelineSplitter = new TimelineSplitter()
	private RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()

	public Timeline createTaskTimeline(long taskId) {
		List<IdeaFlowStateEntity> ideaFlowStates = getStateListWithActiveCompleted(taskId)
		List<IdleTimeBandEntity> idleActivities = persistenceService.getIdleTimeBandList(taskId)
		List<EventEntity> eventList = persistenceService.getEventList(taskId)

		createTimeline(ideaFlowStates, idleActivities, eventList)
	}

	private List<IdeaFlowStateEntity> getStateListWithActiveCompleted(long taskId) {
		List<IdeaFlowStateEntity> stateList = new ArrayList(persistenceService.getStateList(taskId))
		IdeaFlowStateEntity activeState = persistenceService.getActiveState(taskId)
		if (activeState != null) {
			stateList.add(completeState(taskId, activeState))
			IdeaFlowStateEntity containingState = persistenceService.getContainingState(taskId)
			if (containingState != null) {
				stateList.add(completeState(taskId, containingState))
			}
		}
		stateList
	}

	private IdeaFlowStateEntity completeState(long taskId, IdeaFlowStateEntity state) {
		return IdeaFlowStateEntity.from(state)
				.taskId(taskId)
				.end(state.getStart())
				.endingComment("")
				.build();
	}

	private Timeline createTimeline(List<IdeaFlowStateEntity> ideaFlowStates, List<IdleTimeBandEntity> idleActivities,
	                               List<EventEntity> eventList) {
		TimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates, eventList)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		List<TimelineSegment> segments = timelineSplitter.splitTimelineSegment(segment)
		Timeline timeline = Timeline.builder()
				.timelineSegments(segments)
				.build()
		relativeTimeProcessor.setRelativeTime(timeline)
		timeline
	}

}
