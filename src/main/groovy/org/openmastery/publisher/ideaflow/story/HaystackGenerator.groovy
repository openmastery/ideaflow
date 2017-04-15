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

import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.activity.EditorActivity
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.Haystack
import org.springframework.stereotype.Component

@Component
class HaystackGenerator {

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
}
