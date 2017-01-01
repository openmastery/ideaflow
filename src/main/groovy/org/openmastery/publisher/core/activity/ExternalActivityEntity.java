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

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("external")
@Data
@Builder
@EqualsAndHashCode(callSuper = true, of = {})
public class ExternalActivityEntity extends ActivityEntity {

	private static final String COMMENT_KEY = "comment";

	private ExternalActivityEntity() {}

	private ExternalActivityEntity(long id, long ownerId, long taskId, LocalDateTime start, LocalDateTime end, String comment) {
		super(id, ownerId, taskId, start, end);
		setComment(comment);
	}

	public String getComment() {
		return getMetadataValue(COMMENT_KEY);
	}

	public void setComment(String comment) {
		setMetadataField(COMMENT_KEY, comment);
	}

	public static class ExternalActivityEntityBuilder extends ActivityEntityBuilder<ExternalActivityEntityBuilder> {

		private String comment;

		public ExternalActivityEntity build() {
			return new ExternalActivityEntity(id, ownerId, taskId, start, end, comment);
		}

		public ExternalActivityEntityBuilder comment(String comment) {
			this.comment = comment;
			return this;
		}

	}
	
}
