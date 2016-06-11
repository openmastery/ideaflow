package org.openmastery.publisher.core.ideaflow;

import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.openmastery.time.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdeaFlowStateMachineFactory {

	@Autowired
	private TimeService timeService;
	@Autowired
	private IdeaFlowPersistenceService ideaFlowPersistenceService;

	public IdeaFlowStateMachine createStateMachine(Long taskId) {
		return new IdeaFlowStateMachine(taskId, timeService, ideaFlowPersistenceService);
	}

}
