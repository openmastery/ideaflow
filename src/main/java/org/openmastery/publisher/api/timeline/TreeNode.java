package org.openmastery.publisher.api.timeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNode implements Comparable<TreeNode> {

	private int indentLevel;
	private String id;
	private Long relativeStart;
	private LocalDateTime start;
	private LocalDateTime end;
	private TreeNodeType type;
	private Long duration;

	private String startingComment;
	private String endingComment;
	private IdeaFlowStateType bandType;

	@Override
	public int compareTo(TreeNode o) {
		return relativeStart.compareTo(o.relativeStart);
	}

}
