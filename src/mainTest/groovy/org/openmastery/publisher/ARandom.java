package org.openmastery.publisher;

import lombok.experimental.Delegate;

import org.openmastery.testsupport.RandomGenerator;

public class ARandom {

	public static final ARandom aRandom = new ARandom();

	@Delegate
	private RandomBuilderSupport randomBuilderSupport = new RandomBuilderSupport();

	@Delegate
	private RandomGenerator randomGenerator = new RandomGenerator();

}
