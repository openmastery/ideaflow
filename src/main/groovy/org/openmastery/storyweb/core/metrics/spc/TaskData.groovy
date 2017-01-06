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
package org.openmastery.storyweb.core.metrics.spc

import org.joda.time.LocalDateTime
import org.openmastery.mapper.EntityMapper
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.ideaflow.timeline.InitialSubtaskGenerator

class TaskData {

	long taskId
	Task task

	List<Event> events = []
	List<IdleTimeBandModel> idleBands = []
	List<ExecutionEvent> executionEvents = []
	List<FaqAnnotationEntity> faqAnnotations = []
	List<IdeaFlowBandModel> troubleshootingBands

	List<TroubleshootingJourney> journeys
	LocalDateTime start
	LocalDateTime end
	Long durationInSeconds

	TaskData(long taskId) {
		this.taskId = taskId
	}

	void setTask(Task task) {
		this.task = task
	}

	void addEvent(Event event) {
		events.add(event)
	}

	void addIdle(IdleTimeBandModel idleBand) {
		idleBands.add(idleBand)
	}

	void addFaq(FaqAnnotationEntity faq) {
		faqAnnotations.add(faq)
	}

	void addExecutionEvent(ExecutionEvent executionEvent) {
		executionEvents.add(executionEvent)
	}

	List<Positionable> getSortedPositionables() {
		List<Positionable> positionables = []
		positionables.addAll(events)
		positionables.addAll(idleBands)
		positionables.addAll(executionEvents)
		positionables.addAll(troubleshootingBands)
		Collections.sort(positionables, PositionableComparator.INSTANCE)

		return positionables
	}

	LocalDateTime getPosition() {
		if (events.size() > 0) {
			return events.get(0).position
		} else {
			return task.creationDate
		}
	}

	IdeaFlowTaskTimeline toIdeaFlowTaskTimeline() {

		EntityMapper mapper = new EntityMapper()
		InitialSubtaskGenerator subtaskGenerator = new InitialSubtaskGenerator()

		IdeaFlowTaskTimeline taskTimeline = IdeaFlowTaskTimeline.builder()
			.ideaFlowBands(mapper.mapList(troubleshootingBands, IdeaFlowBand.class))
			.executionEvents(executionEvents)
			.events(events)
			.task(task)
			.start(start)
			.end(end)
			.durationInSeconds(durationInSeconds)
			.relativePositionInSeconds(0)
			.build()

		Event subtask = subtaskGenerator.generateInitialStrategySubtaskEvent(events, taskId, start)
		if (subtask) {
			events.add(subtask)
			Collections.sort(events, PositionableComparator.INSTANCE)
		}

		return taskTimeline
	}

}
