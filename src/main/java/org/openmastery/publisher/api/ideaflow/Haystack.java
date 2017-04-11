package org.openmastery.publisher.api.ideaflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.journey.ExperimentCycle;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Haystack {

	private ExperimentCycle experimentCycle;

	private List<EditorActivity> editorActivityList;
}
