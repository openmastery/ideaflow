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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("snippet")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SnippetAnnotationEntity extends AnnotationEntity {

	private static final String SOURCE_KEY = "source";
	private static final String SNIPPET_KEY = "snippet";

	private SnippetAnnotationEntity() {
	}

	private SnippetAnnotationEntity(long id, long ownerId, long taskId, long eventId, String source, String snippet) {
		super(id, ownerId, taskId, eventId);
		setSource(source);
		setSnippet(snippet);
	}

	public String getSource() {
		return getMetadataValue(SOURCE_KEY);
	}

	public void setSource(String source) {
		setMetadataField(SOURCE_KEY, source);
	}

	public String getSnippet() {
		return getMetadataValue(SNIPPET_KEY);
	}

	public void setSnippet(String snippet) {
		setMetadataField(SNIPPET_KEY, snippet);
	}


	public static class SnippetAnnotationEntityBuilder extends AnnotationEntityBuilder<SnippetAnnotationEntityBuilder> {

		private String source;
		private String snippet;

		public SnippetAnnotationEntity build() {
			return new SnippetAnnotationEntity(id, ownerId, taskId, eventId, source, snippet);
		}

		public SnippetAnnotationEntityBuilder source(String source) {
			this.source = source;
			return this;
		}

		public SnippetAnnotationEntityBuilder snippet(String snippet) {
			this.snippet = snippet;
			return this;
		}

	}

}

