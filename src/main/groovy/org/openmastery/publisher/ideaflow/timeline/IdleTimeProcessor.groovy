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
package org.openmastery.publisher.ideaflow.timeline

import org.openmastery.publisher.core.timeline.IdleTimeBandModel
import org.openmastery.publisher.core.timeline.TimeBandIdleCalculator
import org.openmastery.publisher.ideaflow.IdeaFlowBandModel

class IdleTimeProcessor {

	private TimeBandIdleCalculator timeBandCalculator = new TimeBandIdleCalculator()

	public void collapseIdleTime(List<IdeaFlowBandModel> ideaFlowBands, List<IdleTimeBandModel> idleTimeBandList) {
		for (IdleTimeBandModel idleTimeBandModel : idleTimeBandList) {
			addIdleDuration(ideaFlowBands, idleTimeBandModel)
		}
	}

	private void addIdleDuration(List<IdeaFlowBandModel> timeBands, IdleTimeBandModel idleTimeBand) {
		for (IdeaFlowBandModel ideaFlowBandModel : timeBands) {
			IdleTimeBandModel splitIdle = timeBandCalculator.getIdleForTimeBandOrNull(ideaFlowBandModel, idleTimeBand)
			if (splitIdle != null) {
				ideaFlowBandModel.addIdleBand(splitIdle)
				addIdleDuration(ideaFlowBandModel.nestedBands, idleTimeBand)
			}
		}
	}

}
