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
package org.apache.openmeetings.service.mail.template;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;

public class OmTextLabel extends Label {
	private static final long serialVersionUID = 1L;
	private static final String WICKET_VISIBLE = ":visible";

	public OmTextLabel(String id, long key, long langId) {
		this(id, "" + key, langId);
	}

	public OmTextLabel(String id, String key, long langId) {
		this(id, ensureApplication().getOmString(key, langId));
	}

	public OmTextLabel(String id, String label) {
		super(id, label);
		setRenderBodyOnly(true);
	}

	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		final String vis = openTag.getAttribute(markupStream.getWicketNamespace() + WICKET_VISIBLE);
		if (vis != null && Boolean.FALSE.equals(Boolean.valueOf(vis))) {
			//skip the body
			return;
		}
		super.onComponentTagBody(markupStream, openTag);
	}
}
