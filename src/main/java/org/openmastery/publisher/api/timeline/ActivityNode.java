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
public class ActivityNode {

	private LocalDateTime position;
	private Long relativePositionInSeconds;
	private ActivityNodeType type;

	// Event
	private String eventComment;

	// Band
	private String bandComment;
	private IdeaFlowStateType bandStateType;
	private boolean bandStart;

	// File
	private String fileName;
	private String filePath;
	private Long fileDurationInSeconds;

	// External
	private boolean externalIdle;
	private Long externalDurationInSeconds;

}
