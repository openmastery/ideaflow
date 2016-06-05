package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.ResourcePaths;
import org.ideaflow.publisher.api.event.Event;
import org.ideaflow.publisher.api.event.EventType;
import org.ideaflow.publisher.api.ideaflow.IdeaFlowBand;
import org.ideaflow.publisher.api.timeline.TimeBand;
import org.ideaflow.publisher.api.timeline.TimeBandGroup;
import org.ideaflow.publisher.api.timeline.Timeline;
import org.ideaflow.publisher.api.timeline.TimelineSegment;
import org.ideaflow.publisher.api.timeline.TreeNode;
import org.ideaflow.publisher.api.timeline.TreeNodeType;
import org.ideaflow.publisher.api.timeline.TreeTimeline;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Path("/stubtimeline")
@Produces(MediaType.APPLICATION_JSON)
public class StubTimelineResource {

	@GET
	@Path(ResourcePaths.TIMELINE_NATURAL_PATH + ResourcePaths.TASK_PATH + "/{taskId}")
	public Timeline getTimelineForTask(@PathParam("taskId") String taskId) {
		TestDataSupport support = new TestDataSupport();
		support.disableTimelineSplitter();
		return support.createTimeline(taskId);
	}

	@GET
	@Path(ResourcePaths.TIMELINE_TREE_PATH + ResourcePaths.TASK_PATH + "/{taskId}")
	public TreeTimeline getTimelineTreeForTask(@PathParam("taskId") String taskId) {
		TestDataSupport support = new TestDataSupport();
		Timeline timeline = support.createTimeline(taskId);

		TreeTimelineBuilder treeTimelineBuilder = new TreeTimelineBuilder(timeline);
		return treeTimelineBuilder.build();
	}

	@GET
	@Path(ResourcePaths.TASK_PATH)
	public List<String> getTimelineForTask() {
		TestDataSupport support = new TestDataSupport();
		return support.getTaskIds();
	}


	private static class TreeTimelineBuilder {

		private Timeline timeline;
		private int indentLevel;
		private List<TreeNode> treeNodes = new ArrayList<>();

		public TreeTimelineBuilder(Timeline timeline) {
			this.timeline = timeline;
			this.indentLevel = 0;
		}

		public TreeTimeline build() {
			for (TimelineSegment segment : timeline.getTimelineSegments()) {
				TreeNode node = createTreeNode(segment);
				treeNodes.add(node);

				addTimeBandNodes(segment.getAllTimeBands());
				addNonSubtaskEvents(segment.getEvents());
			}

			sortTreeNodesAndSetEventIndentLevel();

			return TreeTimeline.builder()
					.treeNodes(treeNodes)
					.build();
		}

		private void sortTreeNodesAndSetEventIndentLevel() {
			Collections.sort(treeNodes);

			int indentLevel = 0;
			for (TreeNode node : treeNodes) {
				if (node.getType() == TreeNodeType.EVENT) {
					node.setIndentLevel(indentLevel);
				} else {
					indentLevel = node.getIndentLevel();
				}
			}
		}

		private void addNonSubtaskEvents(List<Event> events) {
			for (Event event : events) {
				if (event.getEventType() != EventType.SUBTASK) {
					TreeNode node = createTreeNode(event);
					treeNodes.add(node);
				}
			}
		}

		private void addTimeBandNodes(List<? extends TimeBand> timeBands) {
			indentLevel++;
			for (TimeBand timeBand : timeBands) {
				addTimeBandNodes(timeBand);
			}
			indentLevel--;
		}

		private void addTimeBandNodes(TimeBand timeBand) {
			if (timeBand instanceof IdeaFlowBand) {
				IdeaFlowBand ideaFlowBand = (IdeaFlowBand) timeBand;
				TreeNode node = createTreeNode(ideaFlowBand);
				treeNodes.add(node);
				addTimeBandNodes(ideaFlowBand.getNestedBands());
			} else if (timeBand instanceof TimeBandGroup) {
				TimeBandGroup timeBandGroup = (TimeBandGroup) timeBand;
				TreeNode node = createTreeNode(timeBandGroup);
				treeNodes.add(node);
				addTimeBandNodes(timeBandGroup.getLinkedTimeBands());
			} else {
				throw new RuntimeException("Unknown time band type=" + timeBand);
			}
		}

		private TreeNode createTreeNode(TimelineSegment segment) {
			return TreeNode.builder()
					.indentLevel(indentLevel)
					.type(TreeNodeType.SEGMENT)
					.start(segment.getStart())
					.end(segment.getEnd())
					.relativeStart(segment.getRelativeStart())
					.startingComment(segment.getDescription())
					.duration(segment.getDuration())
					.build();
		}

		private TreeNode createTreeNode(IdeaFlowBand ideaFlowBand) {
			return TreeNode.builder()
					.id(Long.toString(ideaFlowBand.getId()))
					.indentLevel(indentLevel)
					.type(TreeNodeType.IDEA_FLOW_BAND)
					.start(ideaFlowBand.getStart())
					.end(ideaFlowBand.getEnd())
					.relativeStart(ideaFlowBand.getRelativeStart())
					.startingComment(ideaFlowBand.getStartingComment())
					.endingComment(ideaFlowBand.getEndingComent())
					.bandType(ideaFlowBand.getType())
					.duration(ideaFlowBand.getDuration())
					.build();
		}

		private TreeNode createTreeNode(TimeBandGroup timeBand) {
			return TreeNode.builder()
					.id(timeBand.getId())
					.indentLevel(indentLevel)
					.type(TreeNodeType.TIME_BAND_GROUP)
					.start(timeBand.getStart())
					.end(timeBand.getEnd())
					.relativeStart(timeBand.getRelativeStart())
					.duration(timeBand.getDuration())
					.build();
		}

		private TreeNode createTreeNode(Event event) {
			return TreeNode.builder()
					.id(Long.toString(event.getId()))
					.indentLevel(indentLevel)
					.type(TreeNodeType.EVENT)
					.start(event.getPosition())
					.end(event.getPosition())
					.relativeStart(event.getRelativeStart())
					.build();
		}

	}

}
