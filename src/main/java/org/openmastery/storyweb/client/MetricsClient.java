package org.openmastery.storyweb.client;

import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.api.metrics.SPCChart;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MetricsClient extends StorywebClient<SPCChart, MetricsClient> {

	public MetricsClient(String baseUrl) {
		super(baseUrl, ResourcePaths.STORY_WEB_PATH + ResourcePaths.METRICS_PATH, SPCChart.class);
	}

	public SPCChart generateChart(LocalDate startDate, LocalDate endDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String startDateStr = formatter.format(startDate);
		String endDateStr = formatter.format(endDate);

		return crudClientRequest.path(ResourcePaths.METRICS_SPC_PATH)
				.queryParam("startDate", startDateStr)
				.queryParam("endDate", endDateStr).find();
	}


}
