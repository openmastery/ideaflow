package org.openmastery.rest.client.response;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResponseInspector {

    private Response response;

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public Cookie getCookie(String name) {
        Cookie cookie = null;
        if (response != null) {
            Map<String, NewCookie> cookies = response.getCookies();
            if (cookies != null) {
                cookie = cookies.get(name);
            }
        }
        return cookie;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getHeaders(String name) {
        List<T> result = Collections.EMPTY_LIST;
        if (response != null) {
            MultivaluedMap<String, Object> headers = response.getHeaders();
            if (headers != null) {
                result = (List<T>) headers.get(name);
            }
        }
        return result;
    }

    public <T> T getHeader(String name) {
        List<T> headers = getHeaders(name);
        return headers.isEmpty() ? null : headers.get(0);
    }

}
