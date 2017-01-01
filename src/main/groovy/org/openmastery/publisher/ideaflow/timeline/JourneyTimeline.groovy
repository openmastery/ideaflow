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
package org.openmastery.publisher.ideaflow.timeline

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.PartialDiscovery
import org.openmastery.publisher.api.journey.ExperimentCycle
import org.openmastery.publisher.api.journey.TroubleshootingJourney


public class JourneyTimeline implements IdeaFlowTimeline {

		TroubleshootingJourney journey

		JourneyTimeline(TroubleshootingJourney journey) {
			this.journey = journey
		}

		@Override
		List<IdeaFlowBand> getIdeaFlowBands() {
			return [ journey.band ]
		}

		@Override
		List<ExecutionEvent> getExecutionEvents() {
			List<ExecutionEvent> allExecutionEvents = []

			journey.partialDiscoveries.each { PartialDiscovery partialDiscovery ->
				partialDiscovery.experimentCycles.each { ExperimentCycle experimentCycle ->
					allExecutionEvents.add(experimentCycle.executionEvent)
				}
			}

			return allExecutionEvents
		}

		@Override
		List<Event> getEvents() {
			List<Event> allEvents = []
			journey.partialDiscoveries.each { PartialDiscovery partialDiscovery ->
				allEvents.add(partialDiscovery.event)
			}
			return allEvents
		}

		@Override
		Long getDurationInSeconds() {
			return journey.durationInSeconds
		}

		@Override
		LocalDateTime getStart() {
			return journey.start
		}

		@Override
		LocalDateTime getEnd() {
			return journey.end
		}

}
