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
package org.openmastery.storyweb.core.glossary;

import org.openmastery.storyweb.api.GlossaryDefinition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GlossaryRepository extends PagingAndSortingRepository<GlossaryDefinitionEntity, String> {

	@Query(nativeQuery = true, value = "select * from glossary order by name")
	List<GlossaryDefinitionEntity> findAll();

	@Query(nativeQuery = true, value = "select * from glossary where lower(name) similar to ?1 order by name")
	List<GlossaryDefinitionEntity> findByTagsLike(String searchStr);
}
