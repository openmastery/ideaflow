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

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.activity.NewActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.NewSnippetEvent
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ActivityEntity
import org.openmastery.publisher.core.activity.BlockActivityEntity
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.core.task.TaskEntity
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.time.TimeConverter
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Duration
import java.time.LocalDateTime

@Component
class IFMBatchService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private TimeService timeService;
	@Autowired
	private InvocationContext invocationContext;


	Duration determineTimeAdjustment(LocalDateTime messageSentAt) {
		LocalDateTime now = timeService.javaNow()

		Duration.between(messageSentAt, now)
	}

	public void addIFMBatch(NewIFMBatch batch) {
		Duration adjustment = determineTimeAdjustment(TimeConverter.toJavaLocalDateTime(batch.timeSent))

		EntityBuilder entityBuilder = new EntityBuilder(invocationContext.getUserId())
		List<ActivityEntity> activityEntities = entityBuilder.buildActivities(batch, adjustment)
		List<EventEntity> eventEntities = entityBuilder.buildEvents(batch, adjustment)

		saveActivities(activityEntities)
		saveEvents(eventEntities)

		//TODO clean this up, coupled to persistance because eventId is used in snippet annotation, and coupled to taskModifyDates too
		entityBuilder.buildAndSaveSnippets(batch, adjustment, persistenceService)

		saveTaskModifyDates(entityBuilder.getTaskModificationDates())

	}

	private void saveActivities(List<ActivityEntity> activityList) {
		activityList.each { ActivityEntity entity ->
			persistenceService.saveActivity(entity)
		}
	}

	private void saveEvents(List<EventEntity> eventEntityList) {
		eventEntityList.each { EventEntity entity ->
			persistenceService.saveEvent(entity)
		}
	}

	private void saveTaskModifyDates(Map<Long, LocalDateTime> taskModifyDates) {
		taskModifyDates.each { Long taskId, LocalDateTime modifyDate ->
			TaskEntity taskEntity = persistenceService.findTaskWithId(taskId)
			taskEntity.modifyDate = modifyDate
			persistenceService.saveTask(taskEntity)
		}
	}

	static class EntityBuilder {

		private EntityMapper entityMapper = new EntityMapper()
		private long userId

		private Map<Long, LocalDateTime> taskModificationDates = [:]

		EntityBuilder(long userId) {
			this.userId = userId
		}


		List<ActivityEntity> buildActivities(NewIFMBatch batch, Duration adjustment) {
			List<ActivityEntity> activities = (
				build(batch.editorActivityList, adjustment, EditorActivityEntity.class) +
				build(batch.externalActivityList, adjustment, ExternalActivityEntity.class) +
				build(batch.idleActivityList, adjustment, IdleActivityEntity.class) +
				build(batch.executionActivityList, adjustment, ExecutionActivityEntity.class) +
				build(batch.modificationActivityList, adjustment, ModificationActivityEntity.class) +
				build(batch.blockActivityList, adjustment, BlockActivityEntity.class)
			)

			return activities
		}

		List<EventEntity> buildEvents(NewIFMBatch batch, Duration adjustment) {
			batch.eventList.collect {  NewBatchEvent event ->
				buildEventEntity(event, adjustment);
			}
		}

		void buildAndSaveSnippets(NewIFMBatch batch, Duration adjustment, IdeaFlowPersistenceService persistenceService) {
			EntityMapper entityMapper = new EntityMapper()

			batch.snippetEventList.each { NewSnippetEvent snippet ->
				EventEntity eventEntity = entityMapper.mapIfNotNull(snippet, EventEntity.class)
				LocalDateTime endTime = TimeConverter.toJavaLocalDateTime(snippet.position)
				eventEntity.setPosition(endTime.plus(adjustment))
				eventEntity.setOwnerId(userId)

				EventEntity savedEvent = persistenceService.saveEvent(eventEntity)

				SnippetAnnotationEntity annotationEntity = entityMapper.mapIfNotNull(snippet, SnippetAnnotationEntity.class)
				annotationEntity.setOwnerId(userId)
				annotationEntity.setEventId(savedEvent.id)

				persistenceService.saveAnnotation(annotationEntity)

				println "Snippet:" + savedEvent.position
				recordTaskModification(savedEvent.taskId, savedEvent.position)
			}
		}

		Map<Long, LocalDateTime> getTaskModificationDates() {
			Map<Long, LocalDateTime> taskModificationDates = taskModificationDates
			this.taskModificationDates = Collections.unmodifiableMap(taskModificationDates)
			return taskModificationDates
		}

		private List<ActivityEntity> build(List<NewActivity> activityList, Duration adjustment, Class clazz) {
			activityList.collect { NewActivity activity ->
				buildActivityEntity(activity, adjustment, clazz)
			}
		}

		private ActivityEntity buildActivityEntity( NewActivity activity, Duration adjustment, Class clazz) {
			ActivityEntity entity = entityMapper.mapIfNotNull(activity, clazz) as ActivityEntity

			LocalDateTime endTime = TimeConverter.toJavaLocalDateTime(activity.endTime)
			entity.setStart( endTime.plus(adjustment).minusSeconds(activity.getDurationInSeconds()))
			entity.setEnd( endTime.plus(adjustment))
			entity.setOwnerId(userId);

			recordTaskModification(entity.taskId, entity.end)
			return entity
		}

		private EventEntity buildEventEntity ( NewBatchEvent event, Duration adjustment) {
			EventEntity entity = entityMapper.mapIfNotNull(event, EventEntity.class)

			LocalDateTime endTime = TimeConverter.toJavaLocalDateTime(event.position)
			entity.setPosition(endTime.plus(adjustment))
			entity.setOwnerId(userId)

			recordTaskModification(entity.taskId, entity.position)
			return entity
		}




		private void recordTaskModification(Long taskId, LocalDateTime modifyDate) {
			LocalDateTime lastModified = taskModificationDates.get(taskId)

			if (lastModified == null || modifyDate.isAfter(lastModified)) {
				taskModificationDates.put(taskId, modifyDate)
			}
		}

	}

}
