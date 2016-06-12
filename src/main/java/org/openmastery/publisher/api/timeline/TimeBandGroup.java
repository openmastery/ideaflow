package org.openmastery.publisher.api.timeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.ideaflow.IdeaFlowBand;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeBandGroup {

	private String id;
	private long taskId;

	private LocalDateTime start;
	private LocalDateTime end;
	private Long duration;

	private List<IdeaFlowBand> linkedTimeBands;

}
