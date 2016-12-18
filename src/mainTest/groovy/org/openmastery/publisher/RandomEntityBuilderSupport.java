package org.openmastery.publisher;

import org.openmastery.publisher.core.activity.*;
import org.openmastery.publisher.core.event.RandomEventEntityBuilder;
import org.openmastery.publisher.ideaflow.RandomIdeaFlowPartialStateBuilder;
import org.openmastery.publisher.ideaflow.RandomIdeaFlowStateEntityBuilder;
import org.openmastery.publisher.core.task.RandomTaskEntityBuilder;

public class RandomEntityBuilderSupport {

	public ActivityEntity.ActivityEntityBuilder activityEntity() {
		if (ARandom.aRandom.coinFlip()) {
			return editorActivityEntity();
		} else if (ARandom.aRandom.coinFlip()) {
			return idleActivityEntity();
		} else {
			return externalActivityEntity();
		}
	}

	public RandomEditorActivityEntityBuilder editorActivityEntity() {
		return new RandomEditorActivityEntityBuilder();
	}

	public RandomIdleActivityEntityBuilder idleActivityEntity() {
		return new RandomIdleActivityEntityBuilder();
	}

	public RandomExternalActivityEntityBuilder externalActivityEntity() {
		return new RandomExternalActivityEntityBuilder();
	}

	public RandomBlockActivityEntityBuilder blockActivityEntity() {
		return new RandomBlockActivityEntityBuilder();
	}

	public RandomExecutionActivityEntityBuilder executionActivityEntity() {
		return new RandomExecutionActivityEntityBuilder();
	}

	public RandomModificationActivityEntityBuilder modificationActivityEntity() {
		return new RandomModificationActivityEntityBuilder();
	}

	public RandomEventEntityBuilder eventEntity() {
		return new RandomEventEntityBuilder();
	}

	public RandomIdeaFlowStateEntityBuilder ideaFlowStateEntity() {
		return new RandomIdeaFlowStateEntityBuilder();
	}

	public RandomIdeaFlowPartialStateBuilder ideaFlowPartialStateEntity() {
		return new RandomIdeaFlowPartialStateBuilder();
	}

	public RandomTaskEntityBuilder taskEntity() {
		return new RandomTaskEntityBuilder();
	}

}
