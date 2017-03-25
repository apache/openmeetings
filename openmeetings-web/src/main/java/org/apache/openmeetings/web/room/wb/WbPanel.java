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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.AttributeModifier.append;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.apache.openmeetings.core.data.whiteboard.WhiteboardCache;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class WbPanel extends Panel {
	private static final long serialVersionUID = 1L;
	public static final String FUNC_ACTION = "wbAction";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_OBJ = "obj";
	private final static ResourceReference WB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "wb.js");
	private final static ResourceReference FABRIC_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "fabric.js");
	private boolean readOnly = true;
	private final Long roomId;
	private final RoomPanel rp;
	private enum Action {
		createWb
		, removeWb
		, createObj
		, modifyObj
		, deleteObj
	}
	private final AbstractDefaultAjaxBehavior wbAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				Action a = Action.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString());
				StringValue sv = getRequest().getRequestParameters().getParameterValue(PARAM_OBJ);
				JSONObject obj = sv.isEmpty() ? new JSONObject() : new JSONObject(sv.toString());
				if (Action.createObj == a || Action.modifyObj == a) {
					if ("pointer".equals(obj.getJSONObject("obj").getString("type"))) {
						sendWbOthers(String.format("WbArea.%s(%s);", a.name(), obj.toString()));
						return;
					}
				}

				//wb-right
				if (rp.getClient().hasRight(Right.whiteBoard)) {
					switch (a) {
						case createWb:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).add(roomId, rp.getClient().getUser().getLanguageId());
							sendWbAll(getAddWbScript(wb.getId(), wb.getName()).toString());
						}
							break;
						case removeWb:
						{
							long _id = obj.optLong("id", -1);
							Long id = _id < 0 ? null : _id;
							getBean(WhiteboardCache.class).remove(roomId, id);
							sendWbAll(String.format("WbArea.remove(%s);", id));
						}
							break;
						case createObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONObject o = obj.getJSONObject("obj");
							wb.put(o.getString("uid"), o);
							sendWbOthers(String.format("WbArea.%s(%s);", a.name(), obj.toString()));
						}
							break;
						case modifyObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONObject o = obj.getJSONObject("obj");
							JSONArray arr = o.optJSONArray("objects");
							if (arr == null) {
								wb.put(o.getString("uid"), o);
							} else {
								for (int i = 0; i < arr.length(); ++i) {
									JSONObject _o = arr.getJSONObject(i);
									wb.put(_o.getString("uid"), _o);
								}
							}
							sendWbOthers(String.format("WbArea.%s(%s);", a.name(), obj.toString()));
						}
						case deleteObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONArray arr = obj.getJSONArray("obj");
							for (int i = 0; i < arr.length(); ++i) {
								wb.remove(arr.getString(i));
							}
							sendWbAll(String.format("WbArea.removeObj(%s);", obj.toString()));
						}
							break;
					}
				}
			} catch (Exception e) {
				// no-op
			}
		}
	};

	public WbPanel(String id, RoomPanel rp) {
		super(id);
		this.rp = rp;
		this.roomId = rp.getRoom().getId();
		setOutputMarkupId(true);

		getBean(WhiteboardCache.class).get(roomId).getWhiteboards();//TODO
		add(new ListView<String>("clipart", Arrays.asList(OmFileHelper.getPublicClipartsDir().list())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				String cls = String.format("clipart-%s", item.getIndex());
				item.add(append("class", cls), append("data-mode", cls)
						, new AttributeAppender("data-image", item.getModelObject()).setSeparator(""));
			}
		});
		add(wbAction);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(FABRIC_JS_REFERENCE));
		response.render(JavaScriptHeaderItem.forReference(WB_JS_REFERENCE));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_ACTION, wbAction, explicit(PARAM_ACTION), explicit(PARAM_OBJ))));
		StringBuilder sb = new StringBuilder("WbArea.init();");
		for (Entry<Long, Whiteboard> entry : getBean(WhiteboardCache.class).list(roomId, rp.getClient().getUser().getLanguageId())) {
			sb.append(getAddWbScript(entry.getKey(), entry.getValue().getName()));
			JSONArray arr = new JSONArray();
			for (Entry<String, JSONObject> wbEntry : entry.getValue().getRoomItems().entrySet()) {
				arr.put(wbEntry.getValue());
			}
			sb.append("WbArea.load(").append(new JSONObject().put("wbId", entry.getKey()).put("obj", arr).toString()).append(");");
		}
		response.render(OnDomReadyHeaderItem.forScript(sb));
	}

	private void sendWbAll(CharSequence func) {
		sendWb(func, null);
	}

	private void sendWbOthers(CharSequence func) {
		sendWb(func, c -> !rp.getClient().getUid().equals(c.getUid()));
	}

	private void sendWb(CharSequence func, Predicate<Client> check) {
		WebSocketHelper.sendRoom(
				roomId
				, new JSONObject()
						.put("type", "wb")
						.put("func", func)
						.toString()
				, check
			);
	}

	private static CharSequence getAddWbScript(Long id, String name) {
		return new StringBuilder("WbArea.add(")
				.append(new JSONObject()
						.put("id", id)
						.put("name", name)
						.toString()
						)
				.append(");");
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public WbPanel setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public WbPanel update(IPartialPageRequestHandler handler) {
		readOnly = !rp.getClient().hasRight(Right.whiteBoard);
		if (handler != null) {
			handler.appendJavaScript("setRoomSizes();");
		}
		return this;
	}
}
