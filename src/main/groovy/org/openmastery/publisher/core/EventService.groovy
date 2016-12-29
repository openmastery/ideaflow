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

import com.bancvue.rest.exception.NotFoundException
import org.joda.time.LocalDateTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.annotation.FAQAnnotation
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventService {


	@Autowired
	private IdeaFlowPersistenceService persistenceService


	public List<Event> getLatestEvents(Long userId, LocalDateTime afterDate, Integer limit) {
		List<EventEntity> eventEntityList = persistenceService.findRecentEvents(userId, TimeConverter.toSqlTimestamp(afterDate), limit)

		List<Event> eventList = eventEntityList.collect() { EventEntity entity ->
			EntityMapper mapper = new EntityMapper()
			mapper.mapIfNotNull(entity, Event.class)
		}
		return eventList
	}

	Event updateEvent(Long userId, Event eventToUpdate) {
		EventEntity entityToSave = toEntity(eventToUpdate);
		entityToSave.ownerId = userId
		EventEntity savedEntity = persistenceService.saveEvent(entityToSave);

		return toApi(savedEntity)
	}

	Event toApi(EventEntity entity) {
		EntityMapper mapper = new EntityMapper()
		return mapper.mapIfNotNull(entity, Event.class)
	}

	EventEntity toEntity(Event event) {
		EntityMapper mapper = new EntityMapper()
		return mapper.mapIfNotNull(event, EventEntity.class)
	}

	FAQAnnotation annotateWithFAQ(Long userId, Long eventId, FAQAnnotation annotation) {
		EventEntity eventEntity = persistenceService.findEventById(eventId)
		if (eventEntity == null) {
			throw new NotFoundException("Unable to annotate event.  EventId = $eventId not found.")
		}

		persistenceService.deleteFAQAnnotation(eventId)

		FaqAnnotationEntity faqAnnotationEntity = FaqAnnotationEntity.builder()
				.ownerId(userId)
				.taskId(eventEntity.taskId)
				.eventId(eventId)
				.comment(annotation.faq).build()

		persistenceService.saveAnnotation(faqAnnotationEntity)

		return new FAQAnnotation(eventId: faqAnnotationEntity.eventId, faq: faqAnnotationEntity.comment);
	}
}
