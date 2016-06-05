package org.ideaflow.publisher.core.stub;

import org.ideaflow.publisher.core.ideaflow.IdeaFlowPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;

@Component
@ConditionalOnMissingClass("org.ideaflow.publisher.ComponentTest")
public class FixtureTimelineInitializer {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	@PostConstruct
	private void init() {
		TestDataSupport support = new TestDataSupport(persistenceService);
		support.createBasicTimelineWithAllBandTypes();
		support.createDetailedConflictMap();
		support.createLearningNestedConflictMap();
		support.createTrialAndErrorMap();
	}

}
