package org.openmastery.storyweb.client;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.api.SPCChart;

public class SPCClient extends StorywebClient<SPCChart, SPCClient> {

	public SPCClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.SPC_PATH, SPCChart.class);
	}

	public SPCChart generateChart(LocalDate startDate, LocalDate endDate) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		String startDateStr = formatter.print(startDate);
		String endDateStr = formatter.print(endDate);

		return crudClientRequest.queryParam("startDate", startDateStr)
				.queryParam("endDate", endDateStr).find();
	}

}
