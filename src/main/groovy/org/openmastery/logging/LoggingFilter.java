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
package org.openmastery.logging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.message.MessageUtils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Provider
@Priority(Priorities.AUTHORIZATION + 1)
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter, WriterInterceptor {

	public static final String LOG_TRACE = "trace";
	public static final String LOG_ERROR = "error";
	public static final String LOG_DEBUG = "debug";
	public static final String LOG_WARN = "warn";

	private static final String ENTITY_LOGGER_PROPERTY = LoggingFilter.class.getName() + ".entityLogger";
	private static final String ENTITY_TYPE_PROPERTY = "entityType";
	private static final String ENTITY_RESPONSE = "response";
	private static final String ENTITY_REQUEST = "request";
	private static final String LOG_LEVEL_PROPERTY = "logLevel";
	private static final List headersKeysToLog = Arrays.asList("user-agent", "referer");

	private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;

	private final int maxEntitySize;

	public LoggingFilter() {
		this.maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;
	}

	@Override
	public void filter(ContainerRequestContext context) throws IOException {
		logRequest("Server request: ", context.getMethod(), context.getUriInfo().getRequestUri());
		logHeaders(context.getHeaders());
		logRequestPayload(context);
	}

	private void logRequestPayload(ContainerRequestContext context) throws IOException {
		String logLevel = getLogLevelBasedOnRequestMethod(context.getMethod());
		if (isLogLevelEnabled(logLevel) && context.hasEntity()) {
			StringBuilder requestPayload = new StringBuilder().append(ENTITY_REQUEST).append(" ");
			context.setEntityStream(logInboundEntity(requestPayload, context.getEntityStream(), MessageUtils.getCharset(context.getMediaType())));
			logAtLevel(logLevel, requestPayload.toString());
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		logResponse("Server response: ", responseContext.getStatus(), requestContext.getMethod(), requestContext.getUriInfo().getRequestUri());
		logResponsePayload(requestContext, responseContext);
	}

	private void logResponsePayload(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		if (responseContext.hasEntity()) {
			StringBuilder responsePayload = new StringBuilder();
			OutputStream stream = new LoggingStream(responsePayload, responseContext.getEntityStream());
			responseContext.setEntityStream(stream);
			requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
			requestContext.setProperty(LOG_LEVEL_PROPERTY, getResponsePayloadLogLevel(responseContext, requestContext.getMethod()));
			requestContext.setProperty(ENTITY_TYPE_PROPERTY, ENTITY_RESPONSE);
			// not calling log here - it will be called by the interceptor
		}
	}

	@Override
	public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
		LoggingStream stream = (LoggingStream) writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
		writerInterceptorContext.proceed();
		String logLevel = (String) writerInterceptorContext.getProperty(LOG_LEVEL_PROPERTY);
		if (stream != null && isLogLevelEnabled(logLevel)) {
			StringBuilder payload = new StringBuilder((String) writerInterceptorContext.getProperty(ENTITY_TYPE_PROPERTY)).append(" ")
					.append(stream.getStringBuilder(MessageUtils.getCharset(writerInterceptorContext.getMediaType())).toString());
			logAtLevel(logLevel, payload.toString());
		}
	}

	private String getResponsePayloadLogLevel(ContainerResponseContext responseContext, String method) {
		if (isErrorStatus(responseContext.getStatusInfo())) {
			return LOG_ERROR;
		} else if (isWarnStatus(responseContext.getStatusInfo())) {
			return LOG_WARN;
		} else {
			return getLogLevelBasedOnRequestMethod(method);
		}
	}

	private boolean isErrorStatus(Response.StatusType status) {
		return status.getFamily() == Response.Status.Family.SERVER_ERROR;
	}

	private boolean isWarnStatus(Response.StatusType status) {
		return (status != Response.Status.NOT_FOUND) && (status.getFamily() == Response.Status.Family.CLIENT_ERROR);
	}

	private String getLogLevelBasedOnRequestMethod(String method) {
		return "GET".equals(method) ? LOG_TRACE : LOG_DEBUG;
	}

	private boolean isLogLevelEnabled(String logLevel) {
		switch (logLevel) {
			case LOG_DEBUG:
				return log.isDebugEnabled();
			case LOG_ERROR:
				return log.isErrorEnabled();
			case LOG_TRACE:
				return log.isTraceEnabled();
			default:
				return false;
		}
	}

	private void logAtLevel(String logLevel, String logStatement) {
		if (logLevel.equals(LOG_TRACE)) {
			log.trace(logStatement);
		} else if (logLevel.equals(LOG_ERROR)) {
			log.error(logStatement);
		} else {
			log.debug(logStatement);
		}
	}

	private void logRequest(String note, String method, URI uri) {
		if (log.isInfoEnabled()) {
			StringBuilder requestBuilder = new StringBuilder();
			requestBuilder.append(note);
			appendRequestUri(requestBuilder, method, uri);
			log.info(requestBuilder.toString());
		}
	}

	private void logResponse(String note, int status, String method, URI uri) {
		if (log.isInfoEnabled()) {
			StringBuilder responseBuilder = new StringBuilder();
			responseBuilder.append(note);
			appendRequestUri(responseBuilder, method, uri);
			responseBuilder.append(" < ").append(Integer.toString(status));
			log.info(responseBuilder.toString());
		}
	}

	private void appendRequestUri(StringBuilder b, String method, URI uri) {
		b.append(method).append(" ").append(uri.toASCIIString());
	}

	private void logHeaders(MultivaluedMap<String, String> allHeaders) {
		if (log.isDebugEnabled()) {
			List<Map.Entry<String, List<String>>> headersToLog = new ArrayList<>();

			for (Map.Entry<String, List<String>> headerEntry : allHeaders.entrySet()) {
				if (headersKeysToLog.contains(headerEntry.getKey())) {
					headersToLog.add(headerEntry);
				}
			}

			if (!headersToLog.isEmpty()) {
				log.debug("Headers: " + headersToLog.toString());
			}
		}
	}

	private InputStream logInboundEntity(StringBuilder b, InputStream stream, Charset charset) throws IOException {
		if (!stream.markSupported()) {
			stream = new BufferedInputStream(stream);
		}
		stream.mark(maxEntitySize + 1);
		byte[] entity = new byte[maxEntitySize + 1];
		int entitySize = stream.read(entity);
		entitySize = entitySize < 0 ? 0 : entitySize;
		b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
		if (entitySize > maxEntitySize) {
			b.append("...more...");
		}
		b.append('\n');
		stream.reset();
		return stream;
	}

	@AllArgsConstructor
	private class LoggingStream extends OutputStream {
		private final StringBuilder b;
		private final OutputStream inner;
		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		StringBuilder getStringBuilder(Charset charset) {
			// write entity to the builder
			byte[] entity = baos.toByteArray();

			b.append(new String(entity, 0, Math.min(entity.length, maxEntitySize), charset));
			if (entity.length > maxEntitySize) {
				b.append("...more...");
			}
			b.append('\n');

			return b;
		}

		@Override
		public void write(int i) throws IOException {
			if (baos.size() <= maxEntitySize) {
				baos.write(i);
			}
			inner.write(i);
		}
	}
}
