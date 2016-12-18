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
package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.timeline.ActivityNode
import org.openmastery.publisher.api.timeline.ActivityNodeType
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.api.Positionable
import org.openmastery.publisher.core.activity.ActivityModel
import org.openmastery.publisher.core.activity.EditorActivityModel
import org.openmastery.publisher.core.activity.ExternalActivityModel
import org.openmastery.publisher.core.event.EventModel
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel

import static org.openmastery.time.TimeConverter.toJodaLocalDateTime

class ActivityTimelineBuilder {

	private List<ActivityNode> activityNodes = []

	public ActivityTimeline build() {
		Collections.sort(activityNodes)

		ActivityTimeline.builder()
				.activityNodes(activityNodes)
				.build()
	}

	public ActivityTimelineBuilder addTimelineSegment(BandTimelineSegment segment) {
		addIdeaFlowBandActivityNodes(segment.ideaFlowBands)
		addTimeBandGroupActivityNodes(segment.timeBandGroups)
		addEventActivityNodes(segment.events)
		addActivityNodes(segment.activities)
		this
	}

	private void addActivityNodes(List<ActivityModel> activities) {
		for (ActivityModel activity : activities) {
			ActivityNode activityNode

			if (activity instanceof EditorActivityModel) {
				EditorActivityModel editorActivity = (EditorActivityModel) activity
				activityNode = createActivityNodeBuilder(activity, ActivityNodeType.EDITOR)
						.editorFilePath(editorActivity.filePath)
						.editorFileName(editorActivity.fileName)
						.editorFileIsModified(editorActivity.modified)
						.editorDurationInSeconds(editorActivity.duration.seconds)
						.build()
			} else if (activity instanceof ExternalActivityModel) {
				ExternalActivityModel externalActivity = (ExternalActivityModel) activity
				activityNode = createActivityNodeBuilder(activity, ActivityNodeType.EXTERNAL)
						.externalIdle(false)
						.externalDurationInSeconds(externalActivity.duration.seconds)
						.externalComment(externalActivity.comment)
						.build()
			} else {
				throw new RuntimeException("Application error - unexpected activity type=${activity}")
			}

			activityNodes << activityNode
		}
	}

	private void addEventActivityNodes(List<EventModel> events) {
		for (EventModel event : events) {
			ActivityNode eventNode = createActivityNodeBuilder(event, ActivityNodeType.EVENT)
					.eventComment(event.comment)
					.build()
			activityNodes << eventNode
		}
	}

	private void addTimeBandGroupActivityNodes(List<TimeBandGroupModel> timeBandGroups) {
		for (TimeBandGroupModel timeBandGroup : timeBandGroups) {
			addIdeaFlowBandActivityNodes(timeBandGroup.linkedTimeBands)
		}
	}

	private void addIdeaFlowBandActivityNodes(List<IdeaFlowBandModel> ideaFlowBands) {
		for (IdeaFlowBandModel ideaFlowBand : ideaFlowBands) {
			addStartAndEndActivityBandNodes(ideaFlowBand)
		}
	}

	private void addStartAndEndActivityBandNodes(IdeaFlowBandModel ideaFlowBand) {
		if (ideaFlowBand.type != IdeaFlowStateType.PROGRESS) {
			ActivityNode startNode = createActivityNodeBuilder(ideaFlowBand, ActivityNodeType.BAND)
					.bandComment(ideaFlowBand.startingComment)
					.bandStateType(ideaFlowBand.type)
					.bandStart(true)
					.build()
			activityNodes << startNode

			ActivityNode endNode = ActivityNode.builder()
					.type(ActivityNodeType.BAND)
					.position(toJodaLocalDateTime(ideaFlowBand.end))
					.relativePositionInSeconds(ideaFlowBand.relativePositionInSeconds + ideaFlowBand.duration.seconds)
					.bandComment(ideaFlowBand.endingComent)
					.bandStateType(ideaFlowBand.type)
					.bandStart(false)
					.build()
			activityNodes << endNode
		}

		addIdleActivityNodes(ideaFlowBand.idleBands)
		addIdeaFlowBandActivityNodes(ideaFlowBand.nestedBands)
	}

	private void addIdleActivityNodes(List<IdleTimeBandModel> idleBands) {
		for (IdleTimeBandModel idleTimeBand : idleBands) {
			ActivityNode idleNode = createActivityNodeBuilder(idleTimeBand, ActivityNodeType.EXTERNAL)
					.externalDurationInSeconds(idleTimeBand.duration.seconds)
					.externalIdle(true)
					.build()
			activityNodes << idleNode
		}
	}

	private ActivityNode.ActivityNodeBuilder createActivityNodeBuilder(Positionable positionable, ActivityNodeType type) {
		ActivityNode.builder()
				.type(type)
				.position(toJodaLocalDateTime(positionable.position))
				.relativePositionInSeconds(positionable.relativePositionInSeconds)
	}

}
