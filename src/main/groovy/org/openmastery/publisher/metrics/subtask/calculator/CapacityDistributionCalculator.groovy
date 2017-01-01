/*
 * Copyright 2017 New Iron Group, Inc.
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
package org.openmastery.publisher.metrics.subtask.calculator

import org.openmastery.publisher.api.ideaflow.IdeaFlowBand
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.ideaflow.IdeaFlowTimeline
import org.openmastery.publisher.api.metrics.CapacityDistribution

class CapacityDistributionCalculator {


	CapacityDistribution calculateCapacityDistribution(IdeaFlowTimeline timeline) {

		CapacityDistribution capacity = new CapacityDistribution()

		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.LEARNING)
		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.PROGRESS)
		saveTotalDurationForBandType(capacity, timeline, IdeaFlowStateType.TROUBLESHOOTING)

		return capacity
	}

	CapacityDistribution calculateCapacityWithinWindow(IdeaFlowTimeline timeline, Long windowStart, Long windowEnd) {

		CapacityDistribution distribution = new CapacityDistribution()
		distribution.addDurationForType(IdeaFlowStateType.LEARNING, 0)
		distribution.addDurationForType(IdeaFlowStateType.PROGRESS, 0)
		distribution.addDurationForType(IdeaFlowStateType.TROUBLESHOOTING, 0)

		timeline.ideaFlowBands.each { IdeaFlowBand band ->
			if (band.relativeStart >= windowStart && band.relativeStart < windowEnd) {
				long endPoint = Math.min(band.relativeEnd, windowEnd)
				long timeWithinWindow = endPoint - band.relativeStart
				distribution.addDurationForType(band.type, timeWithinWindow)
			} else if (band.relativeEnd >= windowStart && band.relativeEnd < windowEnd) {
				long timeWithinWindow = band.relativeEnd - windowStart
				distribution.addDurationForType(band.type, timeWithinWindow)
			} else if (band.relativeStart <= windowStart && band.relativeEnd >= windowEnd) {
				distribution.addDurationForType(band.type, windowEnd - windowStart)
			}
		}

		return distribution
	}


	void saveTotalDurationForBandType(CapacityDistribution capacity, IdeaFlowTimeline timeline, IdeaFlowStateType ideaFlowStateType) {

		List<IdeaFlowBand> bands = timeline.ideaFlowBands.findAll() { IdeaFlowBand band ->
			band.type == ideaFlowStateType
		}

		long totalDuration = 0

		bands.each { IdeaFlowBand band ->
			totalDuration += band.durationInSeconds
		}

		capacity.addDurationForType(ideaFlowStateType, totalDuration)

	}
}
