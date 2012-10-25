/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.cli;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;

public class DummyServletContext implements ServletContext {
	private String ctx;
	
	public DummyServletContext(String ctx) {
		this.ctx = ctx;
	}
	
	public ServletContext getContext(String uripath) {
		// stab
		return null;
	}

	public String getContextPath() {
		return ctx;
	}

	public int getMajorVersion() {
		// stab
		return 0;
	}

	public int getMinorVersion() {
		// stab
		return 0;
	}

	public int getEffectiveMajorVersion() {
		// stab
		return 0;
	}

	public int getEffectiveMinorVersion() {
		// stab
		return 0;
	}

	public String getMimeType(String file) {
		// stab
		return null;
	}

	public Set<String> getResourcePaths(String path) {
		// stab
		return null;
	}

	public URL getResource(String path)
			throws MalformedURLException {
		// stab
		return null;
	}

	public InputStream getResourceAsStream(String path) {
		// stab
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		// stab
		return null;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		// stab
		return null;
	}

	public Servlet getServlet(String name) throws ServletException {
		// stab
		return null;
	}

	public Enumeration<Servlet> getServlets() {
		// stab
		return null;
	}

	public Enumeration<String> getServletNames() {
		// stab
		return null;
	}

	public void log(String msg) {
		// stab
	}

	public void log(Exception exception, String msg) {
		// stab
	}

	public void log(String message, Throwable throwable) {
		// stab
	}

	public String getRealPath(String path) {
		// stab
		return null;
	}

	public String getServerInfo() {
		// stab
		return null;
	}

	public String getInitParameter(String name) {
		// stab
		return null;
	}

	public Enumeration<String> getInitParameterNames() {
		// stab
		return null;
	}

	public boolean setInitParameter(String name, String value) {
		// stab
		return false;
	}

	public Object getAttribute(String name) {
		// stab
		return null;
	}

	public Enumeration<String> getAttributeNames() {
		// stab
		return null;
	}

	public void setAttribute(String name, Object object) {
		// stab
	}

	public void removeAttribute(String name) {
		// stab
	}

	public String getServletContextName() {
		// stab
		return null;
	}

	public Dynamic addServlet(String servletName, String className) {
		// stab
		return null;
	}

	public Dynamic addServlet(String servletName, Servlet servlet) {
		// stab
		return null;
	}

	public Dynamic addServlet(String servletName,
			Class<? extends Servlet> servletClass) {
		// stab
		return null;
	}

	public <T extends Servlet> T createServlet(Class<T> c)
			throws ServletException {
		// stab
		return null;
	}

	public ServletRegistration getServletRegistration(
			String servletName) {
		// stab
		return null;
	}

	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// stab
		return null;
	}

	public javax.servlet.FilterRegistration.Dynamic addFilter(
			String filterName, String className) {
		// stab
		return null;
	}

	public javax.servlet.FilterRegistration.Dynamic addFilter(
			String filterName, Filter filter) {
		// stab
		return null;
	}

	public javax.servlet.FilterRegistration.Dynamic addFilter(
			String filterName, Class<? extends Filter> filterClass) {
		// stab
		return null;
	}

	public <T extends Filter> T createFilter(Class<T> c)
			throws ServletException {
		// stab
		return null;
	}

	public FilterRegistration getFilterRegistration(
			String filterName) {
		// stab
		return null;
	}

	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// stab
		return null;
	}

	public SessionCookieConfig getSessionCookieConfig() {
		// stab
		return null;
	}

	public void setSessionTrackingModes(
			Set<SessionTrackingMode> sessionTrackingModes)
			throws IllegalStateException, IllegalArgumentException {
		// stab
	}

	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// stab
		return null;
	}

	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// stab
		return null;
	}

	public void addListener(
			Class<? extends EventListener> listenerClass) {
		// stab
	}

	public void addListener(String className) {
		// stab
	}

	public <T extends EventListener> void addListener(T t) {
		// stab
	}

	public <T extends EventListener> T createListener(Class<T> c)
			throws ServletException {
		// stab
		return null;
	}

	public void declareRoles(String... roleNames) {
		// stab
	}

	public ClassLoader getClassLoader() {
		// stab
		return null;
	}

	public JspConfigDescriptor getJspConfigDescriptor() {
		// stab
		return null;
	}
}
