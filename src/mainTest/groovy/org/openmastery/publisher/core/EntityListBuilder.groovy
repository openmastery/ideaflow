package org.openmastery.publisher.core

import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.time.MockTimeService

import java.time.LocalDateTime


class EntityListBuilder {

	private MockTimeService timeService
	LocalDateTime startTime
	List<EditorActivityEntity> editorActivityEntityList = []
	List<IdleActivityEntity> idleActivityEntityList = []
	List<ExternalActivityEntity> externalActivityList = []
	List<ExecutionActivityEntity> executionActivityList = []
	List<EventEntity> eventList = []

	EntityListBuilder() {
		this(new MockTimeService())
	}

	EntityListBuilder(MockTimeService timeService) {
		this.timeService = timeService
		this.startTime = timeService.now()
	}

	EntityListBuilder advanceMinutes(int minutes) {
		timeService.plusMinutes(minutes)
		this
	}

	EntityListBuilder idleActivity(int minutes) {
		LocalDateTime start = timeService.now()
		idleActivityEntityList << IdleActivityEntity.builder()
				.start(start)
				.end(start.plusMinutes(minutes))
				.build()
		advanceMinutes(minutes)
		this
	}

	EntityListBuilder viewFile(int minutes, String filePath) {
		editorActivity(timeService.now(), timeService.minutesInFuture(minutes), filePath, false)
	}

	EntityListBuilder viewFileAndAdvanceMinutes(int minutes, String filePath) {
		viewFile(minutes, filePath)
		advanceMinutes(minutes)
	}

	EntityListBuilder modifyFile(int minutes, String filePath) {
		editorActivity(timeService.now(), timeService.minutesInFuture(minutes), filePath, true)
	}

	EntityListBuilder modifyFileAndAdvanceMinutes(int minutes, String filePath) {
		modifyFile(minutes, filePath)
		advanceMinutes(minutes)
	}

	private EntityListBuilder editorActivity(LocalDateTime start, LocalDateTime end, String filePath, boolean isModified) {
		editorActivityEntityList << EditorActivityEntity.builder()
				.start(start)
				.end(end)
				.filePath(filePath)
				.isModified(isModified)
				.build()
		this
	}

	EntityListBuilder debug(int minutes, String processName, boolean success = true) {
		executionActivity(timeService.now(), timeService.minutesInFuture(minutes), processName, true, success)
	}

	EntityListBuilder debugAndAdvanceMinutes(int minutes, String processName, boolean success = true) {
		debug(minutes, processName, success)
		advanceMinutes(minutes)
	}

	EntityListBuilder execute(int minutes, String processName, boolean success = true) {
		executionActivity(timeService.now(), timeService.minutesInFuture(minutes), processName, false, success)
	}

	EntityListBuilder executeAndAdvanceMinutes(int minutes, String processName, boolean success = true) {
		execute(minutes, processName, success)
		advanceMinutes(minutes)
	}

	private EntityListBuilder executionActivity(LocalDateTime start, LocalDateTime end, String processName, boolean debug, boolean success) {
		executionActivityList << ExecutionActivityEntity.builder()
				.start(start)
				.end(end)
				.processName(processName)
				.debug(debug)
				.exitCode(success ? 0 : 1)
				.build()
		this
	}

	EntityListBuilder switchToExternalApp(int minutes, String comment) {
		externalActivity(timeService.now(), timeService.minutesInFuture(minutes), comment)
		advanceMinutes(minutes)
	}

	private EntityListBuilder externalActivity(LocalDateTime start, LocalDateTime end, String comment) {
		externalActivityList << ExternalActivityEntity.builder()
				.start(start)
				.end(end)
				.comment(comment)
				.build()
		this
	}

	EntityListBuilder note(String comment) {
		event(timeService.now(), EventType.NOTE, comment)
	}

	EntityListBuilder subtask(String comment) {
		event(timeService.now(), EventType.SUBTASK, comment)
	}

	EntityListBuilder distraction(String comment) {
		event(timeService.now(), EventType.DISTRACTION, comment)
	}

	private EntityListBuilder event(LocalDateTime position, EventType type, String comment) {
		eventList << EventEntity.builder()
				.position(position)
				.type(type)
				.comment(comment)
				.build()
		this
	}

}
