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

import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.ProgressMilestone
import org.openmastery.publisher.api.journey.StoryElement
import org.openmastery.publisher.api.journey.SubtaskStory
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.core.annotation.AnnotationRespository
import org.openmastery.publisher.core.annotation.FaqAnnotationEntity
import org.openmastery.publisher.core.annotation.SnippetAnnotationEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AnnotationDecorator {

	@Autowired
	private AnnotationRespository annotationRespository;

	void decorateStoryWithAnnotations(IdeaFlowStory story) {

		List<FaqAnnotationEntity> faqs = annotationRespository.findFaqAnnotationsByTaskId(story.getId())
		List<SnippetAnnotationEntity> snippets = annotationRespository.findSnippetsByTaskId(story.getId())

		annotateStory(story, faqs, snippets)
	}

	private void annotateStory(IdeaFlowStory story, List<FaqAnnotationEntity> faqs, List<SnippetAnnotationEntity> snippets) {
		story.getSubtasks().each { SubtaskStory subtaskStory ->
			subtaskStory.troubleshootingJourneys.each { TroubleshootingJourney journey ->
					fillJourneyWithAnnotations(journey, faqs, snippets)
			}
		}
	}

	private void fillJourneyWithAnnotations(TroubleshootingJourney journey, List<FaqAnnotationEntity> faqs, List<SnippetAnnotationEntity> snippets) {
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
