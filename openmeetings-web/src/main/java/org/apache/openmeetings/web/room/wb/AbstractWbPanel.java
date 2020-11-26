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
package org.apache.openmeetings.web.room.wb;

import java.io.IOException;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.panel.Panel;

import com.github.openjson.JSONObject;

public abstract class AbstractWbPanel extends Panel {
	private static final long serialVersionUID = 1L;
	protected static final String ROLE_NONE = "NONE";
	protected final RoomPanel rp;
	protected boolean inited = false;

	protected AbstractWbPanel(String id, RoomPanel rp) {
		super(id);
		this.rp = rp;
		setOutputMarkupId(true);
	}

	public CharSequence getInitScript() {
		StringBuilder sb = new StringBuilder("WbArea.init(() => {");
		internalWbLoad(sb);
		inited = true;
		sb.append("});");
		return sb;
	}

	public AbstractWbPanel update(IPartialPageRequestHandler handler) {
		if (inited && handler != null) {
			handler.appendJavaScript(String.format("WbArea.setRole('%s');", getRole()));
		}
		return this;
	}

	/**
	 * This method have to be overridden to handle reload WB
	 *
	 * @param handler {@link IPartialPageRequestHandler} to perform necessary actions
	 */
	public void reloadWb(IPartialPageRequestHandler handler) {
		//designed to be empty
	}

	protected abstract String getRole();

	/**
	 * Internal method to perform JS actions on WB load
	 *
	 * @param sb - {@link StringBuilder} to put JS calls
	 */
	void internalWbLoad(StringBuilder sb) {}

	/**
	 * This method being called when file is dropped to WB
	 *
	 * @param fi - File being dropped
	 * @param clean - should WB be cleaned up
	 */
	public void sendFileToWb(final BaseFileItem fi, boolean clean) {}

	public abstract void processWbAction(WbAction a, JSONObject obj, IPartialPageRequestHandler handler) throws IOException;
}
