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

import org.openmastery.publisher.api.ideaflow.IdeaFlowTaskTimeline
import org.openmastery.publisher.api.journey.IdeaFlowStory
import org.openmastery.publisher.api.journey.PainCycle
import org.openmastery.publisher.api.journey.SubtaskStory
import org.openmastery.publisher.api.journey.TagsUtil
import org.openmastery.publisher.api.journey.TroubleshootingJourney
import org.openmastery.publisher.api.metrics.DurationInSeconds
import org.openmastery.publisher.ideaflow.story.AnnotationDecorator
import org.openmastery.publisher.ideaflow.story.IdeaFlowStoryGenerator
import org.openmastery.publisher.ideaflow.story.MetricsDecorator
import org.openmastery.publisher.security.InvocationContext
import org.openmastery.storyweb.api.StoryPoint
import org.openmastery.storyweb.api.metrics.Metric
import org.openmastery.storyweb.api.metrics.SPCChart
import org.openmastery.storyweb.core.metrics.analyzer.DisruptionsPerDayAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.ExperimentFrequencyAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.HaystackAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.HumanCycleTimeAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.ResolutionTimeAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.TotalResolutionTimeAnalyzer
import org.openmastery.storyweb.core.metrics.analyzer.WtfsPerDayAnalyzer
import org.openmastery.storyweb.core.metrics.spc.FilterlessTaskDataGenerator
import org.openmastery.storyweb.core.metrics.spc.MetricSet
import org.openmastery.storyweb.core.metrics.spc.TaskData
import org.openmastery.storyweb.core.metrics.spc.TaskDataGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDate

@Component
class MetricsService {

	@Autowired
	TaskDataGenerator taskDataGenerator

	@Autowired
	FilterlessTaskDataGenerator filterlessTaskDataGenerator

	@Autowired
	InvocationContext invocationContext

	@Autowired
	AnnotationDecorator annotationDecorator

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
			chart.addTask(taskData.task)
		}

		return chart;
	}

	List<StoryPoint> findAndFilterBiggestPainPoints(List<String> tags) {
		List<StoryPoint> painPoints = findBiggestPainPoints();
		if (tags != null && tags.size() > 0) {
			painPoints = painPoints.findAll { StoryPoint painPoint ->
				boolean matchesTag = false;

				tags.each { String tag ->
					String tagWithHash = TagsUtil.prefixHashtag(tag)
					matchesTag |= painPoint.contextTags.contains(tagWithHash) || painPoint.painTags.contains(tagWithHash)
				}

				matchesTag
			}
		}
		return painPoints
	}


	List<StoryPoint> findBiggestPainPoints() {
		List<StoryPoint> allPainPoints = []

		Long userId = invocationContext.userId

		List<TaskData> taskDataList = filterlessTaskDataGenerator.generate(userId)
		taskDataList.each { TaskData taskData ->
			IdeaFlowTaskTimeline taskTimeline = taskData.toIdeaFlowTaskTimeline()
			IdeaFlowStory story = storyGenerator.generateIdeaFlowStory(taskTimeline)
			annotationDecorator.annotateStory(story, taskData.faqAnnotations, [])

			List<StoryPoint> painPoints = generatePainPoints(story)
			allPainPoints.addAll(painPoints)
		}

		return allPainPoints.sort { StoryPoint point -> point.journeyPainInSeconds }.reverse()

	}

	List<StoryPoint> generatePainPoints(IdeaFlowStory ideaFlowStory) {
		MetricSet metricSet = new MetricSet()
		metricSet.addMetric(new TotalResolutionTimeAnalyzer())
		metricSet.calculate(ideaFlowStory)

		MetricsDecorator decorator = new MetricsDecorator()
		decorator.decorateStoryWithMetrics(ideaFlowStory, metricSet)

		List<StoryPoint> painPoints = []

		ideaFlowStory.subtasks.each { SubtaskStory subtask ->
			subtask.troubleshootingJourneys.each { TroubleshootingJourney journey ->
				journey.painCycles.each { PainCycle painCycle ->
					StoryPoint painPoint = new StoryPoint()
					painPoint.taskPath = ideaFlowStory.fullPath
					painPoint.fullPath = painCycle.fullPath

					painPoint.faqType = painCycle.eventType
					painPoint.eventDescription = painCycle.description
					painPoint.faqAnnotation = painCycle.faqAnnotation
					painPoint.taskName = ideaFlowStory.task.name

					painPoint.position = painCycle.position
					painPoint.contextTags = painCycle.contextTags
					painPoint.painTags = painCycle.painTags

					painPoint.journeyPainInSeconds = getFirstMetricValue(journey.allMetrics)
					painPoint.taskPainInSeconds = getFirstMetricValue(ideaFlowStory.allMetrics)

					painPoints.add(painPoint)
				}
			}
		}

		return painPoints
	}

	Long getFirstMetricValue(List<Metric<DurationInSeconds>> metrics) {
		Long value = 0
		if (metrics.size() > 0) {
			value = metrics.get(0).value.durationInSeconds
		}
		return value
	}

	MetricSet generateMetricsForTask(IdeaFlowStory story) {
		MetricSet metricSet = new MetricSet()
		metricSet.addMetric(new HaystackAnalyzer())
		metricSet.addMetric(new ResolutionTimeAnalyzer())
		metricSet.addMetric(new HumanCycleTimeAnalyzer())
		metricSet.addMetric(new ExperimentFrequencyAnalyzer())
		metricSet.addMetric(new WtfsPerDayAnalyzer())
		metricSet.addMetric(new DisruptionsPerDayAnalyzer())

		return metricSet.calculate(story)
	}



}
