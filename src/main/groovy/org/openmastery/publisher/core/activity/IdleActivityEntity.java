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

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("idle")
@Data
@EqualsAndHashCode(callSuper = true, of = {})
public class IdleActivityEntity extends ActivityEntity {

	private IdleActivityEntity() {}

	private IdleActivityEntity(long id, long taskId, LocalDateTime start, LocalDateTime end) {
		super(id, taskId, start, end);
	}


	public static IdleActivityEntityBuilder builder() {
		return new IdleActivityEntityBuilder();
	}

	public static class IdleActivityEntityBuilder extends ActivityEntityBuilder<IdleActivityEntityBuilder> {

		public IdleActivityEntity build() {
			return new IdleActivityEntity(id, taskId, start, end);
		}

	}

}
