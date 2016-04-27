package org.ideaflow.publisher.core.ideaflow

import org.ideaflow.publisher.core.activity.IdleTimeBand
import org.ideaflow.publisher.core.event.EventEntity

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private IdeaFlowStateEntity activeState
	private IdeaFlowStateEntity containingState
	private List<IdeaFlowStateEntity> stateList = []
	private List<IdleTimeBand> idleActivityList = []
	private List<EventEntity> eventList = []

	@Override
	public IdeaFlowStateEntity getActiveState() {
		activeState
	}

	@Override
	public IdeaFlowStateEntity getContainingState() {
		containingState
	}

	public List<IdeaFlowStateEntity> getStateList() {
		stateList
	}

	public List<IdleTimeBand> getIdleActivityList() {
		idleActivityList
	}

	public List<EventEntity> getEventList() {
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
		stateList.add(stateToSave)
		saveActiveState(activeState)
	}

	@Override
	public void saveIdleActivity(IdleTimeBand idleActivity) {
		idleActivityList.add(idleActivity)
	}

	@Override
	public void saveEvent(EventEntity event) {
		eventList.add(event)
	}

}
