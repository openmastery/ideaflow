package org.openmastery.publisher.api;

import org.openmastery.publisher.api.task.RandomNewTaskBuilder;

public class RandomApiBuilderSupport {

	public RandomNewTaskBuilder newTask() {
		return new RandomNewTaskBuilder();
	}

}
