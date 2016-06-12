package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaFlowBand {

	private long id;
	private Long taskId;

	private LocalDateTime start;
	private LocalDateTime end;
	private Long duration;

	private String startingComment;
	private String endingComent;

	private IdeaFlowStateType type;

	private List<IdeaFlowBand> nestedBands = new ArrayList<IdeaFlowBand>();

}


