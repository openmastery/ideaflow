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

import com.bancvue.rest.client.ClientRequestFactory;
import org.openmastery.rest.api.ApiEntity;
import org.openmastery.rest.client.response.ResponseInspector;
import org.openmastery.rest.config.ObjectMapperContextResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public abstract class CrudClient<API_TYPE, CLIENT_TYPE extends CrudClient> {
    protected CrudClientRequest<API_TYPE> crudClientRequest;

    public CrudClient(String baseUrl, String path, Class<API_TYPE> type) {
        ClientRequestFactory clientRequestFactory = new ClientRequestFactory().register(ObjectMapperContextResolver.class);
        crudClientRequest = new CrudClientRequest<API_TYPE>(clientRequestFactory.createClientRequest(baseUrl), type).path(path);
    }

    public CrudClient(CrudClientRequest<API_TYPE> crudClientRequest) {
        this.crudClientRequest = crudClientRequest;
    }

    protected CrudClientRequest getUntypedCrudClientRequest() {
        return crudClientRequest;
    }

    @SuppressWarnings("unchecked")
    private CLIENT_TYPE create(CrudClientRequest crudClientRequest) {
        CLIENT_TYPE crudClient;
        try {
            Constructor constructor = getClass().getConstructor(String.class);
            // the baseUrl doesn't matter since we'll be re-setting the CrudClientRequest
            crudClient = (CLIENT_TYPE) constructor.newInstance("http://base-url");
        } catch (Exception ex) {
            throw new RuntimeException("Application error - " + getClass() + " must implement a single-arg " +
                                               "constructor that accepts the baseUrl as String", ex);
        }
        try {
            Field crudClientRequestField = CrudClient.class.getDeclaredField("crudClientRequest");
            crudClientRequestField.set(crudClient, crudClientRequest);
        } catch (Exception ex) {
            throw new RuntimeException("Application error - failed to set crudClientRequest on type=" + getClass(), ex);
        }
        return crudClient;
    }

    public CLIENT_TYPE header(Header header) {
        return header(header.getName(), header.getValue());
    }

    public CLIENT_TYPE header(String name, Object value) {
        return create(crudClientRequest.header(name, value));
    }

    public CLIENT_TYPE responseInspector(ResponseInspector responseInspector) {
        return create(crudClientRequest.responseInspector(responseInspector));
    }

    @Deprecated
    public void addDefaultHeader(String name, Object value) {
        crudClientRequest = crudClientRequest.header(name, value);
    }

    public List<API_TYPE> findMany() {
        return crudClientRequest.findMany();
    }

    public API_TYPE find(Object id) {
        return crudClientRequest.find(id);
    }

    public API_TYPE create(API_TYPE entity) {
        return crudClientRequest.createWithPost(entity);
    }

    @SuppressWarnings("unchecked")
    public Object createUntyped(Object entity) {
        return getUntypedCrudClientRequest().createWithPost(entity);
    }

    public API_TYPE update(API_TYPE entity) {
        CrudClientRequest<API_TYPE> request = crudClientRequest;
        if (entity instanceof ApiEntity) {
            request = request.path(((ApiEntity) entity).getId());
        }
        return request.updateWithPut(entity);
    }

    @SuppressWarnings("unchecked")
    public Object updateUntyped(Object entity) {
        CrudClientRequest request = getUntypedCrudClientRequest();
        if (entity instanceof ApiEntity) {
            request = request.path(((ApiEntity) entity).getId());
        }
        return request.updateWithPut(entity);
    }

    public void delete(Object id) {
        crudClientRequest.delete(id);
    }

    public void delete(ApiEntity entity) {
        crudClientRequest.delete(entity.getId());
    }
}
