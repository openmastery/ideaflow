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
package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.publisher.api.PositionableComparator
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.core.timeline.TimeBandIdleCalculator
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel

class IdleTimeProcessor {

	private TimeBandIdleCalculator timeBandCalculator = new TimeBandIdleCalculator()

	public List<IdleTimeBandModel> generateIdleTimeBandsFromDeativationEvents(List<Event> events) {
		List<Event> deactivationEvents = getSortedActivationAndDeactivationEvents(events)

		List<IdleTimeBandModel> idleTimeBandModelList = []
		Event matchingDeactivationEvent = null
		for (Event event : deactivationEvents) {
			if (event.type == EventType.DEACTIVATE) {
				if (matchingDeactivationEvent == null) {
					matchingDeactivationEvent = event
				}
			} else if (event.type == EventType.ACTIVATE && matchingDeactivationEvent != null) {
				idleTimeBandModelList << createIdleTimeBand(matchingDeactivationEvent, event)
				matchingDeactivationEvent = null
			}
		}
		idleTimeBandModelList
	}

	private List<Event> getSortedActivationAndDeactivationEvents(List<Event> events) {
		List<Event> deactivationEvents = events.findAll { Event event ->
			event.type == EventType.ACTIVATE || event.type == EventType.DEACTIVATE
		}
		deactivationEvents.sort(PositionableComparator.INSTANCE)
		deactivationEvents
	}

	private IdleTimeBandModel createIdleTimeBand(Event deactivationEvent, Event activationEvent) {
		IdleTimeBandModel.builder()
				.start(deactivationEvent.position)
				.end(activationEvent.position)
				.comment(deactivationEvent.comment)
				.auto(true)
				.build()
	}

	public void collapseIdleTime(List<IdeaFlowBandModel> ideaFlowBands, List<IdleTimeBandModel> idleTimeBandList) {
		for (IdleTimeBandModel idleTimeBandModel : idleTimeBandList) {
			addIdleDuration(ideaFlowBands, idleTimeBandModel)
		}
	}

	private void addIdleDuration(List<IdeaFlowBandModel> timeBands, IdleTimeBandModel idleTimeBand) {
		for (IdeaFlowBandModel ideaFlowBandModel : timeBands) {
			IdleTimeBandModel splitIdle = timeBandCalculator.getIdleForTimeBandOrNull(ideaFlowBandModel, idleTimeBand)
			if (splitIdle != null) {
				ideaFlowBandModel.addIdleBand(splitIdle)
				addIdleDuration(ideaFlowBandModel.nestedBands, idleTimeBand)
			}
		}
	}

}
