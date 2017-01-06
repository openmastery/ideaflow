package org.openmastery.storyweb.api.glossary;

import lombok.Data;

import java.util.List;

@Data
public class Glossary {

	List<GlossaryDefinition> definitions;
}
