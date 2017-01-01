/*
 * Copyright 2017 New Iron Group, Inc.
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

import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.api.event.EventType

class BandTimelineSplitter {

	List<BandTimelineSegment> splitTimelineSegment(BandTimelineSegment segment) {
		boolean hasSubtask = segment.events.find { it.type == EventType.SUBTASK }
		if (hasSubtask == false) {
			return [segment]
		}

		List timeBands = segment.getAllTimeBands()
		Collections.sort(timeBands, PositionableComparator.INSTANCE);
		List<BandTimelineSegment> splitSegments = []
		BandTimelineSegment activeSegment = new BandTimelineSegment()
		activeSegment.id = segment.id
		activeSegment.description = segment.description

		List<EventModel> sortedEvents = segment.events.sort { EventModel event -> event.position }
		sortedEvents.each { EventModel event ->
			if ((event.type == EventType.SUBTASK) == false) {
				activeSegment.addEvent(event)
				return
			}

			while (timeBands.isEmpty() == false) {
				TimeBandModel timeBand = timeBands.remove(0)
				if (timeBand.endsOnOrBefore(event.position)) {
					activeSegment.addTimeBand(timeBand)
				} else {
					if (timeBand.start == event.position) {
						// pop the band back on the stack for processing by the next event
						timeBands.add(0, timeBand)
					} else {
						TimeBandModel leftBand = timeBand.splitAndReturnLeftSide(event.position)
						TimeBandModel rightBand = timeBand.splitAndReturnRightSide(event.position)
						activeSegment.addTimeBand(leftBand)
						timeBands.add(0, rightBand)
					}
					break
				}
			}

			if (activeSegment.ideaFlowBands || activeSegment.timeBandGroups) {
				splitSegments << activeSegment
				activeSegment = new BandTimelineSegment()
				activeSegment.addEvent(event)
				activeSegment.id = event.id
				activeSegment.description = event.comment
			}
		}

		if (timeBands) {
			timeBands.each { TimeBandModel timeBand ->
				activeSegment.addTimeBand(timeBand)
			}
			splitSegments << activeSegment
		}

		splitSegments
	}

}
