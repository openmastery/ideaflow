package org.openmastery.publisher.api.timeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNode implements Comparable<TreeNode> {

	private int indentLevel;
	private String id;
	private TreeNodeType type;
	private LocalDateTime start;
	private LocalDateTime end;
	private Long durationInSeconds;
	private Long relativePositionInSeconds;

	private String startingComment;
	private String endingComment;
	private IdeaFlowStateType bandType;

	@Override
	public int compareTo(TreeNode o) {
		return relativePositionInSeconds.compareTo(o.relativePositionInSeconds);
	}

}
