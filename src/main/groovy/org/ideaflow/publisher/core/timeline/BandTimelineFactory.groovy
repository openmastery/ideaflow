package org.ideaflow.publisher.core.timeline

import com.bancvue.rest.exception.NotFoundException
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

	public BandTimelineSegment createBandTimelineSegmentForTask(long taskId) {
		BandTimelineSegment segment = createTimelineSegment(taskId)
		List<BandTimelineSegment> segments = [segment]
		relativeTimeProcessor.computeRelativeTime(segments)
		segments[0]
	}

	public List<BandTimelineSegment> createAndSplitBandTimelineSegmentForTask(long taskId) {
		BandTimelineSegment segment = createTimelineSegment(taskId)
		List<BandTimelineSegment> segments = timelineSplitter.splitTimelineSegment(segment)
		relativeTimeProcessor.computeRelativeTime(segments)
		segments
	}

	private BandTimelineSegment createTimelineSegment(TaskEntity task, List<IdeaFlowStateEntity> ideaFlowStates,
	                                                  List<IdleTimeBandEntity> idleActivities, List<EventEntity> eventList) {
		BandTimelineSegment segment = segmentFactory.createTimelineSegment(ideaFlowStates, eventList)
		segment.setDescription(task.description)
		idleTimeProcessor.collapseIdleTime(segment, idleActivities)
		segment
	}

	private BandTimelineSegment createTimelineSegment(Long taskId) {
		TaskEntity task = persistenceService.findTaskWithId(taskId)
		if (task == null) {
			throw new NotFoundException("No task with id=" + taskId);
		}

		List<IdeaFlowStateEntity> ideaFlowStates = getStateListWithActiveCompleted(taskId)
		List<IdleTimeBandEntity> idleActivities = persistenceService.getIdleTimeBandList(taskId)
		List<EventEntity> eventList = persistenceService.getEventList(taskId)

		createTimelineSegment(task, ideaFlowStates, idleActivities, eventList)
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
					.build();
			stateList.add(ideaFlowState)
		}
	}

}
