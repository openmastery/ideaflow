/*
 * Copyright 2016 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.publisher.core.ideaflow

import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.ideaflow.ModificationActivity
import org.openmastery.publisher.core.IdeaFlowPersistenceService
import org.openmastery.publisher.core.activity.ModificationActivityEntity
import org.springframework.beans.factory.annotation.Autowired


class IdeaFlowService {

	@Autowired
	private IdeaFlowPersistenceService persistenceService;

	IdeaFlowTimeline generateIdeaFlowForTask(Long taskId) {
			IdeaFlowTimeline ideaFlow = new IdeaFlowTimeline()
			//ideaFlow.task = //fetch from TaskService

		List<ModificationActivityEntity> modificationList = persistenceService.getModificationActivityList(taskId)

	}

}
