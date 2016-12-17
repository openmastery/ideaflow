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
package org.openmastery.publisher.core.ideaflow.timeline

import org.openmastery.publisher.core.activity.IdleActivityEntity
import org.openmastery.publisher.core.ideaflow.IdeaFlowBandModel
import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.core.timeline.TimeBandIdleCalculator
import org.openmastery.time.TimeConverter

class IdleTimeProcessor {

	private TimeBandIdleCalculator timeBandCalculator = new TimeBandIdleCalculator()

	public void collapseIdleTime(List<IdeaFlowBandModel> ideaFlowBands, List<IdleActivityEntity> idleActivities) {
		for (IdleActivityEntity idle : idleActivities) {
			addIdleDuration(ideaFlowBands, idle)
		}
	}

	private void addIdleDuration(List<IdeaFlowBandModel> timeBands, IdleActivityEntity idleEntity) {
		for (IdeaFlowBandModel ideaFlowBandModel : timeBands) {
			IdleTimeBandModel idle = toIdleTimeBand(idleEntity)
			IdleTimeBandModel splitIdle = timeBandCalculator.getIdleForTimeBandOrNull(ideaFlowBandModel, idle)
			if (splitIdle != null) {
				ideaFlowBandModel.addIdleBand(splitIdle)
				addIdleDuration(ideaFlowBandModel.nestedBands, idleEntity)
			}
		}
	}

	private IdleTimeBandModel toIdleTimeBand(IdleActivityEntity entity) {
		// TODO: use dozer
		IdleTimeBandModel.builder()
				.id(entity.id)
				.taskId(entity.taskId)
				.start(TimeConverter.toJodaLocalDateTime(entity.start))
				.end(TimeConverter.toJodaLocalDateTime(entity.end))
				.build()
	}

}
