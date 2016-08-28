package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.core.Positionable
import org.openmastery.publisher.core.PositionableComparator

import java.time.Duration

class RelativeTimeProcessor {

	private static final Comparator<Positionable> IDLE_TIME_BAND_MODEL_COMES_LAST_POSITIONABLE_COMPARABLE = new Comparator<Positionable>() {
		@Override
		public int compare(Positionable o1, Positionable o2) {
			int comparison = PositionableComparator.INSTANCE.compare(o1, o2)

			if (comparison == 0) {
				if (o1 instanceof IdleTimeBandModel) {
					if ((o2 instanceof IdleTimeBandModel) == false) {
						comparison = 1
					}
				} else if (o2 instanceof IdleTimeBandModel) {
					comparison = -1
				}
			}
			comparison
		}
	}

	public void computeRelativeTime(List<Positionable> positionables) {
		positionables = positionables.sort(false, IDLE_TIME_BAND_MODEL_COMES_LAST_POSITIONABLE_COMPARABLE)

		long relativeTime = 0
		Positionable previousPositionable = null
		for (Positionable positionable : positionables) {
			if (previousPositionable != null) {
				Duration duration
				if (previousPositionable instanceof IdleTimeBandModel) {
					duration = Duration.between(previousPositionable.end, positionable.getPosition())
				} else {
					duration = Duration.between(previousPositionable.getPosition(), positionable.getPosition())
				}
				if (duration.isNegative() == false) {
					relativeTime += duration.seconds
				}
			}

			positionable.relativePositionInSeconds = relativeTime
			previousPositionable = positionable
		}
	}

}
