package org.openmastery.publisher.core.timeline;

import java.util.Comparator;

public class TimeBandComparator implements Comparator<TimeBandModel> {

	public static final TimeBandComparator INSTANCE = new TimeBandComparator();

	@Override
	public int compare(TimeBandModel o1, TimeBandModel o2) {
		return o1.getStart().compareTo(o2.getStart());
	}

}
