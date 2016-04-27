package org.ideaflow.publisher.api;

import java.util.Comparator;

public class TimeBandComparator implements Comparator<TimeBand> {

	@Override
	public int compare(TimeBand o1, TimeBand o2) {
		return o1.getStart().compareTo(o2.getStart());
	}

}
