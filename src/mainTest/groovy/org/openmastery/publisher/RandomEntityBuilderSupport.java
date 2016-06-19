package org.openmastery.publisher;

import org.openmastery.publisher.core.activity.RandomEditorActivityEntityBuilder;
import org.openmastery.publisher.core.activity.RandomIdleActivityEntityBuilder;
import org.openmastery.publisher.core.event.RandomEventEntityBuilder;
import org.openmastery.publisher.core.ideaflow.RandomIdeaFlowStateEntityBuilder;
import org.openmastery.publisher.core.task.RandomTaskEntityBuilder;

public class RandomEntityBuilderSupport {

	public RandomEditorActivityEntityBuilder editorActivityEntity() {
		return new RandomEditorActivityEntityBuilder();
	}

	public RandomIdleActivityEntityBuilder idleActivityEntity() {
		return new RandomIdleActivityEntityBuilder();
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
