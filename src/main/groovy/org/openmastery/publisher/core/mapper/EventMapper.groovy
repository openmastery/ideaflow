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
package org.openmastery.publisher.core.mapper

import org.openmastery.mapper.TypedValueObjectMapper
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.core.event.EventEntity


class EventMapper extends TypedValueObjectMapper<EventEntity, Event> {

	EventMapper() {
		super(Event.class)
	}

	@Override
	protected void onMap(EventEntity from, Event to) {
		to.description = from.comment
	}

}
