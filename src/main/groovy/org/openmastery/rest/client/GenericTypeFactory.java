/*
 * Copyright 2014 BancVue, LTD
 * Modifications Copyright 2016 Blackbaud, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.rest.client;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.GenericType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class GenericTypeFactory {

    private static final GenericTypeFactory INSTANCE = new GenericTypeFactory();

    public static GenericTypeFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a generic type.  If a single class is provided, a GenericType
     * is created using the input type.  Otherwise, a ParameterizedType is created
     * using the input types.
     * <p/>
     * For example, if the input types are
     * <code>DefaultEnvelope, List, Widget</code>, then the returned value is
     * equivalent to the following declaration:
     * <code>new GenericType<DefaultEnvelope<List<Widget>>>() {}</code>
     */
    public GenericType createGenericType(@NotNull Type... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException();
        } else if (types.length == 1) {
            return new GenericType(types[0]);
        }

        Type parameterizedType = createParameterizedType(types);
        return new GenericType(parameterizedType);
    }

    private static ParameterizedType createParameterizedType(Type[] types) {
        if (types.length < 2) {
            throw new IllegalArgumentException();
        }

        Type rawType = types[0];
        Type actualType = types[1];
        if (types.length > 2) {
            Type[] remainingTypes = Arrays.copyOfRange(types, 1, types.length);
            actualType = createParameterizedType(remainingTypes);
        }
        return new SimpleParameterizedType(rawType, actualType);
    }


    private static class SimpleParameterizedType implements ParameterizedType {

        private Type actualType;
        private Type rawType;

        public SimpleParameterizedType(Type rawType, Type actualType) {
            this.rawType = rawType;
            this.actualType = actualType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{actualType};
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        public String toString() {
            return rawType + "<" + actualType + ">";
        }

        public int hashCode() {
            return rawType.hashCode() ^ actualType.hashCode();
        }

        public boolean equals(Object other) {
            if (other == null || other.getClass() != getClass()) {
                return false;
            }

            SimpleParameterizedType otherType = (SimpleParameterizedType) other;
            return rawType == otherType.rawType && actualType == otherType.actualType;
        }
    }

}
