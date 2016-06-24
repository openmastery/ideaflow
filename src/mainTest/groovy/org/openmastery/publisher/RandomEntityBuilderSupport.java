package org.openmastery.publisher;

import org.openmastery.publisher.core.activity.ActivityEntity;
import org.openmastery.publisher.core.activity.RandomEditorActivityEntityBuilder;
import org.openmastery.publisher.core.activity.RandomExternalActivityEntityBuilder;
import org.openmastery.publisher.core.activity.RandomIdleActivityEntityBuilder;
import org.openmastery.publisher.core.event.RandomEventEntityBuilder;
import org.openmastery.publisher.core.ideaflow.RandomIdeaFlowPartialStateBuilder;
import org.openmastery.publisher.core.ideaflow.RandomIdeaFlowStateEntityBuilder;
import org.openmastery.publisher.core.task.RandomTaskEntityBuilder;

import java.util.Random;

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
