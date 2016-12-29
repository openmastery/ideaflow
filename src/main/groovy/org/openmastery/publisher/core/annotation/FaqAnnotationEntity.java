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
package org.openmastery.publisher.core.annotation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.openmastery.publisher.core.activity.ActivityEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("faq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FaqAnnotationEntity extends AnnotationEntity {

	private static final String COMMENT_KEY = "comment";

	private FaqAnnotationEntity() {
	}

	private FaqAnnotationEntity(long id, long ownerId, long taskId, long eventId, String comment) {
		super(id, ownerId, taskId, eventId);
		setComment(comment);
	}

	public String getComment() {
		return getMetadataValue(COMMENT_KEY);
	}

	public void setComment(String comment) {
		setMetadataField(COMMENT_KEY, comment);
	}

	public static FaqAnnotationEntityBuilder builder() {
		return new FaqAnnotationEntityBuilder();
	}

	public static class FaqAnnotationEntityBuilder extends AnnotationEntityBuilder<FaqAnnotationEntityBuilder> {

		private String comment;

		public FaqAnnotationEntity build() {
			return new FaqAnnotationEntity(id, ownerId, taskId, eventId, comment);
		}

		public FaqAnnotationEntityBuilder comment(String comment) {
			this.comment = comment;
			return this;
		}

	}

}

