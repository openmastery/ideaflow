/**
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
package org.openmastery.publisher.core.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("editor")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EditorActivityEntity extends ActivityEntity {

	private static final String FILE_PATH_KEY = "filePath";
	private static final String MODIFIED_KEY = "modified";

	private EditorActivityEntity() {}

	private EditorActivityEntity(long id, long ownerId, long taskId, LocalDateTime start, LocalDateTime end, String filePath, boolean modified) {
		super(id, ownerId, taskId, start, end);
		setFilePath(filePath);
		setModified(modified);
	}

	public String getFilePath() {
		return getMetadataValue(FILE_PATH_KEY);
	}

	public void setFilePath(String filePath) {
		setMetadataField(FILE_PATH_KEY, filePath);
	}

	public boolean isModified() {
		return getMetadataValueAsBoolean(MODIFIED_KEY);
	}

	public void setModified(boolean modified) {
		setMetadataField(MODIFIED_KEY, modified);
	}


	public static EditorActivityEntityBuilder builder() {
		return new EditorActivityEntityBuilder();
	}

	public static class EditorActivityEntityBuilder extends ActivityEntityBuilder<EditorActivityEntityBuilder> {

		private String filePath;
		private boolean modified;

		public EditorActivityEntity build() {
			return new EditorActivityEntity(id, ownerId, taskId, start, end, filePath, modified);
		}

		public EditorActivityEntityBuilder filePath(String filePath) {
			this.filePath = filePath;
			return this;
		}

		public EditorActivityEntityBuilder isModified(boolean modified) {
			this.modified = modified;
			return this;
		}
	}

}
