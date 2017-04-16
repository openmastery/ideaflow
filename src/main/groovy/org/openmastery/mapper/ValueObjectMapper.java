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
package org.openmastery.mapper;

import org.dozer.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ValueObjectMapper {

    private Mapper mapper;

    public ValueObjectMapper() {
        this(new DozerMapperFactory());
    }

    public ValueObjectMapper(DozerMapperFactory mapperFactory) {
        mapper = mapperFactory.createMapper();
    }

    public <S, D> D mapIfNotNull(S source, Class<D> destType) {
        D dest = null;
        if (source != null) {
            dest = mapper.map(source, destType);
        }
        return dest;
    }

    public <S, D> List<D> mapList(Iterable<S> source, Class<D> destType) {
        if (source == null) {
            return Collections.EMPTY_LIST;
        }

        return mapList(source, (entity) -> mapIfNotNull(entity, destType));
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
