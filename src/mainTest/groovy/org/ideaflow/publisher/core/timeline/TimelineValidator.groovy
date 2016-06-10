package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.event.Event
import org.ideaflow.publisher.api.event.EventType
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand
import org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType
import org.ideaflow.publisher.api.timeline.BandTimeline
import org.ideaflow.publisher.api.timeline.TreeNode
import org.ideaflow.publisher.api.timeline.TreeNodeType
import org.ideaflow.publisher.api.timeline.TreeTimeline

import java.time.Duration

import static org.ideaflow.publisher.api.ideaflow.IdeaFlowStateType.PROGRESS

class TimelineValidator {

	private BandTimeline bandTimeline
	private int ideaFlowBandIndex = 0
	private int timeBandGroupIndex = 0
	private int eventIndex = 0
	private TreeTimeline treeTimeline
	private int treeNodeIndex = 0

	private IdeaFlowBand activeTimeBand
	private List<IdeaFlowBand> activeNestedTimeBands = []
	private List<IdeaFlowBand> activeLinkedTimeBands = []

	TimelineValidator(BandTimeline bandTimeline, TreeTimeline treeTimeline) {
		this.bandTimeline = bandTimeline
		this.treeTimeline = treeTimeline
		assertTreeNode(TreeNodeType.SEGMENT, 0)
	}

	private TreeNode assertTreeNode(TreeNodeType expectedType, int expectedIndentLevel) {
		TreeNode treeNode = treeTimeline.treeNodes[treeNodeIndex++]
		assert treeNode.type == expectedType
		assert treeNode.indentLevel == expectedIndentLevel
		treeNode
	}

	private void assertTimeBandValues(IdeaFlowBand actualBand, IdeaFlowStateType expectedType, Duration expectedDuration) {
		assert actualBand.type == expectedType
		assert actualBand.duration == expectedDuration
	}

	private void assertNoActiveLinkedOrNestedTimeBands() {
		assert activeNestedTimeBands.isEmpty()
		assert activeLinkedTimeBands.isEmpty()
	}

	void assertValidationComplete() {
		assert bandTimeline.ideaFlowBands.size() == ideaFlowBandIndex
		assert bandTimeline.timeBandGroups.size() == timeBandGroupIndex
		assert bandTimeline.events.size() == eventIndex
		assert treeTimeline.treeNodes.size() == treeNodeIndex
	}

	void assertSegmentStart() {
		assertTreeNode(TreeNodeType.SEGMENT, 0)
		assertEvent(EventType.SUBTASK)
	}

	void assertSegmentStartAndProgressNode(Duration expectedDuration) {
		assertSegmentStart()
		assertIdeaFlowNode(PROGRESS, expectedDuration)
	}

	void assertEvent(EventType expectedEventType) {
		Event event = bandTimeline.events[eventIndex++]
		assert event.eventType == expectedEventType
	}

	void assertIdeaFlowBand(IdeaFlowStateType expectedType, Duration expectedDuration) {
		assertNoActiveLinkedOrNestedTimeBands()

		IdeaFlowBand ideaFlowBand = bandTimeline.ideaFlowBands[ideaFlowBandIndex++]
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration)
		activeTimeBand = ideaFlowBand
		activeNestedTimeBands = [] + ideaFlowBand.nestedBands
		assertIdeaFlowNodeInternal(ideaFlowBand, 1)
	}

	private void assertIdeaFlowNodeInternal(IdeaFlowBand ideaFlowBand, int expectedIndentLevel) {
		TreeNode treeNode = assertTreeNode(TreeNodeType.IDEA_FLOW_BAND, expectedIndentLevel)
		assert treeNode.startingComment == ideaFlowBand.startingComment
		assert treeNode.endingComment == ideaFlowBand.endingComent
		assert treeNode.bandType == ideaFlowBand.type
	}

	void assertIdeaFlowNode(IdeaFlowStateType expectedType, Duration expectedDuration) {
		TreeNode treeNode = assertTreeNode(TreeNodeType.IDEA_FLOW_BAND, 1)
		assert treeNode.bandType == expectedType
		assert treeNode.duration == expectedDuration
	}

	void assertTimeBandGroupNode(Duration expectedDuration) {
		TreeNode treeNode = assertTreeNode(TreeNodeType.TIME_BAND_GROUP, 1)
		assert treeNode.duration == expectedDuration
	}

	void assertNestedTimeBand(IdeaFlowStateType expectedType, Duration expectedDuration) {
		assert activeLinkedTimeBands.isEmpty()
		assert activeNestedTimeBands.isEmpty() == false
		IdeaFlowBand ideaFlowBand = activeNestedTimeBands.remove(0)
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration)
		assertIdeaFlowNodeInternal(ideaFlowBand, 2)
	}

	void assertLinkedBand(IdeaFlowStateType expectedType, Duration expectedDuration) {
		assert activeNestedTimeBands.isEmpty()

		if (activeLinkedTimeBands.isEmpty()) {
			assert bandTimeline.timeBandGroups[timeBandGroupIndex] != null
			activeLinkedTimeBands = bandTimeline.timeBandGroups[timeBandGroupIndex++].linkedTimeBands
		}
		assert activeLinkedTimeBands.isEmpty() == false
		IdeaFlowBand ideaFlowBand = activeLinkedTimeBands.remove(0)
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration)
		activeNestedTimeBands = [] + ideaFlowBand.nestedBands

		assertIdeaFlowNodeInternal(ideaFlowBand, 2)
	}

	void assertLinkedNestedTimeBand(IdeaFlowStateType expectedType, Duration expectedDuration) {
		assert activeNestedTimeBands.isEmpty() == false
		IdeaFlowBand ideaFlowBand = activeNestedTimeBands.remove(0)
		assertTimeBandValues(ideaFlowBand, expectedType, expectedDuration)
		assertIdeaFlowNodeInternal(ideaFlowBand, 3)
	}

}
