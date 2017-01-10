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

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.ideaflow.IdeaFlowSubtaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.journey.*
import org.openmastery.publisher.ideaflow.timeline.IdeaFlowTimelineSplitter

class IdeaFlowStoryGenerator {

	TroubleshootingJourneyGenerator journeyGenerator = new TroubleshootingJourneyGenerator()

	IdeaFlowStory generateIdeaFlowStory(IdeaFlowTaskTimeline taskTimeline) {

		IdeaFlowStory taskStory = new IdeaFlowStory(taskTimeline.task, taskTimeline)

		List<IdeaFlowSubtaskTimeline> subtaskTimelines = splitTimelineBySubtaskEvents(taskTimeline)
		taskStory.subtasks = generateSubtaskStories(taskStory.relativePath, subtaskTimelines)

		return taskStory
	}

	IdeaFlowStory generateIdeaFlowStoryScopedToSubtask(IdeaFlowTaskTimeline taskTimeline, IdeaFlowSubtaskTimeline subtaskTimeline) {
		IdeaFlowStory taskStory = new IdeaFlowStory(taskTimeline.task, taskTimeline)
		taskStory.subtasks = generateSubtaskStories(taskStory.relativePath, [subtaskTimeline])
		return taskStory
	}

	private List<SubtaskStory> generateSubtaskStories(String parentPath, List<IdeaFlowSubtaskTimeline> subtaskTimelines) {

		subtaskTimelines.collect() { IdeaFlowSubtaskTimeline subtaskTimeline ->
			SubtaskStory subtaskStory = new SubtaskStory(parentPath, subtaskTimeline.subtask, subtaskTimeline)
			subtaskStory.milestones = generateProgressMilestones(subtaskStory.fullPath, subtaskTimeline)
			subtaskStory.troubleshootingJourneys = journeyGenerator.createFromTimeline(subtaskTimeline);

			subtaskStory.troubleshootingJourneys.each { TroubleshootingJourney journey ->
				journey.setParentPath(subtaskStory.getFullPath())
			}

			return subtaskStory
		}
	}

	private List<IdeaFlowSubtaskTimeline> splitTimelineBySubtaskEvents(IdeaFlowTaskTimeline timeline) {
		return new IdeaFlowTimelineSplitter()
				.timeline(timeline)
				.splitBySubtaskEvents()
	}

	private List<ProgressMilestone> generateProgressMilestones(String parentPath, IdeaFlowSubtaskTimeline subtaskTimeline) {
		Long relativeStart = subtaskTimeline.relativeStart
		Long relativeEnd = subtaskTimeline.relativeEnd

		List<Event> progressNotes = subtaskTimeline.events.findAll { Event event ->
			event.type == EventType.NOTE
		}

		ProgressMilestone lastMilestone = null
		List<ProgressMilestone> progressMilestones = []

		Event defaultEvent = subtaskTimeline.subtask
		ProgressMilestone defaultMilestone = new ProgressMilestone(parentPath, defaultEvent)
		defaultMilestone.durationInSeconds = relativeEnd - relativeStart
		progressMilestones.add(defaultMilestone)

		lastMilestone = defaultMilestone

		progressNotes.each { Event progressNote ->
			ProgressMilestone milestone = new ProgressMilestone(parentPath, progressNote)
			progressMilestones.add(milestone)

			lastMilestone.durationInSeconds = progressNote.relativePositionInSeconds - lastMilestone.relativePositionInSeconds
			lastMilestone = milestone
		}

		lastMilestone.durationInSeconds = relativeEnd - lastMilestone.relativePositionInSeconds

		if (progressMilestones.size() == 1) {
			progressMilestones = []
		}

		return progressMilestones
	}

}
