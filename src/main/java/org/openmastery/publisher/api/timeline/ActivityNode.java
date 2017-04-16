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
public class ActivityNode implements Comparable<ActivityNode> {

	private LocalDateTime position;
	private Long relativePositionInSeconds;
	private ActivityNodeType type;

	// Event
	private String eventComment;

	// Band
	private String bandComment;
	private IdeaFlowStateType bandStateType;
	private Boolean bandStart;

	// Editor
	private String editorFileName;
	private String editorFilePath;
	private Boolean editorFileIsModified;
	private Long editorDurationInSeconds;

	// External
	private Boolean externalIdle;
	private String externalComment;
	private Long externalDurationInSeconds;

	@Override
	public int compareTo(ActivityNode other) {
		return position.compareTo(other.position);
	}

}
