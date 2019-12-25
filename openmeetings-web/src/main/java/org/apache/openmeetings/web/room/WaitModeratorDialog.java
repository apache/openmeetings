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
package org.apache.openmeetings.web.room;

import java.util.List;

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class WaitModeratorDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;
	private final boolean autoopen;

	public WaitModeratorDialog(String id, String title, boolean autoopen) {
		super(id, title);
		this.autoopen = autoopen;
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("classes", "{'ui-dialog-titlebar': 'ui-corner-all no-close'}");
		behavior.setOption("closeOnEscape", false);
		behavior.setOption("autoOpen", autoopen);
		behavior.setOption("appendTo", Options.asString(".room-block"));
	}

	@Override
	protected List<DialogButton> getButtons() {
		return List.of();
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		// no-op
	}
}
