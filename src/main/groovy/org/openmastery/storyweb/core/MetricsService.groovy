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
import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.ideaflow.story.IdeaFlowStoryGenerator
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.storyweb.api.metrics.SPCChart

import org.openmastery.storyweb.core.metrics.analyzer.ExperimentFrequencyAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.HaystackAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.HumanCycleTimeAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.ResolutionTimeAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.WtfsPerDayAnalyzer
import org.openmastery.storyweb.core.metrics.spc.MetricSet
import org.openmastery.storyweb.core.metrics.spc.TaskData
import org.openmastery.storyweb.core.metrics.spc.TaskDataGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MetricsService {

	@Autowired
	TaskDataGenerator taskDataGenerator

	@Autowired
	InvocationContext invocationContext

	IdeaFlowStoryGenerator storyGenerator = new IdeaFlowStoryGenerator()

	SPCChart generateSPCChart(LocalDate startDate, LocalDate endDate) {
		Long userId = invocationContext.userId

		SPCChart chart = new SPCChart()

		List<TaskData> taskDataList = taskDataGenerator.generate(userId, startDate, endDate)
		taskDataList.each { TaskData taskData ->
			IdeaFlowTaskTimeline taskTimeline = taskData.toIdeaFlowTaskTimeline()
			IdeaFlowStory story = storyGenerator.generateIdeaFlowStory(taskTimeline)

			MetricSet metrics = generateMetricsForTask(story)
			chart.addGraphPoints(metrics.explodableTrees)
			chart.addPainThresholds(metrics.painThresholds)
		}

		return chart;
	}


	MetricSet generateMetricsForTask(IdeaFlowStory story) {
		MetricSet metricSet = new MetricSet()
		metricSet.addMetric(new HaystackAnalyzer())
		metricSet.addMetric(new ResolutionTimeAnalyzer())
		metricSet.addMetric(new HumanCycleTimeAnalyzer())
		metricSet.addMetric(new ExperimentFrequencyAnalyzer())
		metricSet.addMetric(new WtfsPerDayAnalyzer())

		return metricSet.calculate(story)
	}


}
