package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.core.activity.IdleActivityEntity;
import org.ideaflow.publisher.core.event.EventEntity;

import java.util.ArrayList;
import java.util.List;

public class IdeaFlowInMemoryPersistenceService implements IdeaFlowPersistenceService {

	private IdeaFlowStateEntity activeState;
	private IdeaFlowStateEntity containingState;
	private List<IdeaFlowStateEntity> stateList = new ArrayList<>();
	private List<IdleActivityEntity> idleActivityList = new ArrayList<>();
	private List<EventEntity> eventList = new ArrayList<>();

	@Override
	public IdeaFlowStateEntity getActiveState() {
		return activeState;
	}

	@Override
	public IdeaFlowStateEntity getContainingState() {
		return containingState;
	}

	public List<IdeaFlowStateEntity> getStateList() {
		return stateList;
	}

	public List<IdleActivityEntity> getIdleActivityList() {
		return idleActivityList;
	}

	public List<EventEntity> getEventList() {
		return eventList;
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState) {
		this.activeState = activeState;
		this.containingState = containingState;
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState) {
		stateList.add(stateToSave);
		saveActiveState(activeState);
	}

	@Override
	public void saveIdleActivity(IdleActivityEntity idleActivity) {
		idleActivityList.add(idleActivity);
	}

	@Override
	public void saveEvent(EventEntity event) {
		eventList.add(event);
	}

}
