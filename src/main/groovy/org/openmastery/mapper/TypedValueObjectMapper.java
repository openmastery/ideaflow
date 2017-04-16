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

import java.util.List;

public class TypedValueObjectMapper<FROM, TO> {

    private Class<TO> toType;
    private ValueObjectMapper mapper;

    public TypedValueObjectMapper(Class<TO> toType) {
        this(new DozerMapperFactory(), toType);
    }

    public TypedValueObjectMapper(DozerMapperFactory mapperFactory, Class<TO> toType) {
        this.toType = toType;
        mapper = new ValueObjectMapper(mapperFactory);
    }

    public TO map(FROM from) {
        TO to = mapper.mapIfNotNull(from, this.toType);
        if (to != null) {
            onMap(from, to);
        }
        return to;
    }

    protected void onMap(FROM from, TO to) {
        // subclass to override if custom mapping is required
    }

    public List<TO> mapList(Iterable<FROM> fromList) {
        return mapper.mapList(fromList, this::map);
    }

}
