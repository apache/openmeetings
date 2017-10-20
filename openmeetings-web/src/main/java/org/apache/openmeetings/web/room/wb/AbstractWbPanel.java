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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.PARAM_OBJ;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.io.IOException;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.StringValue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONObject;

public abstract class AbstractWbPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(AbstractWbPanel.class, getWebAppRootKey());
	public static final String FUNC_ACTION = "wbAction";
	public static final String PARAM_ACTION = "action";
	protected static final String ROLE_NONE = "none";
	protected final RoomPanel rp;
	protected boolean inited = false;
	private final AbstractDefaultAjaxBehavior wbAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			updateWbActionAttributes(attributes);
		}

		@Override
		protected void respond(AjaxRequestTarget target) {
			if (!inited) {
				return;
			}
			try {
				WbAction a = WbAction.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString());
				StringValue sv = getRequest().getRequestParameters().getParameterValue(PARAM_OBJ);
				JSONObject obj = sv.isEmpty() ? new JSONObject() : new JSONObject(sv.toString());
				processWbAction(a, obj, target);
			} catch (Exception e) {
				log.error("Unexpected error while processing wbAction", e);
			}
		}
	};

	public AbstractWbPanel(String id, RoomPanel rp) {
		super(id);
		this.rp = rp;
		setOutputMarkupId(true);
		add(wbAction);
	}

	public CharSequence getInitScript() {
		StringBuilder sb = new StringBuilder("WbArea.init();");
		internalWbLoad(sb);
		inited = true;
		return sb;
	}

	public AbstractWbPanel update(IPartialPageRequestHandler handler) {
		if (inited && handler != null) {
			handler.appendJavaScript(String.format("Room.setSize();WbArea.setRole('%s');", getRole()));
		}
		return this;
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

	/**
	 * This method allows to set additional attributes to wbAction
	 *
	 * @param attributes - attributes to set
	 */
	protected void updateWbActionAttributes(AjaxRequestAttributes attributes) {}

	protected abstract void processWbAction(WbAction a, JSONObject obj, AjaxRequestTarget target) throws IOException;

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_ACTION, wbAction, explicit(PARAM_ACTION), explicit(PARAM_OBJ))));
	}
}
