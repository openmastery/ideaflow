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
package org.ideaflow.common.rest.client;

import com.bancvue.rest.client.request.ClientRequest;
import com.bancvue.rest.client.response.AbstractResponse;
import com.bancvue.rest.client.response.CreateResponse;
import com.bancvue.rest.client.response.DeleteResponse;
import com.bancvue.rest.client.response.GetResponse;
import com.bancvue.rest.client.response.UpdateResponse;
import org.ideaflow.common.rest.api.ApiEntity;
import org.ideaflow.common.rest.client.response.ResponseInspector;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * An adapter for {@link com.blackbaud.rest.client.request.ClientRequest} which encapsulates invoking a request
 * and unwrapping the response.
 *
 * The constructor accepts the target resource type, which is then used to construct GenericType objects used
 * to deserialize and unwrap the response.  One limitation of this is that only simple, non-generic types are
 * supported.  If this limitation becomes problematic, there is a second, private constructor which allows
 * specifying the GenericType instances which will be used to deserialize the responses.
 */
public class CrudClientRequest<T> {

    private static final GenericTypeFactory GENERIC_TYPE_FACTORY = GenericTypeFactory.getInstance();

    private ClientRequest clientRequest;
    private GenericType<T> entity;
    private GenericType<List<T>> entityList;
    private ResponseInspector responseInspector;

    @SuppressWarnings("unchecked")
    public CrudClientRequest(ClientRequest clientRequest, Class<T> type) {
        this.clientRequest = clientRequest;
        this.entity = GENERIC_TYPE_FACTORY.createGenericType(type);
        this.entityList = GENERIC_TYPE_FACTORY.createGenericType(List.class, type);
    }

    private CrudClientRequest(ClientRequest clientRequest, GenericType<T> entity,
                              GenericType<List<T>> entityList, ResponseInspector responseInspector) {
        this.clientRequest = clientRequest;
        this.entity = entity;
        this.entityList = entityList;
        this.responseInspector = responseInspector;
    }

    public ClientRequest getClientRequest() {
        return clientRequest;
    }

    public CrudClientRequest<T> accept(MediaType ... mediaTypes) {
        return createCrudClientRequest(clientRequest.accept(mediaTypes));
    }

    public CrudClientRequest<T> request(MediaType ... mediaTypes) {
        return createCrudClientRequest(clientRequest.request(mediaTypes));
    }

    public CrudClientRequest<T> entityType(MediaType entityType) {
        return createCrudClientRequest(clientRequest.entityType(entityType));
    }

    @SuppressWarnings("unchecked")
    public CrudClientRequest<T> entity(Class type) {
        GenericType entity = GENERIC_TYPE_FACTORY.createGenericType(type);
        GenericType entityList = GENERIC_TYPE_FACTORY.createGenericType(List.class, type);
        return new CrudClientRequest<>(clientRequest, entity, entityList, responseInspector);
    }

    public CrudClientRequest<T> path(ApiEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Entity id must not be null");
        }

        return path(entity.getId());
    }

    public CrudClientRequest<T> path(Object segment) {
        if (segment == null) {
            throw new IllegalArgumentException("Segment must not be null");
        }

        String pathString = segment.toString();
        return createCrudClientRequest(clientRequest.path(pathString));
    }

    public CrudClientRequest<T> queryParam(String name, Object... values) {
        return createCrudClientRequest(clientRequest.queryParam(name, values));
    }

    public CrudClientRequest<T> header(Header header) {
        return header(header.getName(), header.getValue());
    }

    public CrudClientRequest<T> header(String name, Object value) {
        return createCrudClientRequest(clientRequest.header(name, value));
    }

    public CrudClientRequest<T> property(String name, Object value) {
        return createCrudClientRequest(clientRequest.property(name, value));
    }

    public CrudClientRequest<T> cookie(String name, String value) {
        return createCrudClientRequest(clientRequest.cookie(name, value));
    }

    public CrudClientRequest<T> cookie(Cookie cookie) {
        return createCrudClientRequest(clientRequest.cookie(cookie));
    }

    private CrudClientRequest<T> createCrudClientRequest(ClientRequest clientRequest) {
        return new CrudClientRequest<>(clientRequest, entity, entityList, responseInspector);
    }

    public CrudClientRequest<T> responseInspector(ResponseInspector responseInspector) {
        return new CrudClientRequest<>(clientRequest, entity, entityList, responseInspector);
    }

    private void setClientResponse(AbstractResponse abstractResponse) {
        if (responseInspector != null) {
            responseInspector.setResponse(abstractResponse.getClientResponse());
        }
    }

    /**
     * Invokes HTTP GET method for the current request, returning a single entity.
     */
    public T find() {
        GetResponse response = clientRequest.get();
        setClientResponse(response);
        return response.getValidatedResponse(entity);
    }

    /**
     * Invokes HTTP GET method for the current request, modifying the request path
     * with the input path and returning a single entity.
     */
    public T find(Object path) {
        return path(path).find();
    }

    /**
     * Invokes HTTP GET method for the current request, returning a list of entities.
     */
    public List<T> findMany() {
        GetResponse response = clientRequest.get();
        setClientResponse(response);
        return response.getValidatedResponse(entityList);
    }

    /**
     * Invokes HTTP POST method for the current request and the input entity.
     */
    public T createWithPost(Object entity) {
        CreateResponse response = clientRequest.createWithPost(entity);
        setClientResponse(response);
        return response.getValidatedResponse(this.entity);
    }

    /**
     * Invokes HTTP POST method for the given input entity, modifying the request
     * path with the input path.
     */
    public T createWithPost(Object path, Object entity) {
        return path(path).createWithPost(entity);
    }

    /**
     * Invokes HTTP PUT method for the current request and the input entity.
     */
    public T updateWithPut(Object entity) {
        UpdateResponse response = clientRequest.updateWithPut(entity);
        setClientResponse(response);
        return response.getValidatedResponse(this.entity);
    }

    /**
     * Invokes HTTP PUT method for the given input entity, modifying the request
     * path with the input path.
     */
    public T updateWithPut(Object path, Object entity) {
        return path(path).updateWithPut(entity);
    }

    /**
     * Invokes HTTP DELETE method for the current request.
     */
    public void delete() {
        DeleteResponse response = clientRequest.delete();
        setClientResponse(response);
        response.getValidatedResponse(String.class);
    }

    /**
     * Invokes HTTP DELETE method for the current request, modifying the request path
     * with the input path.
     */
    public void delete(Object path) {
        path(path).delete();
    }

}
