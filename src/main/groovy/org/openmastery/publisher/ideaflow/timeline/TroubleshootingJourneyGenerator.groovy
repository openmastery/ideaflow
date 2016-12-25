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

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.Experiment
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.springframework.stereotype.Component

/**
 * Generates all the troubleshooting journeys from the WTF/YAY events within the timeline
 */
@Component
class TroubleshootingJourneyGenerator {

	TroubleshootingJourney createJourney(List<Event> events, IdeaFlowBand band) {
		List<Event> wtfYayEvents = events.findAll { it.type == EventType.WTF }
		return splitIntoJourneys(wtfYayEvents, [band]).first()
	}

	TroubleshootingJourney createJourney(List<Event> events, IdeaFlowBand band, List<ExecutionEvent> executionEvents) {
		List<Event> wtfYayEvents = events.findAll { it.type == EventType.WTF }
		List<TroubleshootingJourney> journeys = splitIntoJourneys(wtfYayEvents, [band])

		TroubleshootingJourney journey = journeys.first()
		journey.fillWithActivity(executionEvents)

		return journey
	}

	List<TroubleshootingJourney> createFromTimeline(IdeaFlowTimeline timeline) {
		List<Event> wtfYayEvents = timeline.events.findAll { Event event ->
			event.type == EventType.WTF || event.type == EventType.AWESOME
		}

		List<IdeaFlowBand> troubleshootingBands = timeline.ideaFlowBands.findAll { IdeaFlowBand band ->
			band.type == IdeaFlowStateType.TROUBLESHOOTING
		}

		List<TroubleshootingJourney> journeys = splitIntoJourneys(wtfYayEvents, troubleshootingBands)
		journeys.each { TroubleshootingJourney journey ->
			journey.fillWithActivity(timeline.executionEvents)
		}

		return journeys
	}


	private List<TroubleshootingJourney> splitIntoJourneys(List<Event> wtfYayEvents, List<IdeaFlowBand> troubleshootingBands) {
		List<TroubleshootingJourney> journeyList = []

		troubleshootingBands.each { IdeaFlowBand troubleshootingBand ->
			TroubleshootingJourney journey = new TroubleshootingJourney(troubleshootingBand)
			//TODO write refactoring plugin "Inject toString()" from a print statement
			println "Journey [" + journey.relativeStart + ", " + journey.relativeEnd + "]"

			for (int activeIndex = 0; activeIndex < wtfYayEvents.size(); activeIndex++) {
				Event wtfYayEvent = wtfYayEvents.get(activeIndex)
				Long eventPosition = wtfYayEvent.relativePositionInSeconds
				println "Event [" + eventPosition + ", " + wtfYayEvent.type + "]"

				if (eventPosition >= journey.relativeStart && eventPosition <= journey.relativeEnd) {
					println "inside!"

					Long durationInSeconds = 0;
					if (wtfYayEvents.size() > activeIndex + 1) {
						Event peekAtNextEvent = wtfYayEvents.get(activeIndex + 1)
						durationInSeconds = peekAtNextEvent.relativePositionInSeconds - wtfYayEvent.relativePositionInSeconds
						println "[Calculate] Experiment duration (peek): " + durationInSeconds
					} else {
						durationInSeconds = troubleshootingBand.relativeEnd - wtfYayEvent.relativePositionInSeconds
						println "[Calculate] Experiment duration (band-end): " + durationInSeconds
					}

					journey.addExperiment(wtfYayEvent, durationInSeconds);
				}
			}

			journeyList.add(journey)
		}

		return journeyList

	}
}
