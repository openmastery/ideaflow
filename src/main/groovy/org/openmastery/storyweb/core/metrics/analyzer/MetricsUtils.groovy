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
package org.openmastery.storyweb.core.metrics.analyzer

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.journey.TagsUtil


class MetricsUtils {

	static double roundOff(double rawDouble) {
		return Math.round(rawDouble * 100)/100
	}

	static double calculateNumberDaysInSample(long durationInSeconds) {
		double days = durationInSeconds.toDouble() / (60 * 60 * 6)
		return Math.max(days, 1);
	}

	static Set<String> extractPainTags(List<Event> events) {
		Set<String> painTags = new HashSet<>()
		events.each { Event event ->
			painTags.addAll(TagsUtil.extractUniqueHashTags(event.comment))
		}
		return painTags
	}

	static List<Event> findEventsMatchingType(List<Event> events, EventType ... types) {
		List<Event> wtfEvents = events.findAll() { Event event ->
			types.contains(event.type)
		}
	}
}
