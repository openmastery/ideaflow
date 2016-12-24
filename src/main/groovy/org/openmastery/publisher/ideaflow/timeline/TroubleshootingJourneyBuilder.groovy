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

/**
 * Generates all the troubleshooting journeys from the WTF/YAY events within the timeline
 */
class TroubleshootingJourneyBuilder {

	IdeaFlowTimeline timeline

	TroubleshootingJourneyBuilder timeline(IdeaFlowTimeline timeline) {
		this.timeline = timeline
		this
	}

	List<TroubleshootingJourney> build() {
		List<Event> wtfYayEvents = timeline.events.findAll { Event event ->
			event.type == EventType.WTF || event.type == EventType.AWESOME
		}

		List<IdeaFlowBand> troubleshootingBands = timeline.ideaFlowBands.findAll { IdeaFlowBand band ->
			band.type == IdeaFlowStateType.TROUBLESHOOTING
		}

		List<TroubleshootingJourney> journeys = splitIntoJourneys(wtfYayEvents, troubleshootingBands)
		journeys.each { TroubleshootingJourney journey ->
			fillJourneyWithActivity(journey, timeline)
		}

		return journeys
	}

	void fillJourneyWithActivity(TroubleshootingJourney troubleshootingJourney, IdeaFlowTimeline ideaFlowTimeline) {

		//what I actually want here is execution activity.  Duration

		//TODO add file events to this too, they would be perfect here.

		List<ExecutionEvent> executionEvents = ideaFlowTimeline.getExecutionEvents()
		executionEvents.each { ExecutionEvent executionEvent ->
			if (troubleshootingJourney.shouldContain(executionEvent)) {
				troubleshootingJourney.addExecutionEvent(executionEvent)
			}
		} //TODO this could be a lot more efficient


	}

	private List<TroubleshootingJourney> splitIntoJourneys(List<Event> wtfYayEvents, List<IdeaFlowBand> troubleshootingBands) {
		List<TroubleshootingJourney> journeyList = []

		troubleshootingBands.each { IdeaFlowBand troubleshootingBand ->
			TroubleshootingJourney journey = new TroubleshootingJourney(troubleshootingBand)
			//TODO write refactoring plugin "Inject toString()" from a print statement
			println "Journey ["+journey.relativeStart + ", " + journey.relativeEnd + "]"

			for (int activeIndex = 0; activeIndex < wtfYayEvents.size(); activeIndex++) {
					Event wtfYayEvent = wtfYayEvents.get(activeIndex)
					Long eventPosition = wtfYayEvent.relativePositionInSeconds
					println "Event ["+eventPosition+", "+wtfYayEvent.type+ "]"

					if (eventPosition >= journey.relativeStart && eventPosition <= journey.relativeEnd ) {
						println "inside!"

						Long durationInSeconds = 0;
						if (wtfYayEvents.size() > activeIndex + 1) {
							Event peekAtNextEvent = wtfYayEvents.get(activeIndex + 1)
							durationInSeconds = peekAtNextEvent.relativePositionInSeconds - wtfYayEvent.relativePositionInSeconds
							println "[Calculate] Experiment duration (peek): "+durationInSeconds
						} else {
							durationInSeconds = troubleshootingBand.relativeEnd - wtfYayEvent.relativePositionInSeconds
							println "[Calculate] Experiment duration (band-end): "+durationInSeconds
						}

						Experiment experiment = new Experiment(wtfYayEvent, durationInSeconds);
						journey.addExperiment(experiment);
					}
			}

			journeyList.add(journey)
		}

		return journeyList

	}
}
