package org.openmastery.publisher.core.timeline

import org.openmastery.publisher.api.event.Event
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.api.timeline.TreeNode
import org.openmastery.publisher.api.timeline.TreeTimeline
import org.openmastery.publisher.api.timeline.TreeNodeType

public class TreeTimelineBuilder {

	private int indentLevel = 0
	private List<TreeNode> treeNodes = []

	public TreeTimelineBuilder addTimelineSegments(List<BandTimelineSegment> segments) {
		for (BandTimelineSegment segment : segments) {
			addTimelineSegment(segment)
		}
		this
	}

	public TreeTimelineBuilder addTimelineSegment(BandTimelineSegment segment) {
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

	private void addTimeBandNodes(List<? extends TimeBandModel> timeBands) {
		indentLevel++
		for (TimeBandModel timeBand : timeBands) {
			addTimeBandNode(timeBand)
		}
		indentLevel--
	}

	private void addTimeBandNode(TimeBandModel timeBand) {
		if (timeBand instanceof IdeaFlowBandModel) {
			IdeaFlowBandModel ideaFlowBand = timeBand as IdeaFlowBandModel
			TreeNode node = createTreeNode(ideaFlowBand)
			treeNodes.add(node)
			addTimeBandNodes(ideaFlowBand.nestedBands)
		} else if (timeBand instanceof TimeBandGroupModel) {
			TimeBandGroupModel timeBandGroup = timeBand as TimeBandGroupModel
			TreeNode node = createTreeNode(timeBandGroup)
			treeNodes.add(node)
			addTimeBandNodes(timeBandGroup.linkedTimeBands)
		} else {
			throw new RuntimeException("Unknown time band type=${timeBand}")
		}
	}

	private TreeNode createTreeNode(BandTimelineSegment segment) {
		return TreeNode.builder()
				.id(segment.id as String)
				.indentLevel(indentLevel)
				.type(TreeNodeType.SEGMENT)
				.start(segment.start)
				.end(segment.end)
				.relativeStart(segment.relativeStart)
				.startingComment(segment.description)
				.duration(segment.duration.seconds)
				.build()
	}

	private TreeNode createTreeNode(IdeaFlowBandModel ideaFlowBand) {
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
				.duration(ideaFlowBand.duration.seconds)
				.build()
	}

	private TreeNode createTreeNode(TimeBandGroupModel timeBand) {
		return TreeNode.builder()
				.id(timeBand.id)
				.indentLevel(indentLevel)
				.type(TreeNodeType.TIME_BAND_GROUP)
				.start(timeBand.start)
				.end(timeBand.end)
				.relativeStart(timeBand.relativeStart)
				.duration(timeBand.duration.seconds)
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
				.startingComment(event.comment)
				.build()
	}

}
