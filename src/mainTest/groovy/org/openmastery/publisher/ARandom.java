package org.openmastery.publisher;

import lombok.experimental.Delegate;

import org.openmastery.publisher.api.RandomApiBuilderSupport;
import org.openmastery.testsupport.RandomGenerator;

public class ARandom {

	public static final ARandom aRandom = new ARandom();

	@Delegate
	private RandomEntityBuilderSupport randomEntityBuilderSupport = new RandomEntityBuilderSupport();

	@Delegate
	private RandomApiBuilderSupport randomApiBuilderSupport = new RandomApiBuilderSupport();

	@Delegate
	private RandomGenerator randomGenerator = new RandomGenerator();

}
