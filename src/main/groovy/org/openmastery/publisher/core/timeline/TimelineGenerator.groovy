package org.openmastery.publisher.core.timeline

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.publisher.api.timeline.BandTimeline
import org.openmastery.publisher.api.timeline.TreeTimeline
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowPartialStateEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowStateEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDateTime

@Component
class TimelineGenerator {

	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BandTimelineSegmentFactory segmentFactory = new BandTimelineSegmentFactory()
	private IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
	private BandTimelineSplitter timelineSplitter = new BandTimelineSplitter()
	private RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()

	public BandTimeline createBandTimelineForTask(long taskId) {
		BandTimelineSegment segment = createBandTimelineSegmentForTask(taskId)
		EntityMapper mapper = new EntityMapper()
		mapper.mapIfNotNull(segment, BandTimeline.class)
	}

	private BandTimelineSegment createBandTimelineSegmentForTask(long taskId) {
		BandTimelineSegment segment = createTimelineSegmentAndCollapseIdleTime(taskId)
		List<BandTimelineSegment> segments = [segment]
		relativeTimeProcessor.computeRelativeTime(segments)
		segments[0]
	}

	public TreeTimeline createTreeTimelineForTask(long taskId) {
		List<BandTimelineSegment> segments = createAndSplitBandTimelineSegmentForTask(taskId)
		new TreeTimelineBuilder()
				.addTimelineSegments(segments)
				.build()
	}

	private List<BandTimelineSegment> createAndSplitBandTimelineSegmentForTask(long taskId) {
		BandTimelineSegment segment = createTimelineSegmentAndCollapseIdleTime(taskId)
		List<BandTimelineSegment> segments = timelineSplitter.splitTimelineSegment(segment)
		relativeTimeProcessor.computeRelativeTime(segments)
		segments
	}

	private BandTimelineSegment createTimelineSegmentAndCollapseIdleTime(Long taskId) {
		TaskEntity task = persistenceService.findTaskWithId(taskId)
		if (task == null) {
			throw new NotFoundException("No task with id=" + taskId);
		}

		List<IdeaFlowStateEntity> ideaFlowStates = getStateListWithActiveCompleted(taskId)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(taskId)
		List<EventEntity> eventList = persistenceService.getEventList(taskId)

		BandTimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates, eventList)
		segment.setDescription(task.description)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		segment
	}

	private List<IdeaFlowStateEntity> getStateListWithActiveCompleted(long taskId) {
		List<IdeaFlowStateEntity> stateList = new ArrayList(persistenceService.getStateList(taskId))
		IdeaFlowPartialStateEntity activeState = persistenceService.getActiveState(taskId)
		if (activeState != null) {
			LocalDateTime stateEndTime = persistenceService.getMostRecentActivityEnd(taskId)
			addCompleteStateIfDurationNotZero(stateList, taskId, activeState, stateEndTime)
			IdeaFlowPartialStateEntity containingState = persistenceService.getContainingState(taskId)
			if (containingState != null) {
				addCompleteStateIfDurationNotZero(stateList, taskId, containingState, stateEndTime)
			}
		}
		stateList
	}

	private void addCompleteStateIfDurationNotZero(List<IdeaFlowStateEntity> stateList, long taskId, IdeaFlowPartialStateEntity state, LocalDateTime endTime) {
		if (endTime != null && endTime != state.start) {
			IdeaFlowStateEntity ideaFlowState = IdeaFlowStateEntity.from(state)
					.taskId(taskId)
					.end(endTime)
					.build();
			stateList.add(ideaFlowState)
		}
	}

}
