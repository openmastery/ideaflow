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
package org.openmastery.publisher.ideaflow.story

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import org.openmastery.publisher.api.Interval
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.activity.EditorActivity
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.Haystack
import org.openmastery.publisher.core.activity.EditorActivityEntity
import org.openmastery.publisher.core.activity.ExecutionActivityEntity
import org.openmastery.publisher.core.activity.ExternalActivityEntity
import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.event.EventEntity
import org.openmastery.publisher.ideaflow.timeline.IdleTimeProcessor
import org.springframework.stereotype.Component

import java.time.Duration
import java.time.LocalDateTime

class HaystackGenerator {

	// IdleActivityEntity
	// EditorActivityEntity
	// ExternalActivityEntity
	// ExecutionActivityEntity

	// EditorActivityEntity, sum duration per file path, aggregate modified flag
	// ExternalActivityEntity, aggregate total time
	// ExecutionActivityEntity bounds haystacks, include debug flag

	private IdleTimeProcessor idleTimeProcessor
	private List<IdleActivityEntity> idleActivities
	private List<EditorActivityEntity> editorActivities
	private List<ExternalActivityEntity> externalActivities
	private List<ExecutionActivityEntity> executionActivities
	private List<EventEntity> events

	HaystackGenerator idleTimeProcessor(IdleTimeProcessor idleTimeProcessor) {
		this.idleTimeProcessor = idleTimeProcessor
		this
	}

	HaystackGenerator idleActivities(List<IdleActivityEntity> idleActivities) {
		this.idleActivities = idleActivities
		this
	}

	HaystackGenerator editorActivities(List<EditorActivityEntity> editorActivities) {
		this.editorActivities = editorActivities
		this
	}

	HaystackGenerator externalActivities(List<ExternalActivityEntity> externalActivities) {
		this.externalActivities = externalActivities
		this
	}

	HaystackGenerator executionActivities(List<ExecutionActivityEntity> executionActivities) {
		this.executionActivities = executionActivities
		this
	}

	HaystackGenerator events(List<EventEntity> events) {
		this.events = events
		this
	}

	List<Haystack> generate() {
		idleTimeProcessor.generateIdleTimeBandsFromDeativationEvents(events)

//		executionActivities.sort()
//		for (ExternalActivityEntity externalActivity : externalActivities) {
//			externalActivity.start
//			externalActivity.end
//			externalActivity.duration
//		}

//		editorActivities
		null
	}


	List<Haystack> generateHaystacks(List<ExecutionEvent> executionEvents, List<EditorActivity> editorActivities) {
		//sort the list all together, and then break into groups
		//progress ticks in list?
		//idles in the list?
		//disruptions in the list?

		List<Haystack> haystacks = []

		List<Positionable> positionables = getAllItemsSortedByStartTime(executionEvents, editorActivities)

		Haystack activeHaystack = null

		positionables.eachWithIndex { Positionable positionable, int i ->
			if (positionable instanceof ExecutionEvent) {
				if (activeHaystack != null) {
					haystacks.add(activeHaystack)
				}

				activeHaystack = new Haystack()
				activeHaystack.executionEvent = positionable;

			} else if (positionable instanceof EditorActivity) {
				activeHaystack.addEditorActivity(positionable)
			}
		}
		if (activeHaystack != null) {
			haystacks.add(activeHaystack)
		}

		return haystacks
	}

	public List<Positionable> getAllItemsSortedByStartTime(List<ExecutionEvent> executionEvents, List<EditorActivity> editorActivities) {
		List<Positionable> positionables = []
		positionables.addAll(executionEvents)
		positionables.addAll(editorActivities)
		Collections.sort(positionables, PositionableComparator.INSTANCE)

		return positionables
	}

	private static class Activity {

		String activityType
		String activityName
		String activityDetail

		Long durationInSeconds
		Long durationModifiedInSeconds

	}

	private static class ActivityInterval implements Interval {

		LocalDateTime start
		LocalDateTime end
		Duration Duration
		Long durationInSeconds

		LocalDateTime position
		Long relativePositionInSeconds

	}

}
