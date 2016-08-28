package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.timeline.ActivityNode
import org.openmastery.publisher.api.timeline.ActivityNodeType
import org.openmastery.publisher.api.timeline.ActivityTimeline
import org.openmastery.publisher.api.timeline.BandTimeline

import java.time.Duration


class ActivityTimelineValidator {

	ActivityTimeline activityTimeline

	private int activityNodeIndex = 0;

	private int expectedBandActivityCount = 0
	private int expectedEditorActivityCount = 0

	ActivityTimelineValidator(ActivityTimeline activityTimeline) {
		this.activityTimeline = activityTimeline
	}

	void assertFileActivity(Long relativePosition, String filePath, boolean isModified, Duration durationInSeconds) {
		ActivityNode activityNode = activityTimeline.activityNodes[activityNodeIndex++]

		assert activityNode.type == ActivityNodeType.FILE
		assert activityNode.filePath == filePath
		assert activityNode.fileIsModified == isModified
		assert activityNode.fileDurationInSeconds == durationInSeconds.seconds
		assert activityNode.relativePositionInSeconds == relativePosition
	}

	void assertBandStart(Long relativePosition, IdeaFlowStateType bandType, String bandComment) {
		ActivityNode activityNode = activityTimeline.activityNodes[activityNodeIndex++]

		assert activityNode.type == ActivityNodeType.BAND
		assert activityNode.bandStart == true
		assert activityNode.bandStateType == bandType
		assert activityNode.bandComment == bandComment
		assert activityNode.relativePositionInSeconds == relativePosition
	}

	void assertBandEnd(Long relativePosition, IdeaFlowStateType bandType, String bandComment) {
		ActivityNode activityNode = activityTimeline.activityNodes[activityNodeIndex++]

		assert activityNode.type == ActivityNodeType.BAND
		assert activityNode.bandStart == false
		assert activityNode.bandStateType == bandType
		assert activityNode.bandComment == bandComment
		assert activityNode.relativePositionInSeconds == relativePosition
	}

	void assertValidationComplete() {
		assert activityTimeline.activityNodes.size() == activityNodeIndex
	}
}
