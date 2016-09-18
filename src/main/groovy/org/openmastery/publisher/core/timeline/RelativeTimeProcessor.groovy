/*
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
