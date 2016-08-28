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
		int index = activityNodeIndex++

		assert getActivityNode(index).type == ActivityNodeType.EDITOR
		assert getActivityNode(index).editorFilePath == filePath
		assert getActivityNode(index).editorFileName == new File(filePath).name
		assert getActivityNode(index).editorFileIsModified == isModified
		assert getActivityNode(index).editorDurationInSeconds == durationInSeconds.seconds
		assert getActivityNode(index).relativePositionInSeconds == relativePosition
	}

	void assertExternalActivity(Long relativePosition, String comment, Duration durationInSeconds) {
		int index = activityNodeIndex++

		assert getActivityNode(index).type == ActivityNodeType.EXTERNAL
		assert getActivityNode(index).externalIdle == false
		assert getActivityNode(index).externalComment == comment
		assert getActivityNode(index).externalDurationInSeconds == durationInSeconds.seconds
		assert getActivityNode(index).relativePositionInSeconds == relativePosition
	}

	void assertBandStart(Long relativePosition, IdeaFlowStateType bandType, String bandComment) {
		int index = activityNodeIndex++

		assert getActivityNode(index).type == ActivityNodeType.BAND
		assert getActivityNode(index).bandStart == true
		assert getActivityNode(index).bandStateType == bandType
		assert getActivityNode(index).bandComment == bandComment
		assert getActivityNode(index).relativePositionInSeconds == relativePosition
	}

	void assertBandEnd(Long relativePosition, IdeaFlowStateType bandType, String bandComment) {
		int index = activityNodeIndex++

		assert getActivityNode(index).type == ActivityNodeType.BAND
		assert getActivityNode(index).bandStart == false
		assert getActivityNode(index).bandStateType == bandType
		assert getActivityNode(index).bandComment == bandComment
		assert getActivityNode(index).relativePositionInSeconds == relativePosition
	}

	void assertIdleActivity(Long relativePosition, Duration durationInSeconds) {
		int index = activityNodeIndex++

		assert getActivityNode(index).type == ActivityNodeType.EXTERNAL
		assert getActivityNode(index).externalIdle == true
		assert getActivityNode(index).externalDurationInSeconds == durationInSeconds.seconds
		assert getActivityNode(index).relativePositionInSeconds == relativePosition
	}

	ActivityNode getActivityNode(int index) {
		return activityTimeline.activityNodes[index]
	}

	void assertValidationComplete() {
		assert activityTimeline.activityNodes.size() == activityNodeIndex
	}

}
