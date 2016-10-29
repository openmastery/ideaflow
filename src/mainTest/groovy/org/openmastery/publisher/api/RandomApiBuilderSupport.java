package org.openmastery.publisher.api;

import org.openmastery.publisher.api.activity.RandomNewActivityBuilder;
import org.openmastery.publisher.api.task.RandomNewTaskBuilder;

public class RandomApiBuilderSupport {

	public RandomNewTaskBuilder newTask() {
		return new RandomNewTaskBuilder();
	}

	public RandomNewActivityBuilder newActivity() {
		return new RandomNewActivityBuilder();
	}



}
