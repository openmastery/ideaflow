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
package org.openmastery.storyweb.resources;

import org.openmastery.mapper.EntityMapper;
import org.openmastery.storyweb.api.GlossaryEntry;
import org.openmastery.storyweb.api.ResourcePaths;
import org.openmastery.storyweb.core.glossary.GlossaryEntryEntity;
import org.openmastery.storyweb.core.glossary.GlossaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePaths.GLOSSARY_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class GlossaryResource {

	@Autowired
	private GlossaryRepository glossaryRepository;
	private EntityMapper entityMapper = new EntityMapper();

	@PUT
	public GlossaryEntry createOrUpdate(GlossaryEntry entry) {
		GlossaryEntryEntity entryEntity = entityMapper.mapIfNotNull(entry, GlossaryEntryEntity.class);
		glossaryRepository.save(entryEntity);
		return entry;
	}

	@GET
	public List<GlossaryEntry> findAll() {
		return entityMapper.mapList(glossaryRepository.findAll(), GlossaryEntry.class);
	}

}
