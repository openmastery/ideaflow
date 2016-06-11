package org.openmastery.publisher.core.stub;

import org.openmastery.publisher.core.IdeaFlowPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnMissingClass("org.openmastery.publisher.ComponentTest")
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
