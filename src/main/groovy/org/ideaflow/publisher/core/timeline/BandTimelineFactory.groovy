package org.ideaflow.publisher.core.timeline

import com.bancvue.rest.exception.NotFoundException
import org.ideaflow.publisher.api.timeline.BandTimeline
import org.ideaflow.publisher.api.timeline.BandTimelineSegment
import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity
import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService
import org.ideaflow.publisher.core.ideaflow.IdeaFlowStateEntity
import org.ideaflow.publisher.core.task.TaskEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDateTime

@Component
public class BandTimelineFactory {

	@Autowired
	private IdeaFlowPersistenceService persistenceService
	private BandTimelineSegmentFactory segmentFactory = new BandTimelineSegmentFactory()
	private IdleTimeProcessor idleTimeProcessor = new IdleTimeProcessor()
	private BandTimelineSplitter timelineSplitter = new BandTimelineSplitter()
	private RelativeTimeProcessor relativeTimeProcessor = new RelativeTimeProcessor()

	public void disableTimelineSplitter() {
		timelineSplitter = null
	}

	public BandTimeline createTaskTimeline(long taskId) {
		TaskEntity task = persistenceService.findTaskWithId(taskId)
		if (task == null) {
			throw new NotFoundException("No task with id=" + taskId);
		}

		List<IdeaFlowStateEntity> ideaFlowStates = getStateListWithActiveCompleted(taskId)
		List<IdleTimeBandEntity> idleActivities = persistenceService.getIdleTimeBandList(taskId)
		List<EventEntity> eventList = persistenceService.getEventList(taskId)

		createTimeline(task, ideaFlowStates, idleActivities, eventList)
	}

	private List<IdeaFlowStateEntity> getStateListWithActiveCompleted(long taskId) {
		List<IdeaFlowStateEntity> stateList = new ArrayList(persistenceService.getStateList(taskId))
		IdeaFlowStateEntity activeState = persistenceService.getActiveState(taskId)
		if (activeState != null) {
			LocalDateTime stateEndTime = persistenceService.getMostRecentActivityEnd(taskId)
			addCompleteStateIfDurationNotZero(stateList, taskId, activeState, stateEndTime)
			IdeaFlowStateEntity containingState = persistenceService.getContainingState(taskId)
			if (containingState != null) {
				addCompleteStateIfDurationNotZero(stateList, taskId, containingState, stateEndTime)
			}
		}
		stateList
	}

	private void addCompleteStateIfDurationNotZero(List<IdeaFlowStateEntity> stateList, long taskId, IdeaFlowStateEntity state, LocalDateTime endTime) {
		if (endTime != null && endTime != state.start) {
			IdeaFlowStateEntity ideaFlowState = IdeaFlowStateEntity.from(state)
					.taskId(taskId)
					.end(endTime)
					.endingComment("")
					.build();
			stateList.add(ideaFlowState)
		}
	}

	private BandTimeline createTimeline(TaskEntity task, List<IdeaFlowStateEntity> ideaFlowStates,
	                                    List<IdleTimeBandEntity> idleActivities, List<EventEntity> eventList) {
		BandTimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates, eventList)
		segment.setDescription(task.description)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		List<BandTimelineSegment> segments
		if (timelineSplitter != null) {
			segments = timelineSplitter.splitTimelineSegment(segment)
		} else {
			segments = [segment]
		}
		BandTimeline timeline = BandTimeline.builder()
				.timelineSegments(segments)
				.build()
		relativeTimeProcessor.setRelativeTime(timeline)
		timeline
	}

}
