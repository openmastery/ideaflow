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
import org.openmastery.publisher.api.ideaflow.SubtaskTimelineOverview;
import org.openmastery.publisher.api.ideaflow.TaskTimelineOverview;
import org.openmastery.publisher.api.ideaflow.TaskTimelineWithAllSubtasks;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class TimelineClient extends IdeaFlowClient<Object, TimelineClient> {

	public TimelineClient(String hostUri) {
		super(hostUri, ResourcePaths.IDEAFLOW_PATH, Object.class);
	}

	public TaskTimelineOverview getTimelineOverviewForTask(long taskId) {
		return (TaskTimelineOverview) getUntypedCrudClientRequest()
				.path(ResourcePaths.IDEAFLOW_TIMELINE)
				.path(ResourcePaths.IDEAFLOW_TASK)
				.path(taskId)
				.entity(TaskTimelineOverview.class)
				.find();
	}

	public TaskTimelineWithAllSubtasks getTimelineOverviewForTaskWithAllSubtasks(long taskId) {
		return (TaskTimelineWithAllSubtasks) getUntypedCrudClientRequest()
				.path(ResourcePaths.IDEAFLOW_TIMELINE)
				.path(ResourcePaths.IDEAFLOW_TASK)
				.path(taskId)
				.path(ResourcePaths.IDEAFLOW_FULL)
				.entity(TaskTimelineWithAllSubtasks.class)
				.find();
	}

	public SubtaskTimelineOverview getTimelineOverviewForSubtask(long taskId, long subtaskId) {
		return (SubtaskTimelineOverview) getUntypedCrudClientRequest()
				.path(ResourcePaths.IDEAFLOW_TIMELINE)
				.path(ResourcePaths.IDEAFLOW_TASK)
				.path(taskId)
				.path(ResourcePaths.IDEAFLOW_SUBTASK)
				.path(subtaskId)
				.entity(SubtaskTimelineOverview.class)
				.find();
	}

}
