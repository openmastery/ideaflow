package org.openmastery.mapper;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class EntityMapper {
    private Mapper mapper = createMapper();

    private Mapper createMapper() {
        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.setMappingFiles(getMappingConfigurationFiles());
        return mapper;
    }

    private List<String> getMappingConfigurationFiles() {
        List<String> mappingConfigurationFiles = new ArrayList<>();
        mappingConfigurationFiles.add("dozerConfig.xml");
        return mappingConfigurationFiles;
    }

    public <S, D> D mapIfNotNull(S source, Class<D> destType) {
        D dest = null;
        if (source != null) {
            dest = mapper.map(source, destType);
        }
        return dest;
    }

    public <S, D> List<D> mapList(Iterable<S> source, Function<S, D> function) {
        return toStream(source)
            .map(function)
            .collect(Collectors.toList());
    }

    private <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
