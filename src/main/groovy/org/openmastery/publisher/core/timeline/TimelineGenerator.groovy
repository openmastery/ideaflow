package org.openmastery.publisher.core.timeline

import com.bancvue.rest.exception.NotFoundException
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.api.timeline.BandTimeline
import org.openmastery.publisher.api.timeline.TreeTimeline
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
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

	public BandTimeline createBandTimelineForTask(long taskId) {
		BandTimelineSegment segment = createBandTimelineSegmentBuilder(taskId).build()
		EntityMapper mapper = new EntityMapper()
		mapper.mapIfNotNull(segment, BandTimeline.class)
	}

	public TreeTimeline createTreeTimelineForTask(long taskId) {
		List<BandTimelineSegment> segments = createBandTimelineSegmentBuilder(taskId).buildAndSplit()
		new TreeTimelineBuilder()
				.addTimelineSegments(segments)
				.build()
	}

	public ActivityTimeline createActivityTimelineForTask(long taskId) {
		BandTimelineSegment segment = createBandTimelineSegmentBuilder(taskId).build()
		new ActivityTimelineBuilder()
				.addTimelineSegment(segment)
				.build()
	}

	private BandTimelineSegmentBuilder createBandTimelineSegmentBuilder(Long taskId) {
		TaskEntity task = persistenceService.findTaskWithId(taskId)
		if (task == null) {
			throw new NotFoundException("No task with id=" + taskId);
		}

		List<IdeaFlowStateEntity> ideaFlowStates = getStateListWithActiveCompleted(taskId)
		List<EventEntity> eventList = persistenceService.getEventList(taskId)
		List<EditorActivityEntity> editorActivityList = persistenceService.getEditorActivityList(taskId)
		List<ExternalActivityEntity> externalActivityList = persistenceService.getExternalActivityList(taskId)
		List<IdleActivityEntity> idleActivities = persistenceService.getIdleActivityList(taskId)
		new BandTimelineSegmentBuilder(ideaFlowStates)
				.description(task.description)
				.events(eventList)
				.editorActivities(editorActivityList)
				.externalActivities(externalActivityList)
				.collapseIdleTime(idleActivities)
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
