package org.openmastery.publisher.core.task

import static org.openmastery.publisher.ARandom.aRandom

class RandomTaskEntityBuilder extends TaskEntity.TaskEntityBuilder {

	RandomTaskEntityBuilder() {
		super.id(aRandom.id())
				.name(aRandom.text(250))
				.description(aRandom.optionalText(500))
	}

}
