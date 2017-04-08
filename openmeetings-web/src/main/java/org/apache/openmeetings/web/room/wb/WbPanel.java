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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.AttributeModifier.append;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.RoomResourceReference;
import org.apache.openmeetings.web.user.record.JpgRecordingResourceReference;
import org.apache.openmeetings.web.user.record.Mp4RecordingResourceReference;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
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
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.github.openjson.JSONTokener;

public class WbPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(WbPanel.class, webAppRootKey);
	private static final int UPLOAD_WB_LEFT = 0;
	private static final int UPLOAD_WB_TOP = 0;
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	public static final String FUNC_ACTION = "wbAction";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_OBJ = "obj";
	private final static ResourceReference WB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "wb.js");
	private final static ResourceReference FABRIC_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "fabric.js");
	private boolean readOnly = true;
	private final Long roomId;
	private final RoomPanel rp;
	private long wb2save = -1;
	private enum Action {
		createWb
		, removeWb
		, activateWb
		, setSlide
		, createObj
		, modifyObj
		, deleteObj
		, clearAll
		, clearSlide
		, save
	}
	private final AbstractDefaultAjaxBehavior wbAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			attributes.setMethod(Method.POST);
		}

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				Action a = Action.valueOf(getRequest().getRequestParameters().getParameterValue(PARAM_ACTION).toString());
				StringValue sv = getRequest().getRequestParameters().getParameterValue(PARAM_OBJ);
				JSONObject obj = sv.isEmpty() ? new JSONObject() : new JSONObject(sv.toString());
				if (Action.createObj == a || Action.modifyObj == a) {
					if ("pointer".equals(obj.getJSONObject("obj").getString("type"))) {
						sendWbOthers(a, obj);
						return;
					}
				}

				//wb-right
				if (rp.getClient().hasRight(Right.whiteBoard)) {
					switch (a) {
						case createWb:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).add(roomId, rp.getClient().getUser().getLanguageId());
							sendWbAll(Action.createWb, getAddWbJson(wb.getId(), wb.getName()));
						}
							break;
						case removeWb:
						{
							long _id = obj.optLong("wbId", -1);
							Long id = _id < 0 ? null : _id;
							getBean(WhiteboardCache.class).remove(roomId, id);
							sendWbAll(Action.removeWb, obj);
						}
							break;
						case activateWb:
						{
							long _id = obj.optLong("wbId", -1);
							if (_id > -1) {
								Whiteboards wbs = getBean(WhiteboardCache.class).get(roomId);
								wbs.setActiveWb(_id);
								sendWbAll(Action.activateWb, obj);
							}
						}
							break;
						case setSlide:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							wb.setSlide(obj.optInt("slide", 0));
							sendWbOthers(Action.setSlide, obj);
						}
							break;
						case createObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONObject o = obj.getJSONObject("obj");
							wb.put(o.getString("uid"), o);
							sendWbOthers(Action.createObj, obj);
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
							sendWbOthers(Action.modifyObj, obj);
						}
						case deleteObj:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							JSONArray arr = obj.getJSONArray("obj");
							for (int i = 0; i < arr.length(); ++i) {
								wb.remove(arr.getString(i));
							}
							sendWbAll(Action.deleteObj, obj);
						}
							break;
						case clearAll:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							clearAll(wb);
						}
							break;
						case clearSlide:
						{
							Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(obj.getLong("wbId"));
							wb.entrySet().removeIf(e -> e.getValue().optInt("slide", -1) == obj.getInt("slide"));
							sendWbAll(Action.clearSlide, obj);
						}
							break;
						case save:
							wb2save = obj.getLong("wbId");
							fileName.open(target);
							break;
					}
				}
			} catch (Exception e) {
				// no-op
			}
		}
	};
	private final NameDialog fileName = new NameDialog("filename") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			Whiteboard wb = getBean(WhiteboardCache.class).get(roomId).get(wb2save);
			FileExplorerItem f = new FileExplorerItem();
			f.setType(Type.WmlFile);
			f.setRoomId(roomId);
			f.setHash(UUID.randomUUID().toString());
			f.setName(getModelObject());
			f = getBean(FileExplorerItemDao.class).update(f);
			try (BufferedWriter writer = Files.newBufferedWriter(f.getFile().toPath())) {
				writer.write(wb.toJson().toString(2));
			} catch (IOException e) {
				error("Unexpected error while saving WB: " + e.getMessage());
				target.add(feedback);
				log.error("Unexpected error while saving WB", e);
			}
		}

		@Override
		protected String getTitleStr() {
			return getString("199");
		}

		@Override
		protected String getLabelStr() {
			return getString("200");
		}

		@Override
		protected String getAddStr() {
			return Application.getString("203");
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
			}, fileName);
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
			sb.append(new StringBuilder("WbArea.create(")
					.append(getAddWbJson(entry.getKey(), entry.getValue().getName()).toString())
					.append(");"));
			JSONArray arr = new JSONArray();
			for (Entry<String, JSONObject> wbEntry : entry.getValue().getRoomItems().entrySet()) {
				JSONObject o = wbEntry.getValue();
				arr.put(addFileUrl(wbs.getUid(), o));
			}
			sb.append("WbArea.load(").append(getObjWbJson(entry.getKey(), arr).toString()).append(");");
		}
		sb.append("WbArea.activateWb({wbId: ").append(wbs.getActiveWb()).append("});");
		response.render(OnDomReadyHeaderItem.forScript(sb));
	}

	private void sendWbAll(Action meth, JSONObject obj) {
		sendWb(meth, obj, null);
	}

	private void sendWbOthers(Action meth, JSONObject obj) {
		sendWb(meth, obj, c -> !rp.getClient().getUid().equals(c.getUid()));
	}

	private void sendWb(Action meth, JSONObject obj, Predicate<Client> check) {
		WebSocketHelper.sendRoom(
				roomId
				, new JSONObject().put("type", "wb")
				, check
				, (o, c) -> o.put("func", String.format("WbArea.%s(%s);", meth.name(), obj.toString())).toString()
			);
	}

	private static JSONObject getObjWbJson(Long wbId, Object o) {
		return new JSONObject().put("wbId", wbId).put(PARAM_OBJ, o);
	}

	private static JSONObject getAddWbJson(Long id, String name) {
		return new JSONObject().put("wbId", id).put("name", name);
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
			handler.appendJavaScript(String.format("setRoomSizes();WbArea.setReadOnly(%s);", readOnly));
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
		JSONObject file = new JSONObject(_file, JSONObject.getNames(_file)); //FIXME TODO openjson 1.0.2
		final FileSystemResourceReference ref;
		final PageParameters pp = new PageParameters()
				.add("id", fi.getId()).add("uid", c.getUid())
				.add("ruid", ruid).add("wuid", _file.optString("uid"));
		switch (fi.getType()) {
			case Video:
				ref = new RoomResourceReference();
				file.put("_src", urlFor(ref, pp));
				file.put("_poster", urlFor(ref, new PageParameters(pp).add("preview", true)));
				break;
			case Recording:
				ref = new Mp4RecordingResourceReference();
				file.put("_src", urlFor(ref, pp));
				file.put("_poster", urlFor(new JpgRecordingResourceReference(), pp));
				break;
			case Presentation:
				ref = new RoomResourceReference();
				file.put("_src", urlFor(ref, pp));
				file.put("deleted", !fi.exists());
				break;
			default:
				ref = new RoomResourceReference();
				file.put("src", urlFor(ref, pp));
				break;
		}
		return file;
	}

	private void clearAll(Whiteboard wb) {
		wb.clear();
		sendWbAll(Action.clearAll, new JSONObject().put("wbId", wb.getId()));
	}

	public void sendFileToWb(FileItem fi, boolean clean) {
		if (isVisible() && fi.getId() != null) {
			Whiteboards wbs = getBean(WhiteboardCache.class).get(roomId);
			String wuid = UUID.randomUUID().toString();
			Whiteboard wb = wbs.get(wbs.getActiveWb());
			switch (fi.getType()) {
				case Folder:
					//do nothing
					break;
				case WmlFile:
				{
					File f = fi.getFile();
					if (f.exists() && f.isFile()) {
						try (BufferedReader br = Files.newBufferedReader(f.toPath())) {
							JSONObject wbo = new JSONObject(new JSONTokener(br));
							/*WebSocketHelper.sendRoom(
									roomId
									, new JSONObject().put("type", "wb")
									, null
									, (o, c) -> {
											return o.put("func", String.format("WbArea.%s(%s);"
													, Action.createObj.name()
													, getObjWbJson(wb.getId(), addFileUrl(ruid, file, fi, c)).toString())
												).toString();
										}
									);*/
						} catch (IOException e) {
							log.error("Unexpected error while loading WB", e);
						}
					}
				}
					break;
				case PollChart:
					break;
				default:
				{
					JSONObject file = new JSONObject()
							.put("fileId", fi.getId())
							.put("fileType", fi.getType().name())
							.put("count", fi.getCount())
							.put("type", "image")
							.put("left", UPLOAD_WB_LEFT)
							.put("top", UPLOAD_WB_TOP)
							.put("width", fi.getWidth() == null ? DEFAULT_WIDTH : fi.getWidth())
							.put("height", fi.getHeight() == null ? DEFAULT_HEIGHT : fi.getHeight())
							.put("uid", wuid)
							.put("slide", wb.getSlide())
							;
					wb.put(wuid, file);
					final String ruid = wbs.getUid();
					if (clean) {
						clearAll(wb);
					}
					WebSocketHelper.sendRoom(
							roomId
							, new JSONObject().put("type", "wb")
							, null
							, (o, c) -> {
									return o.put("func", String.format("WbArea.%s(%s);"
											, Action.createObj.name()
											, getObjWbJson(wb.getId(), addFileUrl(ruid, file, fi, c)).toString())
										).toString();
								}
							);
				}
					break;
			}
		}
	}

	//FIXME TODO openjson 1.0.2
	//private static class ObjectStringer
}
