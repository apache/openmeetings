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
package org.apache.openmeetings.web.common.upload;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMaxUploadSize;

import java.text.DecimalFormat;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.util.upload.BootstrapFileUploadBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.lang.Bytes;

public abstract class UploadForm extends Panel {
	private static final long serialVersionUID = 1L;
	private final String action;
	protected final WebMarkupContainer form = new WebMarkupContainer("form");

	protected UploadForm(String id, String action) {
		super(id);
		this.action = action;
		setRenderBodyOnly(true);
	}

	@Override
	protected void onInitialize() {
		final MainPanel mainPanel = findParent(MainPanel.class);

		add(form.add(AttributeModifier.append("data-max-size", getMaxUploadSize()))
				.add(AttributeModifier.append("data-max-size-lbl", Bytes.bytes(getMaxUploadSize()).toString(WebSession.get().getLocale())))
				.add(AttributeModifier.append("data-upload-lbl", getString(buttonLabelKey())))
				.add(AttributeModifier.append("action", action))
				.setOutputMarkupId(true)
				.setOutputMarkupPlaceholderTag(true));
		form.add(new WebMarkupContainer("sid")
				.add(AttributeModifier.append("value", mainPanel.getClient().getSid()))
				.setOutputMarkupId(true));
		WebMarkupContainer file = new WebMarkupContainer("file");
		if (allowMultiple()) {
			file.add(AttributeModifier.append("multiple", "multiple"));
		}
		form.add(file);

		form.add(new WebMarkupContainer("desc-block").setVisible(showDescBlock()));
		// set max upload size in form as info text
		Long maxBytes = getMaxUploadSize();
		double megaBytes = maxBytes.doubleValue() / 1024 / 1024;
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		form.add(new Label("MaxUploadSize", formatter.format(megaBytes)));
		form.add(new Label("btn-label", new ResourceModel(buttonLabelKey())));

		add(new WebMarkupContainer("progress-title")
				.add(AttributeModifier.append("data-processing-lbl", getString(processingLabelKey()))));
		add(BootstrapFileUploadBehavior.getInstance());
		super.onInitialize();
	}

	public void show(IPartialPageRequestHandler handler) {
		handler.add(form.setVisible(true));
		handler.appendJavaScript("Upload.bindUpload(\"" + uploadLocation() + "\", "+ extraBindFunc() + ", " + onCompleteFunc() + ");");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(UploadForm.class, "upload.js"))));
	}

	protected abstract String uploadLocation();

	protected String extraBindFunc() {
		return "null";
	}

	protected String onCompleteFunc() {
		return "null";
	}

	protected boolean allowMultiple() {
		return true;
	}

	protected boolean showDescBlock() {
		return true;
	}

	protected String buttonLabelKey() {
		return "593";
	}

	protected String processingLabelKey() {
		return "upload.dlg.convert.title";
	}
}
