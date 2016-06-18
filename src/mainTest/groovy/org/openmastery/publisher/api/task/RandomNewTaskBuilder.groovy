package org.openmastery.publisher.api.task

import static org.openmastery.publisher.ARandom.aRandom

class RandomNewTaskBuilder extends NewTask.NewTaskBuilder {

	RandomNewTaskBuilder() {
		super.name(aRandom.text(10))
				.description(aRandom.optionalText(50))
	}

}
