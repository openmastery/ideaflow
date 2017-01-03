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
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.api.SPCChart;
import org.openmastery.storyweb.core.StoryWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.STORY_WEB_PATH + ResourcePaths.SPC_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class SPCResource {


	@Autowired
	private StoryWebService storyWebService;


	/**
	 * Generate a chart with explodable (drill-downable) graphpoints, from task, to journey, to discovery cycle, to execution cycle
	 * @param startDate inclusive beginning
	 * @param endDate exclusive end
	 * @return SPCChart
	 */
	@GET
	public SPCChart generateSPCChart(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		System.out.println("generateSPCChart [" + startDate + " : " + endDate +"]");
		LocalDate jodaStartDate;
		LocalDate jodaEndDate;
		if (startDate != null && endDate != null) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
			jodaStartDate = formatter.parseLocalDate(startDate);
			jodaEndDate = formatter.parseLocalDate(endDate);
		} else {
			jodaStartDate = LocalDate.now().minusDays(7);
			jodaEndDate = LocalDate.now();
		}
		
		return storyWebService.generateSPCChart(jodaStartDate, jodaEndDate);
	}

}
