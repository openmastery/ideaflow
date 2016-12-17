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
package org.openmastery.publisher.core.activity;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.openmastery.publisher.api.Positionable;
import org.openmastery.time.TimeConverter;

public abstract class ActivityModel<T extends ActivityEntity> implements Positionable {

	protected T delegate;
	@Getter @Setter
	private Long relativePositionInSeconds;

	public ActivityModel(T delegate) {
		this.delegate = delegate;
	}

	public Long getId() {
		return delegate.getId();
	}

	public Long getTaskId() {
		return delegate.getTaskId();
	}

	public LocalDateTime getPosition() {
		return getStart();
	}

	public LocalDateTime getStart() {
		return TimeConverter.toJodaLocalDateTime(delegate.getStart());
	}

	public LocalDateTime getEnd() {
		return TimeConverter.toJodaLocalDateTime(delegate.getEnd());
	}

	public Duration getDuration() {
		return TimeConverter.between(getStart(), getEnd());
	}

}
