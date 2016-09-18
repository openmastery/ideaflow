/**
 * Copyright 2016 New Iron Group, Inc.
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
package org.openmastery.publisher.core.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdleTimeBandModel extends TimeBandModel<IdleTimeBandModel> {

	private long id;
	private long taskId;

	private LocalDateTime start;
	private LocalDateTime end;

	private String comment;

	private boolean auto;

	@Override
	public Duration getDuration() {
		return Duration.between(start, end);
	}

	@Override
	@JsonIgnore
	public List<TimeBandModel> getContainedBands() {
		return Collections.EMPTY_LIST;
	}

	@Override
	protected IdleTimeBandModel internalSplitAndReturnLeftSide(LocalDateTime position) {
		return from(this)
				.end(position)
				.build();
	}

	@Override
	protected IdleTimeBandModel internalSplitAndReturnRightSide(LocalDateTime position) {
		return from(this)
				.start(position)
				.build();
	}

	public static IdleTimeBandModel.IdleTimeBandModelBuilder from(IdleTimeBandModel idle) {
		return builder().id(idle.id)
				.taskId(idle.taskId)
				.start(idle.start)
				.end(idle.end)
				.comment(idle.comment)
				.auto(idle.auto);
	}

}
