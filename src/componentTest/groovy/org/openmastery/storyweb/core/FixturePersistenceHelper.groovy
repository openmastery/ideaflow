package org.openmastery.storyweb.core

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineElementBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FixturePersistenceHelper {

	@Autowired
	IdeaFlowPersistenceService persistenceService

	EntityMapper entityMapper = new EntityMapper()

	void saveIdeaFlow(Long ownerId, Long taskId, IdeaFlowTimelineElementBuilder builder) {
		builder.eventList.each { Event event ->
			EventEntity entity = entityMapper.mapIfNotNull(event, EventEntity)
			entity.taskId = taskId
			entity.ownerId = ownerId
			entity.comment = event.description
			persistenceService.saveEvent(entity)
		}

		builder.idleTimeBands.each { IdleTimeBandModel idle ->
			IdleActivityEntity entity = entityMapper.mapIfNotNull(idle, IdleActivityEntity)
			entity.taskId = taskId
			entity.ownerId = ownerId
			persistenceService.saveActivity(entity)
		}

		builder.executionEventList.each { ExecutionEvent executionEvent ->
			ExecutionActivityEntity entity = entityMapper.mapIfNotNull(executionEvent, ExecutionActivityEntity)
			entity.taskId = taskId
			entity.ownerId = ownerId
			entity.end = executionEvent.position
			persistenceService.saveActivity(entity)
		}
	}
}
