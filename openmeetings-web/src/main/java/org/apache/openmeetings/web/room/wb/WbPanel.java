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
import java.util.UUID;
import java.util.function.Predicate;

import org.apache.openmeetings.core.data.whiteboard.WhiteboardCache;
import org.apache.openmeetings.core.util.WebSocketHelper;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.RoomResourceReference;
import org.apache.openmeetings.web.user.record.JpgRecordingResourceReference;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.FileSystemResourceReference;
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
						sendWbOthers(String.format("WbArea.%s", a.name()), obj);
						return;
					}
				}

				//wb-right
				if (rp.getClient().hasRight(Right.whiteBoard)) {
					switch (a) {
						case createWb:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).add(roomId, rp.getClient().getUser().getLanguageId());
							sendWbAll("WbArea.add", getAddWbJson(wb.getId(), wb.getName()));
						}
							break;
						case removeWb:
						{
							long _id = obj.optLong("id", -1);
							Long id = _id < 0 ? null : _id;
							getBean(WhiteboardCache.class).remove(roomId, id);
							sendWbAll("WbArea.remove", new JSONObject().put("id", id));
						}
							break;
						case createObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONObject o = obj.getJSONObject("obj");
							wb.put(o.getString("uid"), o);
							sendWbOthers(String.format("WbArea.%s", a.name()), obj);
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
							sendWbOthers(String.format("WbArea.%s", a.name()), obj);
						}
						case deleteObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONArray arr = obj.getJSONArray("obj");
							for (int i = 0; i < arr.length(); ++i) {
								wb.remove(arr.getString(i));
							}
							sendWbAll("WbArea.removeObj", obj);
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
		if (rp.getRoom().isHidden(RoomElement.Whiteboard)) {
			setVisible(false);
		} else {
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
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(FABRIC_JS_REFERENCE));
		response.render(JavaScriptHeaderItem.forReference(WB_JS_REFERENCE));
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_ACTION, wbAction, explicit(PARAM_ACTION), explicit(PARAM_OBJ))));
		StringBuilder sb = new StringBuilder("WbArea.init();");
		WhiteboardCache cache = getBean(WhiteboardCache.class);
		Whiteboards wbs = cache.get(roomId);
		for (Entry<Long, Whiteboard> entry : cache.list(roomId, rp.getClient().getUser().getLanguageId())) {
			sb.append(getAddWbScript(entry.getKey(), entry.getValue().getName()));
			JSONArray arr = new JSONArray();
			for (Entry<String, JSONObject> wbEntry : entry.getValue().getRoomItems().entrySet()) {
				JSONObject o = wbEntry.getValue();
				arr.put(addFileUrl(wbs.getUid(), o));
			}
			sb.append("WbArea.load(").append(getObjWbJson(entry.getKey(), arr).toString()).append(");");
		}
		response.render(OnDomReadyHeaderItem.forScript(sb));
	}

	private void sendWbAll(CharSequence meth, JSONObject obj) {
		sendWb(meth, obj, null);
	}

	private void sendWbOthers(CharSequence meth, JSONObject obj) {
		sendWb(meth, obj, c -> !rp.getClient().getUid().equals(c.getUid()));
	}

	private void sendWb(CharSequence meth, JSONObject obj, Predicate<Client> check) {
		WebSocketHelper.sendRoom(
				roomId
				, new JSONObject()
						.put("type", "wb")
				, check
				, (o, c) -> o.put("func", String.format("%s(%s);", meth, obj.toString())).toString()
			);
	}

	private static JSONObject getObjWbJson(Long wbId, Object o) {
		return new JSONObject()
				.put("wbId", wbId)
				.put(PARAM_OBJ, o);
	}

	private static JSONObject getAddWbJson(Long id, String name) {
		return new JSONObject()
				.put("id", id)
				.put("name", name);
	}

	private static CharSequence getAddWbScript(Long id, String name) {
		return new StringBuilder("WbArea.add(")
				.append(getAddWbJson(id, name).toString())
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

	private JSONObject addFileUrl(String ruid, JSONObject _file) {
		try {
		final long fid = _file.optLong("fileId", -1);
			if (fid > 0) {
				FileItem fi = FileItem.Type.Recording.name().equals(_file.optString("fileType"))
						? getBean(RecordingDao.class).get(fid)
						: getBean(FileExplorerItemDao.class).get(fid);
				if (fi != null) {
					return addFileUrl(ruid, _file, fi, rp.getClient());
				}
			}
		} catch (Exception e) {
			//no-op, non-file object
		}
		return _file;
	}
	private JSONObject addFileUrl(String ruid, JSONObject _file, FileItem fi, Client c) {
		final PageParameters pp = new PageParameters();
		final FileSystemResourceReference ref;
		pp.add("id", fi.getId()).add("ruid", ruid).add("wuid", _file.optString("uid"));
		switch (fi.getType()) {
			case Video:
				pp.add("preview", true);
				ref = new RoomResourceReference();
				break;
			case Recording:
				ref = new JpgRecordingResourceReference();
				break;
			default:
				ref = new RoomResourceReference();
				break;
		}
		return new JSONObject(_file, JSONObject.getNames(_file)).put("src", urlFor(ref, pp.add("uid", c.getUid())));  //FIXME TODO openjson 1.0.2
	}

	/*
	 * OLD VERSION
	 *
	public void sendFileToWb(FileItem fi, boolean clean) {
		if (wb.isVisible() && fi.getId() != null && FileItem.Type.Folder != fi.getType()) {
			long activeWbId = -1;
			if (FileItem.Type.WmlFile == fi.getType()) {
				getBean(ConferenceLibrary.class).sendToWhiteboard(getClient().getUid(), activeWbId, fi);
			} else {
				String url = null;
				PageParameters pp = new PageParameters();
				pp.add("id", fi.getId())
					.add("ruid", getBean(WhiteboardCache.class).get(r.getId()).getUid());
				switch (fi.getType()) {
					case Video:
						pp.add("preview", true);
						url = urlFor(new RoomResourceReference(), pp).toString();
						break;
					case Recording:
						url = urlFor(new JpgRecordingResourceReference(), pp).toString();
						break;
					default:
						url = urlFor(new RoomResourceReference(), pp).toString();
						break;
				}
				getBean(ScopeApplicationAdapter.class).sendToWhiteboard(getClient().getUid(), activeWbId, fi, url, clean);
			}
		}
	}
	 */

	public void sendFileToWb(FileItem fi, boolean clean) {
		if (isVisible() && fi.getId() != null && FileItem.Type.Folder != fi.getType()) {
			//FIXME TODO WmlFile special handling
			Whiteboards wbs = getBean(WhiteboardCache.class).get(roomId);
			String wuid = UUID.randomUUID().toString();
			Whiteboard wb = wbs.getWhiteboards().values().iterator().next(); //TODO active
			//FIXME TODO various types
			JSONObject file = new JSONObject()
					.put("fileId", fi.getId())
					.put("fileType", fi.getType().name())
					.put("type", "image")
					.put("left", 0) //FIXME TODO constant
					.put("top", 0) //FIXME TODO constant
					.put("width", 800/*fi.getWidth()*/) //FIXME TODO check null
					.put("height", 600/*fi.getHeight()*/) //FIXME TODO constant
					//,"angle":32.86
					//,"crossOrigin":""
					.put("uid", wuid)
					//,"filters":[]
					//,"resizeFilters":[]
					;
			wb.put(wuid, file);
			final String ruid = wbs.getUid();
			WebSocketHelper.sendRoom(
					roomId
					, new JSONObject().put("type", "wb")
					, null
					, (o, c) -> {
							return o.put("func", String.format("WbArea.%s(%s);"
									, Action.createObj.name()
									, getObjWbJson(wb.getId(), addFileUrl(ruid, file, fi, c)).toString()) //FIXME TODO openjson 1.0.2
								).toString();
						}
					);
		}
	}

	//FIXME TODO openjson 1.0.2
	//private static class ObjectStringer
}
