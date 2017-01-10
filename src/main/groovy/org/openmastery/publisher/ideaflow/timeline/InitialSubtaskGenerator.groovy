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
package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType


class InitialSubtaskGenerator {

	Event generateInitialStrategySubtaskEvent(List<Event> events, Long taskId, LocalDateTime timelineStart) {
		events = events.sort(false, PositionableComparator.INSTANCE)
		Event firstSubtaskEvent = events.find { it.type == EventType.SUBTASK }
		if ((firstSubtaskEvent != null) && firstSubtaskEvent.position.isEqual(timelineStart)) {
			return null
		}

		Event initialStrategySubtaskEvent = Event.builder()
				.id(-1)
				.type(EventType.SUBTASK)
				.comment("Initial Strategy")
				.build()
		initialStrategySubtaskEvent.taskId = taskId
		initialStrategySubtaskEvent.position = timelineStart
		initialStrategySubtaskEvent.relativePositionInSeconds = 0
		initialStrategySubtaskEvent
	}
}
