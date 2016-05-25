package org.ideaflow.publisher.core.task

import static org.ideaflow.publisher.ARandom.aRandom

class RandomTaskEntityBuilder extends TaskEntity.TaskEntityBuilder {

	RandomTaskEntityBuilder() {
		super.id(aRandom.id())
				.name(aRandom.text(250))
				.description(aRandom.optionalText(500))
	}

}
