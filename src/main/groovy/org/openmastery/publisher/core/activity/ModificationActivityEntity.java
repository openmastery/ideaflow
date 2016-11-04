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
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("modification")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ModificationActivityEntity extends ActivityEntity {

	private static final String MODIFICATION_COUNT_KEY = "modificationCount";

	private ModificationActivityEntity() {
	}

	private ModificationActivityEntity(long id, long ownerId, long taskId, LocalDateTime start, LocalDateTime end,
	                                   int modificationCount) {
		super(id, ownerId, taskId, start, end);
		setModificationCount(modificationCount);
	}

	public void setModificationCount(int modificationCount) {
		setMetadataField(MODIFICATION_COUNT_KEY, modificationCount);
	}

	public int getModificationCount() {
		return getMetadataValueAsInteger(MODIFICATION_COUNT_KEY);
	}


	public static class ModificationActivityEntityBuilder extends ActivityEntityBuilder<ModificationActivityEntityBuilder> {

		private int modificationCount;

		public ModificationActivityEntity build() {
			return new ModificationActivityEntity(id, ownerId, taskId, start, end, modificationCount);
		}

		public ModificationActivityEntityBuilder modificationCount(int modificationCount) {
			this.modificationCount = modificationCount;
			return this;
		}
	}

}

