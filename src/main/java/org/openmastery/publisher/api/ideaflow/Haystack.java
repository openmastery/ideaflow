package org.openmastery.publisher.api.ideaflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.activity.EditorActivity;
import org.openmastery.publisher.api.event.ExecutionEvent;
import org.openmastery.publisher.api.journey.ExperimentCycle;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class Haystack {

	private ExecutionEvent executionEvent;

	@JsonIgnore
	private Map<String, ActivitySummary> activitySummaryMap;

	public Haystack() {
		activitySummaryMap = new HashMap<>();
	}

	public void addEditorActivity(EditorActivity editorActivity) {
		ActivitySummary summary = activitySummaryMap.get(editorActivity.getFilePath());
		if (summary == null) {
			summary = new ActivitySummary(editorActivity);
			activitySummaryMap.put(editorActivity.getFilePath(), summary);
		} else {
			summary.aggregateWith(editorActivity);
		}
	}

	public List<ActivitySummary> getActivitySummary() {
		List<ActivitySummary> summaryList = new ArrayList<>(activitySummaryMap.values());
		Collections.sort(summaryList, new Comparator<ActivitySummary>() {
			final int BEFORE = -1;
			final int EQUAL = 0;
			final int AFTER = 1;

			@Override
			public int compare(ActivitySummary summary1, ActivitySummary summary2) {
				if (summary1.getDurationInSeconds() < summary2.getDurationInSeconds()) {
					return BEFORE;
				} else if (summary1.getDurationInSeconds() > summary2.getDurationInSeconds()) {
					return AFTER;
				} else {
					return EQUAL;
				}
			}
		});
		return summaryList;
	}
}
