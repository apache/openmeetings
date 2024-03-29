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

import static org.apache.openmeetings.util.OmVersion.getBuildDate;
import static org.apache.openmeetings.util.OmVersion.getRevision;
import static org.apache.openmeetings.util.OmVersion.getVersion;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class AboutDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;

	public AboutDialog(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("1549"));
		super.onInitialize();
		add(new Label("name", getApplicationName()));
		add(new Label("version", getVersion()));
		add(new Label("revision", getRevision()));
		add(new Label("buildDate", getBuildDate()));
	}
}
