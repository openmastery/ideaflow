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
import org.openmastery.publisher.api.journey.DiscoveryCycle
import org.openmastery.publisher.api.journey.ExperimentCycle
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.openmastery.publisher.metrics.MetricService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Generates all the troubleshooting journeys from the WTF/YAY events within the timeline
 */
@Component
@Slf4j
class TroubleshootingJourneyGenerator {

	@Autowired
	MetricService metricsService

	TroubleshootingJourney createJourney(List<Event> events, IdeaFlowBand band) {
		List<Event> wtfYayEvents = events.findAll { it.type == EventType.WTF }
		return splitIntoJourneys(wtfYayEvents, [band]).first()
	}

	TroubleshootingJourney createJourney(List<Event> events, IdeaFlowBand band, List<ExecutionEvent> executionEvents) {
		List<Event> wtfYayEvents = events.findAll { it.type == EventType.WTF }
		List<TroubleshootingJourney> journeys = splitIntoJourneys(wtfYayEvents, [band])

		TroubleshootingJourney journey = journeys.first()
		fillWithActivity(journey, executionEvents)

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
			fillWithActivity(journey, timeline.executionEvents)

			journey.metrics = metricsService.generateJourneyMetrics(new JourneyTimeline(journey))

		}

		return journeys
	}

	void fillWithActivity(TroubleshootingJourney journey, List<ExecutionEvent> executionEvents) {
		journey.getDiscoveryCycles().each { DiscoveryCycle discoveryCycle ->
			log.debug("Populating Discovery Cycle [" + discoveryCycle.relativeStart + " : " + discoveryCycle.relativeEnd + "]")

			ExecutionEvent lastExecutionEvent = null
			for (ExecutionEvent executionEvent : executionEvents) {
				if (discoveryCycle.shouldContain(executionEvent)) {


					if (lastExecutionEvent != null && discoveryCycle.experimentCycles.size() == 0) {
						addShortenedExecutionContext(discoveryCycle, lastExecutionEvent, executionEvent)
					}

					addExecutionEvent(discoveryCycle, executionEvent)
				}
				lastExecutionEvent = executionEvent
			}
		}
	}

	private void addShortenedExecutionContext(DiscoveryCycle discoveryCycle, ExecutionEvent contextEvent, ExecutionEvent firstEvent) {
		long initialDuration = firstEvent.relativePositionInSeconds - discoveryCycle.relativeStart

		if (initialDuration > 0) {
			ExperimentCycle experimentCycle = new ExperimentCycle(contextEvent, initialDuration)
			experimentCycle.relativeStart = discoveryCycle.relativeStart

			log.debug("Context Exec [${contextEvent.id}, ${experimentCycle.relativeStart} ] : "+initialDuration)

			discoveryCycle.addExperimentCycle(experimentCycle)
		}
	}

	private void addExecutionEvent(DiscoveryCycle discoveryCycle, ExecutionEvent event) {
		//execution starts somewhere in the middle, could extend beyond end (truncate)
		if (discoveryCycle.experimentCycles.size() > 0) {
			ExperimentCycle last = discoveryCycle.experimentCycles.last()
			last.durationInSeconds = event.relativePositionInSeconds - last.relativeStart
		}
		long duration = discoveryCycle.relativeEnd - event.relativePositionInSeconds
		ExperimentCycle experimentCycle = new ExperimentCycle(event, duration)

		log.debug("Adding Exec [${event.id}, ${event.relativePositionInSeconds} ] : "+duration)
		discoveryCycle.addExperimentCycle(experimentCycle)
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
			fillWithActivity(journey, executionEvents)
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

				if ((wtfYayEvent.type == EventType.WTF && eventPosition >= journey.relativeStart && eventPosition < journey.relativeEnd) ||
						(wtfYayEvent.type == EventType.AWESOME && eventPosition > journey.relativeStart && eventPosition <= journey.relativeEnd)) {

					Long durationInSeconds = 0;
					if (wtfYayEvents.size() > activeIndex + 1 && wtfYayEvents.get(activeIndex + 1).relativePositionInSeconds < troubleshootingBand.relativeEnd) {
						Event peekAtNextEvent = wtfYayEvents.get(activeIndex + 1)

						durationInSeconds = peekAtNextEvent.relativePositionInSeconds - wtfYayEvent.relativePositionInSeconds
					} else {
						durationInSeconds = troubleshootingBand.relativeEnd - wtfYayEvent.relativePositionInSeconds
					}
					log.debug("Adding event: " + wtfYayEvent.type + ":" + wtfYayEvent.id + " {position: " + wtfYayEvent.relativePositionInSeconds + ", duration: " + durationInSeconds + "}")
					journey.addPartialDiscovery(wtfYayEvent, durationInSeconds);

				}
			}

			journeyList.add(journey)
		}

		return journeyList

	}


}
