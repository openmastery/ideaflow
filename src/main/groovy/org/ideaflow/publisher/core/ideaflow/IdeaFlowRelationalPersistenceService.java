package org.ideaflow.publisher.core.ideaflow;

import org.ideaflow.publisher.core.activity.IdleTimeBandEntity;
import org.ideaflow.publisher.core.activity.IdleTimeBandRepository;
import org.ideaflow.publisher.core.event.EventEntity;
import org.ideaflow.publisher.core.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdeaFlowRelationalPersistenceService implements IdeaFlowPersistenceService {

	private static final long ACTIVE_STATE_ID = -1;
	private static final long CONTAINING_STATE_ID = -1;

	@Autowired
	private IdeaFlowStateRepository ideaFlowStateRepository;
	@Autowired
	private IdleTimeBandRepository idleTimeBandRepository;
	@Autowired
	private EventRepository eventRepository;

	@Override
	public IdeaFlowStateEntity getActiveState() {
		return null;
	}

	@Override
	public IdeaFlowStateEntity getContainingState() {
		return null;
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState) {
		saveActiveState(activeState, null);
	}

	@Override
	public void saveActiveState(IdeaFlowStateEntity activeState, IdeaFlowStateEntity containingState) {
//		ideaFlowStateRepository.save()
		// TODO:
	}

	@Override
	public void saveTransition(IdeaFlowStateEntity stateToSave, IdeaFlowStateEntity activeState) {
		ideaFlowStateRepository.save(stateToSave);
		saveActiveState(activeState);
	}

	@Override
	public void saveIdleActivity(IdleTimeBandEntity idleActivity) {
		idleTimeBandRepository.save(idleActivity);
	}

	@Override
	public void saveEvent(EventEntity event) {
		eventRepository.save(event);
	}

}
