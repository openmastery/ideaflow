package org.openmastery.publisher.api;

import java.util.Comparator;

public class IntervalComparator implements Comparator<Interval> {

	public static final IntervalComparator INSTANCE = new IntervalComparator();

	@Override
	public int compare(Interval o1, Interval o2) {
		return o1.getStart().compareTo(o2.getStart());
	}

}
