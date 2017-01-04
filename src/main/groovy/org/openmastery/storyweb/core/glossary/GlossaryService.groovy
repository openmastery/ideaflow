package org.openmastery.storyweb.core.glossary

import com.bancvue.rest.exception.ConflictException
import com.bancvue.rest.exception.NotFoundException
import org.openmastery.mapper.EntityMapper
import org.openmastery.storyweb.api.GlossaryDefinition
import org.openmastery.storyweb.api.TagsUtil
import org.openmastery.storyweb.core.SearchUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GlossaryService {

	@Autowired
	GlossaryRepository glossaryRepository

	private EntityMapper entityMapper = new EntityMapper();

	public List<GlossaryDefinition> findGlossaryDefinitionsByTag(List<String> tags) {
		String searchPattern = SearchUtils.createSearchPattern(tags)
		entityMapper.mapList(glossaryRepository.findByTagsLike(searchPattern), GlossaryDefinition.class)
	}


	List<GlossaryDefinition> findAllGlossaryDefinitions() {
		return entityMapper.mapList(glossaryRepository.findAll(), GlossaryDefinition.class);
	}

	GlossaryDefinition updateExistingTerm(Long id, GlossaryDefinition entry) {
		GlossaryDefinitionEntity definition = glossaryRepository.findOne(id)
		if (definition == null) {
			throw new NotFoundException("Unable to find GlossaryDefinition with "+id);
		}

		definition.name =  TagsUtil.prefixHashtag(entry.name)
		definition.description = entry.description

		GlossaryDefinitionEntity savedDefinition = glossaryRepository.save(definition)

		return toApi(savedDefinition);
	}

	GlossaryDefinition createNewTerm(GlossaryDefinition glossaryDefinition) {
		String searchStr = SearchUtils.createSearchPattern([glossaryDefinition.name])
		List<GlossaryDefinitionEntity> conflictingEntries = glossaryRepository.findByTagsLike(searchStr)

		if (conflictingEntries.size() > 0) {
			throw new ConflictException("Conflicting glossary term found", conflictingEntries.get(0))
		}
		GlossaryDefinitionEntity entity = toEntity(glossaryDefinition)
		entity.name = TagsUtil.prefixHashtag(entity.name);
		entity.id = null

		GlossaryDefinitionEntity savedDefinition = glossaryRepository.save(entity)
		return toApi(savedDefinition);
	}

	GlossaryDefinition toApi(GlossaryDefinitionEntity entity) {
		return entityMapper.mapIfNotNull(entity, GlossaryDefinition.class);
	}

	GlossaryDefinitionEntity toEntity(GlossaryDefinition definition) {
		return entityMapper.mapIfNotNull(definition, GlossaryDefinitionEntity.class);
	}

	void createGlossaryDefinitionsWhenNotExists(List<String> tags) {
		String searchPattern = SearchUtils.createSearchPattern(tags)
		List<GlossaryDefinitionEntity> glossaryEntities = glossaryRepository.findByTagsLike(searchPattern)

		Map<String, GlossaryDefinitionEntity> definitionsByTag = glossaryEntities.collectEntries { entry ->
			[entry.name, entry]
		}

		tags.each { String tag ->
			if (definitionsByTag.get(tag) == null) {
				GlossaryDefinitionEntity newEntry = new GlossaryDefinitionEntity(null, tag, null)
				glossaryRepository.save(newEntry)
			}
		}
	}


}
