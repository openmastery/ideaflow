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
package org.openmastery.publisher.core

import com.bancvue.rest.exception.NotFoundException
import org.apache.commons.lang3.NotImplementedException
import org.hibernate.cfg.NotYetImplementedException
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.event.AnnotatedEvent
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.journey.FormattableSnippet
import org.openmastery.publisher.core.annotation.AnnotationRespository
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.event.EventRepository
import org.openmastery.time.TimeConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDateTime

@Component
class EventService {

	@Autowired
	private EventRepository eventRepository

	@Autowired
	private AnnotationRespository annotationRespository


	public List<Event> getLatestEventsByType(Long userId, EventType eventType, LocalDateTime afterDate, Integer limit) {
		throw new NotImplementedException("getLatestEventsByType is not yet supported")
	}


	public List<Event> getLatestEvents(Long userId, LocalDateTime afterDate, Integer limit) {
		List<EventEntity> eventEntityList = eventRepository.findRecentEvents(userId, TimeConverter.toSqlTimestamp(afterDate), limit)

		List<Event> eventList = eventEntityList.collect() { EventEntity entity ->
			EntityMapper mapper = new EntityMapper()
			mapper.mapIfNotNull(entity, Event.class)
		}
		return eventList
	}

	Event updateJourney(Long userId, Long journeyId, String comment) {
		throw new NotYetImplementedException("Need to implement this still!")
	}



	Event updateEvent(Long userId, Long eventId, String comment) {
		//TODO this query should take userId too
		EventEntity entity = eventRepository.findByOwnerIdAndId(userId, eventId)
		entity.comment = comment
		entity.ownerId = userId

		EventEntity savedEntity = eventRepository.save(entity);
		return toApi(savedEntity)
	}

	Event toApi(EventEntity entity) {
		EntityMapper mapper = new EntityMapper()
		Event event = mapper.mapIfNotNull(entity, Event.class)
		event.description = entity.comment
		return event
	}

	EventEntity toEntity(Event event) {
		EntityMapper mapper = new EntityMapper()
		EventEntity entity = mapper.mapIfNotNull(event, EventEntity.class)
		entity.comment = event.description
		return entity
	}

	AnnotatedEvent annotateWithFAQ(Long userId, Long eventId, String faqComment) {
		EventEntity eventEntity = eventRepository.findByOwnerIdAndId(userId, eventId)
		if (eventEntity == null) {
			throw new NotFoundException("Unable to annotate event.  EventId = $eventId not found.")
		}

		annotationRespository.deleteByEventAndType(eventId, "faq");

		FaqAnnotationEntity faqAnnotationEntity = FaqAnnotationEntity.builder()
				.ownerId(userId)
				.taskId(eventEntity.taskId)
				.eventId(eventId)
				.comment(faqComment).build()

		annotationRespository.save(faqAnnotationEntity)

		AnnotatedEvent event = new AnnotatedEvent()
		event.taskId = eventEntity.taskId
		event.eventId = eventEntity.id
		event.type = eventEntity.type
		event.description = eventEntity.comment
		event.faq = faqAnnotationEntity.comment

		return event;
	}


	AnnotatedEvent annotateWithSnippet(long userId, Long eventId, FormattableSnippet formattableSnippet) {
		EventEntity eventEntity = eventRepository.findByOwnerIdAndId(userId, eventId)
		if (eventEntity == null) {
			throw new NotFoundException("Unable to annotate event.  EventId = $eventId not found.")
		}

		SnippetAnnotationEntity snippetAnnotationEntity = SnippetAnnotationEntity.builder()
			.ownerId(userId)
			.taskId(eventEntity.taskId)
			.eventId(eventId)
			.snippet(formattableSnippet.contents)
			.source(formattableSnippet.source)
			.build()

		annotationRespository.deleteByEventAndType(eventId, "snippet")
		annotationRespository.save(snippetAnnotationEntity)

		AnnotatedEvent event = new AnnotatedEvent()
		event.taskId = eventEntity.taskId
		event.eventId = eventEntity.id
		event.type = eventEntity.type
		event.description = eventEntity.comment

		return event
	}
}
