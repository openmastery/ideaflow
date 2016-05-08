package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.core.activity.IdleTimeBandEntity
import org.ideaflow.publisher.core.event.EventEntity

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private long id = 1L;
	private IdeaFlowStateEntity activeState
	private IdeaFlowStateEntity containingState
	private List<IdeaFlowStateEntity> stateList = []
	private List<IdleTimeBandEntity> idleTimeBandList = []
	private List<EventEntity> eventList = []

	@Override
	public IdeaFlowStateEntity getActiveState(long taskId) {
		activeState
	}

	@Override
	public IdeaFlowStateEntity getContainingState(long taskId) {
		containingState
	}

	@Override
	public List<IdeaFlowStateEntity> getStateList(long taskId) {
		stateList
	}

	@Override
	public List<IdleTimeBandEntity> getIdleTimeBandList(long taskId) {
		idleTimeBandList
	}

	@Override
	public List<EventEntity> getEventList(long taskId) {
		eventList
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState) {
		saveActiveState(activeState, null)
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState) {
		this.activeState = activeState
		this.containingState = containingState
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState) {
		stateToSave.id = id++
		stateList.add(stateToSave)
		saveActiveState(activeState)
	}

	@Override
	public void saveIdleActivity(IdleTimeBandEntity idleActivity) {
		idleActivity.id = id++
		idleTimeBandList.add(idleActivity)
	}

	@Override
	public void saveEvent(EventEntity event) {
		event.id = id++
		eventList.add(event)
	}

}
