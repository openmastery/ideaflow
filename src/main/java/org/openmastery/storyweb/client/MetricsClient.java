package org.openmastery.storyweb.client;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.api.metrics.SPCChart;

public class MetricsClient extends StorywebClient<SPCChart, MetricsClient> {

	public MetricsClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.METRICS_PATH, SPCChart.class);
	}

	public SPCChart generateChart(LocalDate startDate, LocalDate endDate) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		String startDateStr = formatter.print(startDate);
		String endDateStr = formatter.print(endDate);

		return crudClientRequest.path(ResourcePaths.METRICS_SPC_PATH)
				.queryParam("startDate", startDateStr)
				.queryParam("endDate", endDateStr).find();
	}


}
