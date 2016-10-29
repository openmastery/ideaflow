package org.openmastery.publisher.core.activity

import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.activity.NewActivity
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.core.IdeaFlowPersistenceService
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
		return entity
	}


}
