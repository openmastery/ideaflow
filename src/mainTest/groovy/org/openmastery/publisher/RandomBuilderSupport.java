package org.openmastery.publisher;

import org.openmastery.publisher.core.activity.RandomEditorActivityEntityBuilder;
import org.openmastery.publisher.core.activity.RandomIdleTimeBandEntityBuilder;
import org.openmastery.publisher.core.event.RandomEventEntityBuilder;
import org.openmastery.publisher.core.ideaflow.RandomIdeaFlowStateEntityBuilder;
import org.openmastery.publisher.core.task.RandomTaskEntityBuilder;

public class RandomBuilderSupport {

	public RandomEditorActivityEntityBuilder editorActivityEntity() {
		return new RandomEditorActivityEntityBuilder();
	}

	public RandomIdleTimeBandEntityBuilder idleTimeBandEntity() {
		return new RandomIdleTimeBandEntityBuilder();
	}

	public RandomEventEntityBuilder eventEntity() {
		return new RandomEventEntityBuilder();
	}

	public RandomIdeaFlowStateEntityBuilder ideaFlowStateEntity() {
		return new RandomIdeaFlowStateEntityBuilder();
	}

	public RandomTaskEntityBuilder taskEntity() {
		return new RandomTaskEntityBuilder();
	}

}
