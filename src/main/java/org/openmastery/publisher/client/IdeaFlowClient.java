/**
 * Copyright 2015 New Iron Group, Inc.
 * <p>
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/gpl-3.0.en.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.client;

import org.openmastery.publisher.api.ResourcePaths;
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline;
import org.openmastery.publisher.api.metrics.TimelineMetrics;

import java.util.List;

public class IdeaFlowClient extends  OpenMasteryClient<Object, IdeaFlowClient> {

	public IdeaFlowClient(String hostUri) {
		super(hostUri, ResourcePaths.IDEAFLOW_PATH, Object.class);
	}

	public IdeaFlowTimeline getTimelineForTask(long taskId) {
		return (IdeaFlowTimeline) getUntypedCrudClientRequest()
				.path(ResourcePaths.IDEAFLOW_TIMELINE + ResourcePaths.IDEAFLOW_TASK)
				.path(taskId)
				.entity(IdeaFlowTimeline.class)
				.find();
	}

	public List<TimelineMetrics> generateRiskSummariesBySubtask(long taskId) {
		return (List<TimelineMetrics>) getUntypedCrudClientRequest()
				.path(ResourcePaths.IDEAFLOW_METRICS + ResourcePaths.IDEAFLOW_TASK)
				.path(taskId)
				.entity(TimelineMetrics.class)
				.findMany();
	}

}
