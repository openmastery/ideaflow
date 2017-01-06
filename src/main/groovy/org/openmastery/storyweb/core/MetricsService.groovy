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
package org.openmastery.storyweb.core

import org.joda.time.LocalDate
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.storyweb.api.metrics.SPCChart
import org.openmastery.storyweb.core.metrics.spc.SPCChartGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MetricsService {

	@Autowired
	SPCChartGenerator spcChartGenerator

	@Autowired
	InvocationContext invocationContext


	SPCChart generateSPCChart(LocalDate startDate, LocalDate endDate) {
		return spcChartGenerator.generateChart(invocationContext.userId, startDate, endDate)
	}
}
