package org.ideaflow.publisher.core.timeline

import org.ideaflow.publisher.api.event.Event
import org.ideaflow.publisher.api.event.EventType
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand
import org.ideaflow.publisher.api.timeline.TimeBand
import org.ideaflow.publisher.api.timeline.TimeBandGroup
import org.ideaflow.publisher.api.timeline.Timeline
import org.ideaflow.publisher.api.timeline.TimelineSegment
import org.ideaflow.publisher.api.timeline.TreeNode
import org.ideaflow.publisher.api.timeline.TreeNodeType
import org.ideaflow.publisher.api.timeline.TreeTimeline

public class TreeTimelineBuilder {

	private int indentLevel = 0
	private List<TreeNode> treeNodes = []

	public TreeTimelineBuilder addTimeline(Timeline timeline) {
		for (TimelineSegment segment : timeline.timelineSegments) {
			addTimelineSegment(segment)
		}
		this
	}

	public TreeTimelineBuilder addTimelineSegment(TimelineSegment segment) {
		TreeNode node = createTreeNode(segment)
		treeNodes.add(node)

		addTimeBandNodes(segment.allTimeBands)
		addNonSubtaskEvents(segment.events)
		this
	}

	public TreeTimeline build() {
		sortTreeNodesAndSetEventIndentLevel()

		return TreeTimeline.builder()
				.treeNodes(treeNodes)
				.build()
	}

	private void sortTreeNodesAndSetEventIndentLevel() {
		Collections.sort(treeNodes)

		int indentLevel = 0
		for (TreeNode node : treeNodes) {
			if (node.getType() == TreeNodeType.EVENT) {
				node.setIndentLevel(indentLevel)
			} else {
				indentLevel = node.getIndentLevel()
			}
		}
	}

	private void addNonSubtaskEvents(List<Event> events) {
		for (Event event : events) {
			if (event.getEventType() != EventType.SUBTASK) {
				TreeNode node = createTreeNode(event)
				treeNodes.add(node)
			}
		}
	}

	private void addTimeBandNodes(List<? extends TimeBand> timeBands) {
		indentLevel++
		for (TimeBand timeBand : timeBands) {
			addTimeBandNode(timeBand)
		}
		indentLevel--
	}

	private void addTimeBandNode(TimeBand timeBand) {
		if (timeBand instanceof IdeaFlowBand) {
			IdeaFlowBand ideaFlowBand = timeBand as IdeaFlowBand
			TreeNode node = createTreeNode(ideaFlowBand)
			treeNodes.add(node)
			addTimeBandNodes(ideaFlowBand.nestedBands)
		} else if (timeBand instanceof TimeBandGroup) {
			TimeBandGroup timeBandGroup = timeBand as TimeBandGroup
			TreeNode node = createTreeNode(timeBandGroup)
			treeNodes.add(node)
			addTimeBandNodes(timeBandGroup.linkedTimeBands)
		} else {
			throw new RuntimeException("Unknown time band type=${timeBand}")
		}
	}

	private TreeNode createTreeNode(TimelineSegment segment) {
		return TreeNode.builder()
				.indentLevel(indentLevel)
				.type(TreeNodeType.SEGMENT)
				.start(segment.start)
				.end(segment.end)
				.relativeStart(segment.relativeStart)
				.startingComment(segment.description)
				.duration(segment.duration)
				.build()
	}

	private TreeNode createTreeNode(IdeaFlowBand ideaFlowBand) {
		return TreeNode.builder()
				.id(ideaFlowBand.id as String)
				.indentLevel(indentLevel)
				.type(TreeNodeType.IDEA_FLOW_BAND)
				.start(ideaFlowBand.start)
				.end(ideaFlowBand.end)
				.relativeStart(ideaFlowBand.relativeStart)
				.startingComment(ideaFlowBand.startingComment)
				.endingComment(ideaFlowBand.endingComent)
				.bandType(ideaFlowBand.type)
				.duration(ideaFlowBand.duration)
				.build()
	}

	private TreeNode createTreeNode(TimeBandGroup timeBand) {
		return TreeNode.builder()
				.id(timeBand.id)
				.indentLevel(indentLevel)
				.type(TreeNodeType.TIME_BAND_GROUP)
				.start(timeBand.start)
				.end(timeBand.end)
				.relativeStart(timeBand.relativeStart)
				.duration(timeBand.duration)
				.build()
	}

	private TreeNode createTreeNode(Event event) {
		return TreeNode.builder()
				.id(event.id as String)
				.indentLevel(indentLevel)
				.type(TreeNodeType.EVENT)
				.start(event.position)
				.end(event.position)
				.relativeStart(event.relativeStart)
				.build()
	}

}
