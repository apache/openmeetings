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
package org.apache.openmeetings.web.util;

import java.util.UUID;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * see: <a href="https://cwiki.apache.org/confluence/display/WICKET/AJAX+update+and+file+download+in+one+blow">
 * https://cwiki.apache.org/confluence/display/WICKET/AJAX+update+and+file+download+in+one+blow</href>
 * 
 */
public class AjaxDownload extends AbstractAjaxBehavior {
	private static final long serialVersionUID = 1L;
	private boolean addAntiCache;
	private String fileName;
	private IResourceStream resourceStream;
	private final String iframeId;

	public AjaxDownload() {
		this(true);
	}

	public AjaxDownload(boolean addAntiCache) {
		super();
		this.addAntiCache = addAntiCache;
		iframeId = String.format("download-iframe-%s", UUID.randomUUID().toString());
	}

	/**
	 * Call this method to initiate the download.
	 */
	public void initiate(AjaxRequestTarget target) {
		StringBuilder url = new StringBuilder(getCallbackUrl());

		if (addAntiCache) {
			url.append(url.indexOf("?") > -1 ? "&" : "?")
				.append("antiCache=").append(System.currentTimeMillis());
		}
		target.appendJavaScript(String.format("$('#%s').attr('src', '%s');", iframeId, url.toString()));
	}

	@Override
	protected void onBind() {
		super.onBind();
		// it is impossible to get page by id anyway
		if (!(getComponent() instanceof Page)) {
			getComponent().setOutputMarkupId(true);
		}
	}

	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(AjaxDownload.class, "ajax-download.js");
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(newResourceReference()));
		response.render(OnDomReadyHeaderItem.forScript(String.format("addDwnldIframe('%s', '%s');", component instanceof Page ? "" : component.getMarkupId(), iframeId)));
	}

	@Override
	public void onRequest() {
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(getResourceStream(), getFileName());
		handler.setContentDisposition(getContentDisposition());
		getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
	}

	protected ContentDisposition getContentDisposition() {
		return ContentDisposition.ATTACHMENT;
	}
	/**
	 * Override this method for a file name which will let the browser prompt
	 * with a save/open dialog.
	 * 
	 * @see ResourceStreamRequestTarget#getFileName()
	 */
	protected String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Hook method providing the actual resource stream.
	 */
	protected IResourceStream getResourceStream() {
		return resourceStream;

	}

	public void setResourceStream(IResourceStream resourceStream) {
		this.resourceStream = resourceStream;
	}

}
