package org.openmastery.publisher.core;

import java.util.Comparator;

public class PositionableComparator implements Comparator<Positionable> {

	public static final PositionableComparator INSTANCE = new PositionableComparator();

	@Override
	public int compare(Positionable o1, Positionable o2) {
		return o1.getPosition().compareTo(o2.getPosition());
	}

}
