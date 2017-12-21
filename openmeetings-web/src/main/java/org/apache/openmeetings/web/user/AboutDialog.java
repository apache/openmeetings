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
package org.apache.openmeetings.web.user;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.Version.getBuildDate;
import static org.apache.openmeetings.util.Version.getRevision;
import static org.apache.openmeetings.util.Version.getVersion;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class AboutDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;

	public AboutDialog(String id) {
		super(id, "");

		add(new Label("name", getApplicationName()));
		add(new Label("version", getVersion()));
		add(new Label("revision", getRevision()));
		add(new Label("buildDate", getBuildDate()));
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("1549"));
		super.onInitialize();
	}

	@Override
	protected List<DialogButton> getButtons() {
		return new ArrayList<>();
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton arg1) {
		//no-op
	}
}
