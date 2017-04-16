/**
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
package org.openmastery.storyweb.resources;


import lombok.extern.slf4j.Slf4j;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.api.metrics.SPCChart;
import org.openmastery.storyweb.core.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Path(ResourcePaths.STORY_WEB_PATH + ResourcePaths.METRICS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class MetricsResource {

	@Autowired
	private MetricsService metricsService;

	/**
	 * Generate a chart with explodable (drill-downable) graphpoints, from task, to journey, to discovery cycle, to execution cycle
	 * @param startDate inclusive beginning
	 * @param endDate exclusive end
	 * @return SPCChart
	 */
	@GET
	@Path(ResourcePaths.METRICS_SPC_PATH)
	public SPCChart generateSPCChart(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		System.out.println("generateSPCChart [" + startDate + " : " + endDate +"]");
		LocalDate jodaStartDate;
		LocalDate jodaEndDate;
		if (startDate != null && endDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			jodaStartDate = LocalDate.parse(startDate, formatter);
			jodaEndDate = LocalDate.parse(endDate, formatter);
		} else {
			jodaStartDate = LocalDate.now().minusDays(7);
			jodaEndDate = LocalDate.now();
		}

		return metricsService.generateSPCChart(jodaStartDate, jodaEndDate);
	}


}
