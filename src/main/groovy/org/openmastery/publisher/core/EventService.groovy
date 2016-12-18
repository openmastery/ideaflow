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
package org.openmastery.publisher.core

import org.joda.time.LocalDateTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventService {


	@Autowired
	private IdeaFlowPersistenceService persistenceService


	public List<NewBatchEvent> getLatestEvents(Long userId, LocalDateTime afterDate, Integer limit) {
		List<EventEntity> eventEntityList = persistenceService.findRecentEvents(userId, TimeConverter.toSqlTimestamp(afterDate), limit)

		List<NewBatchEvent> eventList = eventEntityList.collect() { EventEntity entity ->
			EntityMapper mapper = new EntityMapper()
			mapper.mapIfNotNull(entity, NewBatchEvent.class)
		}
		return eventList
	}

}
