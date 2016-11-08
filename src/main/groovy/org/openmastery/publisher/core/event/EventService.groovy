package org.openmastery.publisher.core.event

import org.joda.time.LocalDateTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.core.IdeaFlowPersistenceService
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
