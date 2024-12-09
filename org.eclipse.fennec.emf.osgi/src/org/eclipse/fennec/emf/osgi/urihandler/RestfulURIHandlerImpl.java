/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.urihandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipse.fennec.emf.osgi.constants.EMFUriHandlerConstants;

/**
 * URI Handler with basic authentication
 * 
 * @author Juergen Albert
 * @since 17.12.2012
 */
public class RestfulURIHandlerImpl extends URIHandlerImpl {

	/** ERROR_WITH_RESPONSE_CODE */
	private static final String ERROR_WITH_RESPONSE_CODE = " failed with HTTP response code ";
	/** ERROR_LAST_MODIFIED */
	private static final String ERROR_LAST_MODIFIED = "Error reading last modified header from the response";
	/** SCHEMA_HTTPS */
	private static final String SCHEMA_HTTPS = "https";
	/** SCHEMA_HTTP */
	private static final String SCHEMA_HTTP = "http";
	/** HEADER_CONTENT_LENGTH */
	private static final String HEADER_CONTENT_LENGTH = "Content-Length";
	/** HTTP_HEAD */
	private static final String HTTP_HEAD = "HEAD";
	/** HEADER_ALLOW */
	private static final String HEADER_ALLOW = "Allow";
	/** HTTP_OPTIONS */
	private static final String HTTP_OPTIONS = "OPTIONS";
	/** HTTP_DELETE */
	private static final String HTTP_DELETE = "DELETE";
	/** PROP_HTTP_RESPONSE_CODE */
	private static final String PROP_HTTP_RESPONSE_CODE = "HTTPResponseCode";
	/** HEADER_LAST_MODIFIED */
	private static final String HEADER_LAST_MODIFIED = "Last-Modified";
	/** HEADER_CONTENT_CLASS */
	private static final String HEADER_CONTENT_CLASS = "Content-Class";
	/** PROP_ECLASS */
	private static final String PROP_ECLASS = "EClass";
	/** HTTP_PUT */
	private static final String HTTP_PUT = "PUT";
	private static final Logger LOG = Logger.getLogger(RestfulURIHandlerImpl.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#createOutputStream(org.
	 * eclipse.emf.common.util.URI, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OutputStream createOutputStream(URI uri, final Map<?, ?> options) throws IOException {
		URL url = new URL(uri.toString());
		final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		String method = HTTP_PUT;
		if (options.containsKey(EMFUriHandlerConstants.OPTION_HTTP_METHOD)) {
			method = options.get(EMFUriHandlerConstants.OPTION_HTTP_METHOD).toString().toUpperCase();
		}
		httpURLConnection.setRequestMethod(method);
		setTimeout(httpURLConnection, options);
		httpURLConnection.setDoOutput(Boolean.TRUE);
		setRequestHeaders(httpURLConnection,
				(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
		if (options.containsKey(PROP_ECLASS)) {
			httpURLConnection.setRequestProperty(HEADER_CONTENT_CLASS, options.get(PROP_ECLASS).toString());
		}
		return new FilterOutputStream(httpURLConnection.getOutputStream()) {
			@Override
			public void close() throws IOException {
				super.close();
				try {
					int responseCode = httpURLConnection.getResponseCode();
					Map<Object, Object> response = getResponse(options);
					if (response != null) {
						setLastModified(httpURLConnection, response);
						response.put(PROP_HTTP_RESPONSE_CODE, responseCode);
						response.putAll(httpURLConnection.getHeaderFields());
					}
					InputStream in = extreactStreamAndLogResponse(options, httpURLConnection);
					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
					case HttpURLConnection.HTTP_CREATED:
						Resource responseResource = (Resource) options
								.get(EMFUriHandlerConstants.OPTIONS_EXPECTED_RESPONSE_RESOURCE);
						if (responseResource != null) {
							responseResource.load(in, (Map<?, ?>) options
									.get(EMFUriHandlerConstants.OPTIONS_EXPECTED_RESPONSE_RESOURCE_OPTIONS));
						}
						break;
					case HttpURLConnection.HTTP_NO_CONTENT: {
						break;
					}
					default: {
						throw new IOException(httpURLConnection.getRequestMethod() + ERROR_WITH_RESPONSE_CODE
								+ responseCode);
					}
					}
				} finally {
					httpURLConnection.disconnect();
				}
			}
		};
	}

	/**
	 * Sets the given headers to the url connection
	 * 
	 * @param httpURLConnection
	 * @param headers
	 */
	private void setRequestHeaders(HttpURLConnection httpURLConnection, Map<String, String> headers) {
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#createInputStream(org.
	 * eclipse.emf.common.util.URI, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
		try {
			URL url = new URL(uri.toString());
			final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			setTimeout(httpURLConnection, options);
			setRequestHeaders(httpURLConnection,
					(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
			final int responseCode = httpURLConnection.getResponseCode();
			Map<Object, Object> response = getResponse(options);
			if (response != null) {
				setLastModified(httpURLConnection, response);
			}
			InputStream result = extreactStreamAndLogResponse(options, httpURLConnection);
			return new FilterInputStream(result) {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.io.FilterInputStream#read()
				 */
				@Override
				public int read() throws IOException {
					if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
						return -1;
					}
					return super.read();
				}

				@Override
				public void close() throws IOException {
					super.close();

					int responseCode = httpURLConnection.getResponseCode();
					httpURLConnection.disconnect();
					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
					case HttpURLConnection.HTTP_CREATED:
					case HttpURLConnection.HTTP_NO_CONTENT: {
						break;
					}
					default: {
						throw new IOException(httpURLConnection.getRequestMethod() + ERROR_WITH_RESPONSE_CODE
								+ responseCode);
					}
					}
				}

			};
		} catch (RuntimeException exception) {
			throw new Resource.IOWrappedException(exception);
		}
	}

	/**
	 * @param httpURLConnection
	 * @param response
	 */
	private void setLastModified(final HttpURLConnection httpURLConnection, Map<Object, Object> response) {
		try {
			String lastModified = httpURLConnection.getHeaderField(HEADER_LAST_MODIFIED);
			if (lastModified != null) {
				Long lm = Long.parseLong(lastModified);
				response.put(URIConverter.RESPONSE_TIME_STAMP_PROPERTY, lm);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, ERROR_LAST_MODIFIED, e);
		}
	}

	/**
	 * @param options
	 * @param httpURLConnection
	 * @return
	 * @throws IOException
	 */
	private InputStream extreactStreamAndLogResponse(Map<?, ?> options, final HttpURLConnection httpURLConnection)
			throws IOException {
		InputStream result = httpURLConnection.getErrorStream();
		if (result == null) {
			result = httpURLConnection.getInputStream();
		}
		if (Boolean.TRUE.equals(options.get(EMFUriHandlerConstants.OPTIONS_LOG_RESPONSE))) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = result.read();
			while (read != -1) {
				baos.write(read);
				read = result.read();
			}
			byte[] responseArray = baos.toByteArray();
			baos.close();
			result = new ByteArrayInputStream(responseArray);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#delete(org.eclipse.emf.
	 * common.util.URI, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException {
		try {
			URL url = new URL(uri.toString());
			final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			setTimeout(httpURLConnection, options);
			httpURLConnection.setDoOutput(true);
			setRequestHeaders(httpURLConnection,
					(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
			httpURLConnection.setRequestMethod(HTTP_DELETE);
			int responseCode = httpURLConnection.getResponseCode();
			httpURLConnection.disconnect();
			switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
			case HttpURLConnection.HTTP_ACCEPTED:
			case HttpURLConnection.HTTP_NO_CONTENT: {
				break;
			}
			default: {
				throw new IOException(
						httpURLConnection.getRequestMethod() + ERROR_WITH_RESPONSE_CODE + responseCode);
			}
			}
		} catch (RuntimeException exception) {
			throw new Resource.IOWrappedException(exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#getAttributes(org.eclipse.
	 * emf.common.util.URI, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
		Map<String, Object> result = new HashMap<>();
		Set<String> requestedAttributes = getRequestedAttributes(options);
		try {
			URL url = new URL(uri.toString());
			URLConnection urlConnection = null;
			if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY)) {

				urlConnection = url.openConnection();
				setTimeout(urlConnection, options);
				if (urlConnection instanceof HttpURLConnection) {
					HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
					httpURLConnection.setRequestMethod(HTTP_OPTIONS);
					setRequestHeaders(httpURLConnection,
							(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
					int responseCode = httpURLConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						String allow = httpURLConnection.getHeaderField(HEADER_ALLOW);
						result.put(URIConverter.ATTRIBUTE_READ_ONLY, allow == null || !allow.contains(HTTP_PUT));
					}
					urlConnection = null;
				} else {
					result.put(URIConverter.ATTRIBUTE_READ_ONLY, true);
				}
			}

			if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_TIME_STAMP)) {
				if (urlConnection == null) {
					urlConnection = url.openConnection();
					setTimeout(urlConnection, options);
					if (urlConnection instanceof HttpURLConnection) {
						HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
						setRequestHeaders(httpURLConnection,
								(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
						httpURLConnection.setRequestMethod(HTTP_HEAD);
						httpURLConnection.getResponseCode();
					}
				}
				if (urlConnection.getHeaderField(HEADER_LAST_MODIFIED) != null) {
					result.put(URIConverter.ATTRIBUTE_TIME_STAMP, urlConnection.getLastModified());
				}
			}

			if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_LENGTH)) {
				if (urlConnection == null) {
					urlConnection = url.openConnection();
					setTimeout(urlConnection, options);
					if (urlConnection instanceof HttpURLConnection) {
						HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
						setRequestHeaders(httpURLConnection,
								(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
						httpURLConnection.setRequestMethod(HTTP_HEAD);
						httpURLConnection.getResponseCode();
					}
				}
				if (urlConnection.getHeaderField(HEADER_CONTENT_LENGTH) != null) {
					result.put(URIConverter.ATTRIBUTE_LENGTH, urlConnection.getContentLength());
				}
			}
		} catch (IOException exception) {
			// Ignore exceptions.
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#exists(org.eclipse.emf.
	 * common.util.URI, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean exists(URI uri, Map<?, ?> options) {
		try {
			URL url = new URL(uri.toString());
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			setTimeout(httpURLConnection, options);
			httpURLConnection.setRequestMethod(HTTP_HEAD);
			setRequestHeaders(httpURLConnection,
					(Map<String, String>) options.get(EMFUriHandlerConstants.OPTION_HTTP_HEADERS));
			int responseCode = httpURLConnection.getResponseCode();
			Map<Object, Object> response = getResponse(options);
			if (response != null) {
				setLastModified(httpURLConnection, response);
			}
			httpURLConnection.disconnect();
			return responseCode == HttpURLConnection.HTTP_OK;
		} catch (Exception exception) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.ecore.resource.impl.URIHandlerImpl#canHandle(org.eclipse.emf.
	 * common.util.URI)
	 */
	@Override
	public boolean canHandle(URI uri) {
		return uri.scheme().equalsIgnoreCase(SCHEMA_HTTP) || uri.scheme().equalsIgnoreCase(SCHEMA_HTTPS);
	}

	/**
	 * Returns the value of the {@link URIConverter#OPTION_TIMEOUT timeout option}.
	 * 
	 * @param options the options in which to look for the timeout option.
	 * @return the value of the timeout option, or <code>3000</code> if not present.
	 */
	@Override
	protected int getTimeout(Map<?, ?> options) {
		Integer timeout = (Integer) options.get(URIConverter.OPTION_TIMEOUT);
		return timeout == null ? 3000 : timeout.intValue();
	}

	protected void setTimeout(URLConnection connection, Map<?, ?> options) {
		int timeout = getTimeout(options);
		if (timeout != 0) {
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
		}
	}
	
}