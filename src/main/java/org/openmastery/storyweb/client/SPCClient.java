package org.openmastery.storyweb.client;

import com.bancvue.rest.client.crud.CrudClientRequest;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmastery.storyweb.api.GlossaryDefinition;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.api.SPCChart;
import org.openmastery.time.TimeConverter;

import java.util.List;

public class SPCClient extends StorywebClient<SPCChart, SPCClient> {

	public SPCClient(String baseUrl) {
		super(baseUrl, ResourcePaths.SPC_PATH, SPCChart.class);
	}

	public SPCChart generateChart(LocalDate startDate, LocalDate endDate) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		String startDateStr = formatter.print(startDate);
		String endDateStr = formatter.print(endDate);

		return crudClientRequest.queryParam("startDate", startDateStr)
				.queryParam("endDate", endDateStr).find();
	}

}
