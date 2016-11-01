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
package org.openmastery.publisher.core.activity

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.activity.NewActivity
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.time.TimeConverter
import org.openmastery.time.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Duration
import java.time.LocalDateTime

@Component
class ActivityService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;
	@Autowired
	private TimeService timeService;
	@Autowired
	private InvocationContext invocationContext;
	private EntityMapper entityMapper = new EntityMapper();

	Duration determineTimeAdjustment(LocalDateTime messageSentAt) {
		LocalDateTime now = timeService.now()

		Duration.between(messageSentAt, now)
	}


	public void addActivityBatch(NewActivityBatch batch) {
		Duration adjustment = determineTimeAdjustment(TimeConverter.toJavaLocalDateTime(batch.timeSent))

		saveActivities(batch.editorActivityList, adjustment, EditorActivityEntity.class)
		saveActivities(batch.externalActivityList, adjustment, ExternalActivityEntity.class)
		saveActivities(batch.idleActivityList, adjustment, IdleActivityEntity.class)
		saveActivities(batch.executionActivityList, adjustment, ExecutionActivityEntity.class)
		saveActivities(batch.modificationActivityList, adjustment, ModificationActivityEntity.class)
	}

	public void saveActivities(List<NewActivity> activityList, Duration adjustment, Class clazz) {
		activityList.each { NewActivity activity ->
			ActivityEntity entity = buildEntity(activity, adjustment, clazz)
			persistenceService.saveActivity(entity)
		}
	}

	public ActivityEntity buildEntity( NewActivity activity, Duration adjustment, Class clazz) {
		ActivityEntity entity = entityMapper.mapIfNotNull(activity, clazz) as ActivityEntity

		LocalDateTime endTime = TimeConverter.toJavaLocalDateTime(activity.endTime)
		entity.setStart( endTime.plus(adjustment).minusSeconds(activity.getDurationInSeconds()))
		entity.setEnd( endTime.plus(adjustment))
		entity.setOwnerId(invocationContext.getUserId());
		return entity
	}


}
