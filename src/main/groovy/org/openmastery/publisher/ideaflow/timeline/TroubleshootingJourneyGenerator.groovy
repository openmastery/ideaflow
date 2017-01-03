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

import groovy.util.logging.Slf4j
import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.event.ExecutionEvent
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.metrics.subtask.MetricsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Generates all the troubleshooting journeys from the WTF/YAY events within the timeline
 */
@Component
@Slf4j
class TroubleshootingJourneyGenerator {

	@Autowired
	MetricsService metricsService

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

			journey.metrics = metricsService.generateJourneyMetrics(new JourneyTimeline(journey))

		}

		return journeys
	}

	void annotateJourneys(List<TroubleshootingJourney> journeys, List<FaqAnnotationEntity> faqs, List<SnippetAnnotationEntity> snippets) {

		journeys.each { TroubleshootingJourney journey ->
			faqs.each { FaqAnnotationEntity faqEntity ->
				if (journey.containsEvent(faqEntity.eventId)) {
					journey.addFAQ(faqEntity.eventId, faqEntity.comment)
				}
			}

			snippets.each { SnippetAnnotationEntity snippetEntity ->
				if (journey.containsEvent(snippetEntity.eventId)) {
					journey.addSnippet(snippetEntity.eventId, snippetEntity.source, snippetEntity.snippet)
				}
			}
		}

	}

	List<TroubleshootingJourney> splitIntoJourneys(List<Event> events, List<IdeaFlowBand> bands, List<ExecutionEvent> executionEvents) {
		List<Event> wtfYayEvents = events.findAll { Event event ->
			event.type == EventType.WTF || event.type == EventType.AWESOME
		}

		List<TroubleshootingJourney> journeyList = splitIntoJourneys(wtfYayEvents, bands)

		journeyList.each { TroubleshootingJourney journey ->
			journey.fillWithActivity(executionEvents)
		}

		return journeyList
	}


	List<TroubleshootingJourney> splitIntoJourneys(List<Event> wtfYayEvents, List<IdeaFlowBand> troubleshootingBands) {
		List<TroubleshootingJourney> journeyList = []

		troubleshootingBands.each { IdeaFlowBand troubleshootingBand ->
			TroubleshootingJourney journey = new TroubleshootingJourney(troubleshootingBand)
			log.debug("Generating Journey [" + journey.relativeStart + ", " + journey.relativeEnd + "]")

			for (int activeIndex = 0; activeIndex < wtfYayEvents.size(); activeIndex++) {
				Event wtfYayEvent = wtfYayEvents.get(activeIndex)
				Long eventPosition = wtfYayEvent.relativePositionInSeconds

				if (eventPosition >= journey.relativeStart && eventPosition <= journey.relativeEnd) {

					Long durationInSeconds = 0;
					if (wtfYayEvents.size() > activeIndex + 1) {
						Event peekAtNextEvent = wtfYayEvents.get(activeIndex + 1)
						durationInSeconds = peekAtNextEvent.relativePositionInSeconds - wtfYayEvent.relativePositionInSeconds
					} else {
						durationInSeconds = troubleshootingBand.relativeEnd - wtfYayEvent.relativePositionInSeconds
					}
					log.debug("Adding event: "+wtfYayEvent.id + "{position: "+wtfYayEvent.relativePositionInSeconds+", duration: "+durationInSeconds+"}")
					journey.addPartialDiscovery(wtfYayEvent, durationInSeconds);
				}
			}

			journeyList.add(journey)
		}

		return journeyList

	}


}
