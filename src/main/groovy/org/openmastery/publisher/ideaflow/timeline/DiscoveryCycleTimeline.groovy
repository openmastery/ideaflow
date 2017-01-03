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
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.DiscoveryCycle
import org.openmastery.publisher.api.journey.ExperimentCycle

class DiscoveryCycleTimeline implements IdeaFlowTimeline {

	DiscoveryCycle discoveryCycle

	DiscoveryCycleTimeline(DiscoveryCycle discoveryCycle) {
		this.discoveryCycle = discoveryCycle
	}

	@Override
	List<IdeaFlowBand> getIdeaFlowBands() {
		IdeaFlowBand band = new IdeaFlowBand()
		band.setRelativePositionInSeconds(discoveryCycle.relativePositionInSeconds)
		band.setDurationInSeconds(discoveryCycle.durationInSeconds)
		band.setStart(discoveryCycle.event.getPosition())
		band.setEnd(discoveryCycle.event.getPosition().plusSeconds((int)discoveryCycle.durationInSeconds))
		band.type = IdeaFlowStateType.TROUBLESHOOTING
		return [band]
	}

	@Override
	List<ExecutionEvent> getExecutionEvents() {
		List<ExecutionEvent> allExecutionEvents = []

		discoveryCycle.getExperimentCycles().each { ExperimentCycle experimentCycle ->
			allExecutionEvents.add(experimentCycle.executionEvent)
		}

		return allExecutionEvents
	}

	@Override
	List<Event> getEvents() {
		return [discoveryCycle.event]
	}

	@Override
	Long getDurationInSeconds() {
		return discoveryCycle.getDurationInSeconds()
	}

	@Override
	LocalDateTime getStart() {
		return discoveryCycle.event.getPosition()
	}

	@Override
	LocalDateTime getEnd() {
		return getStart().plusSeconds((int)getDurationInSeconds())
	}
}
