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

import static org.apache.openmeetings.db.dto.room.Whiteboard.ITEMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.PARAM_OBJ;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.getObjWbJson;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.getWbJson;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.wicket.AttributeModifier.append;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.openmeetings.core.data.whiteboard.WhiteboardCache;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboard.ZoomMode;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
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
	private static final int UNDO_SIZE = 20;
	public static final String FUNC_ACTION = "wbAction";
	public static final String PARAM_ACTION = "action";
	public final static ResourceReference WB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "wb.js");
	private final static ResourceReference FABRIC_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "fabric.js");
	private final Long roomId;
	private final RoomPanel rp;
	private long wb2save = -1;
	private boolean inited = false;
	private final Map<Long, Deque<UndoObject>> undoList = new HashMap<>();
	private final AbstractDefaultAjaxBehavior wbAction = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			attributes.setMethod(Method.POST);
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
				if (WbAction.createObj == a || WbAction.modifyObj == a) {
					JSONObject o = obj.optJSONObject("obj");
					if (o != null && "pointer".equals(o.getString("type"))) {
						sendWbOthers(a, obj);
						return;
					}
				}

				Client c = rp.getClient();
				if (WbAction.downloadPdf == a) {
					boolean moder = c.hasRight(Room.Right.moderator);
					Room r = rp.getRoom();
					if ((moder && !r.isHidden(RoomElement.ActionMenu)) || (!moder && r.isAllowUserQuestions())) {
						PDDocument doc = new PDDocument();
						JSONArray arr = obj.getJSONArray("slides");
						for (int i = 0; i < arr.length(); ++i) {
							String base64Image = arr.getString(i).split(",")[1];
							byte[] bb = Base64.decodeBase64(base64Image);
							BufferedImage img = ImageIO.read(new ByteArrayInputStream(bb));
							float width = img.getWidth();
							float height = img.getHeight();
							PDPage page = new PDPage(new PDRectangle(width, height));
							PDImageXObject pdImageXObject = LosslessFactory.createFromImage(doc, img);
							try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, false)) {
								contentStream.drawImage(pdImageXObject, 0, 0, width, height);
							}
							doc.addPage(page);
						}
						//TODO check object
					}
				}
				//presenter-right
				if (c.hasRight(Right.presenter)) {
					switch (a) {
						case createWb:
						{
							Whiteboard wb = WhiteboardCache.add(roomId, c.getUser().getLanguageId());
							sendWbAll(WbAction.createWb, getAddWbJson(wb));
						}
							break;
						case removeWb:
						{
							long _id = obj.optLong("wbId", -1);
							Long id = _id < 0 ? null : _id;
							WhiteboardCache.remove(roomId, id);
							sendWbAll(WbAction.removeWb, obj);
						}
							break;
						case activateWb:
						{
							long _id = obj.optLong("wbId", -1);
							if (_id > -1) {
								WhiteboardCache.activate(roomId, _id);
								sendWbAll(WbAction.activateWb, obj);
							}
						}
							break;
						case setSlide:
						{
							Whiteboard wb = WhiteboardCache.get(roomId).get(obj.getLong("wbId"));
							wb.setSlide(obj.optInt("slide", 0));
							WhiteboardCache.update(roomId, wb);
							sendWbOthers(WbAction.setSlide, obj);
						}
							break;
						case clearAll:
						{
							clearAll(roomId, obj.getLong("wbId"));
						}
							break;
						case setSize:
						{
							Whiteboard wb = WhiteboardCache.get(roomId).get(obj.getLong("wbId"));
							wb.setZoom(obj.getDouble("zoom"));
							wb.setZoomMode(ZoomMode.valueOf(obj.getString("zoomMode")));
							WhiteboardCache.update(roomId, wb);
							sendWbOthers(WbAction.setSize, getAddWbJson(wb));
							//TODO scroll????
						}
							break;
						default:
							break;
					}
				}
				//wb-right
				if (c.hasRight(Right.presenter) || c.hasRight(Right.whiteBoard)) {
					switch (a) {
						case createObj:
						{
							Whiteboard wb = WhiteboardCache.get(roomId).get(obj.getLong("wbId"));
							JSONObject o = obj.getJSONObject("obj");
							wb.put(o.getString("uid"), o);
							WhiteboardCache.update(roomId, wb);
							addUndo(wb.getId(), new UndoObject(UndoObject.Type.add, o));
							sendWbOthers(WbAction.createObj, obj);
						}
							break;
						case modifyObj:
						{
							Whiteboard wb = WhiteboardCache.get(roomId).get(obj.getLong("wbId"));
							JSONArray arr = obj.getJSONArray("obj");
							JSONArray undo = new JSONArray();
							for (int i = 0; i < arr.length(); ++i) {
								JSONObject _o = arr.getJSONObject(i);
								String uid = _o.getString("uid");
								undo.put(wb.get(uid));
								wb.put(uid, _o);
							}
							if (arr.length() != 0) {
								WhiteboardCache.update(roomId, wb);
								addUndo(wb.getId(), new UndoObject(UndoObject.Type.modify, undo));
							}
							sendWbOthers(WbAction.modifyObj, obj);
						}
							break;
						case deleteObj:
						{
							Whiteboard wb = WhiteboardCache.get(roomId).get(obj.getLong("wbId"));
							JSONArray arr = obj.getJSONArray("obj");
							JSONArray undo = new JSONArray();
							for (int i = 0; i < arr.length(); ++i) {
								JSONObject _o = arr.getJSONObject(i);
								JSONObject u = wb.remove(_o.getString("uid"));
								if (u != null) {
									undo.put(u);
								}
							}
							if (undo.length() != 0) {
								WhiteboardCache.update(roomId, wb);
								addUndo(wb.getId(), new UndoObject(UndoObject.Type.remove, undo));
							}
							sendWbAll(WbAction.deleteObj, obj);
						}
							break;
						case clearSlide:
						{
							Whiteboard wb = WhiteboardCache.get(roomId).get(obj.getLong("wbId"));
							JSONArray arr = wb.clearSlide(obj.getInt("slide"));
							if (arr.length() != 0) {
								WhiteboardCache.update(roomId, wb);
								addUndo(wb.getId(), new UndoObject(UndoObject.Type.remove, arr));
							}
							sendWbAll(WbAction.clearSlide, obj);
						}
							break;
						case save:
							wb2save = obj.getLong("wbId");
							fileName.open(target);
							break;
						case undo:
						{
							Long wbId = obj.getLong("wbId");
							UndoObject uo = getUndo(wbId);
							if (uo != null) {
								Whiteboard wb = WhiteboardCache.get(roomId).get(wbId);
								switch (uo.getType()) {
									case add:
									{
										JSONObject o = new JSONObject(uo.getObject());
										wb.remove(o.getString("uid"));
										WhiteboardCache.update(roomId, wb);
										sendWbAll(WbAction.deleteObj, obj.put("obj", new JSONArray().put(o)));
									}
										break;
									case remove:
									{
										JSONArray arr = new JSONArray(uo.getObject());
										for (int i  = 0; i < arr.length(); ++i) {
											JSONObject o = arr.getJSONObject(i);
											wb.put(o.getString("uid"), o);
										}
										WhiteboardCache.update(roomId, wb);
										sendWbAll(WbAction.createObj, obj.put("obj", new JSONArray(uo.getObject())));
									}
										break;
									case modify:
									{
										JSONArray arr = new JSONArray(uo.getObject());
										for (int i  = 0; i < arr.length(); ++i) {
											JSONObject o = arr.getJSONObject(i);
											wb.put(o.getString("uid"), o);
										}
										WhiteboardCache.update(roomId, wb);
										sendWbAll(WbAction.modifyObj, obj.put("obj", arr));
									}
										break;
								}
							}
						}
							break;
						default:
							break;
					}
				}
			} catch (Exception e) {
				log.error("Unexpected error while processing wbAction", e);
			}
		}
	};
	private final NameDialog fileName = new NameDialog("filename") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			Whiteboard wb = WhiteboardCache.get(roomId).get(wb2save);
			FileExplorerItem f = new FileExplorerItem();
			f.setType(Type.WmlFile);
			f.setRoomId(roomId);
			f.setHash(UUID.randomUUID().toString());
			f.setName(getModelObject());
			f = getBean(FileExplorerItemDao.class).update(f);
			try (BufferedWriter writer = Files.newBufferedWriter(f.getFile().toPath())) {
				writer.write(wb.toJson().toString(new NullStringer(2)));
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
	private final AbstractDefaultAjaxBehavior wbLoad = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			StringBuilder sb = new StringBuilder("WbArea.init();");
			Whiteboards wbs = WhiteboardCache.get(roomId);
			for (Entry<Long, Whiteboard> entry : WhiteboardCache.list(roomId, rp.getClient().getUser().getLanguageId())) {
				Whiteboard wb = entry.getValue();
				sb.append(new StringBuilder("WbArea.create(").append(getAddWbJson(wb)).append(");"));
				JSONArray arr = new JSONArray();
				for (JSONObject o : wb.list()) {
					arr.put(addFileUrl(wbs.getUid(), o));
				}
				sb.append("WbArea.load(").append(getObjWbJson(entry.getKey(), arr).toString(new NullStringer())).append(");");
			}
			JSONObject wbj = getWbJson(wbs.getActiveWb());
			sb.append("WbArea.activateWb(").append(wbj).append(");");
			Whiteboard wb = wbs.get(wbs.getActiveWb());
			if (wb != null) {
				sb.append("WbArea.setSlide(").append(wbj.put("slide", wb.getSlide())).append(");");
			}
			target.appendJavaScript(sb);
			inited = true;
		}
	};

	public WbPanel(String id, RoomPanel rp) {
		super(id);
		this.rp = rp;
		this.roomId = rp.getRoom().getId();
		setOutputMarkupId(true);
		add(wbLoad);
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
		response.render(new PriorityHeaderItem(getNamedFunction(FUNC_ACTION, wbAction, explicit(PARAM_ACTION), explicit(PARAM_OBJ))));
		response.render(OnDomReadyHeaderItem.forScript(wbLoad.getCallbackScript()));
	}

	private static JSONObject getAddWbJson(Whiteboard wb) {
		return new JSONObject().put("wbId", wb.getId())
				.put("name", wb.getName())
				.put("width", wb.getWidth())
				.put("height", wb.getHeight())
				.put("zoom", wb.getZoom())
				.put("zoomMode", wb.getZoomMode());
	}

	public WbPanel update(IPartialPageRequestHandler handler) {
		String role = "none";
		if (rp.getClient().hasRight(Right.presenter)) {
			role = "presenter";
		} else if (rp.getClient().hasRight(Right.whiteBoard)) {
			role = "whiteBoard";
		}
		if (handler != null) {
			handler.appendJavaScript(String.format("setRoomSizes();WbArea.setRole('%s');", role));
		}
		return this;
	}

	private JSONObject addFileUrl(String ruid, JSONObject _file) {
		return addFileUrl(ruid, _file, null);
	}

	private JSONObject addFileUrl(String ruid, JSONObject _file, Consumer<FileItem> consumer) {
		try {
			final long fid = _file.optLong("fileId", -1);
			if (fid > 0) {
				FileItem fi = FileItem.Type.Recording.name().equals(_file.optString("fileType"))
						? getBean(RecordingDao.class).get(fid)
						: getBean(FileExplorerItemDao.class).get(fid);
				if (fi != null) {
					if (consumer != null) {
						consumer.accept(fi);
					}
					return WbWebSocketHelper.addFileUrl(ruid, _file, fi, rp.getClient());
				}
			}
		} catch (Exception e) {
			//no-op, non-file object
		}
		return _file;
	}

	private static JSONArray getArray(JSONObject wb, Function<JSONObject, JSONObject> postprocess) {
		JSONObject items = wb.getJSONObject(ITEMS_KEY);
		JSONArray arr = new JSONArray();
		for (String uid : items.keySet()) {
			JSONObject o = items.getJSONObject(uid);
			if (postprocess != null) {
				o = postprocess.apply(o);
			}
			arr.put(o);
		}
		return arr;
	}

	private void clearAll(Long roomId, long wbId) {
		Whiteboard wb = WhiteboardCache.get(roomId).get(wbId);

		JSONArray arr = getArray(wb.toJson(), null);
		if (arr.length() != 0) {
			addUndo(wb.getId(), new UndoObject(UndoObject.Type.remove, arr));
		}
		wb = WhiteboardCache.clear(roomId, wbId);
		sendWbAll(WbAction.clearAll, new JSONObject().put("wbId", wbId));
		sendWbAll(WbAction.setSize, getAddWbJson(wb));
	}

	private static void updateWbSize(Whiteboard wb, final FileItem fi) {
		int w = fi.getWidth() == null ? DEFAULT_WIDTH : fi.getWidth();
		int h = fi.getHeight() == null ? DEFAULT_HEIGHT : fi.getHeight();
		wb.setWidth(Math.max(wb.getWidth(), w));
		wb.setHeight(Math.max(wb.getHeight(), h));
	}

	public void sendFileToWb(final FileItem fi, boolean clean) {
		if (isVisible() && fi.getId() != null) {
			Whiteboards wbs = WhiteboardCache.get(roomId);
			String wuid = UUID.randomUUID().toString();
			Whiteboard wb = wbs.get(wbs.getActiveWb());
			final boolean[] updated = {false};
			switch (fi.getType()) {
				case Folder:
					//do nothing
					break;
				case WmlFile:
				{
					File f = fi.getFile();
					if (f.exists() && f.isFile()) {
						try (BufferedReader br = Files.newBufferedReader(f.toPath())) {
							JSONArray arr = getArray(new JSONObject(new JSONTokener(br)), (o) -> {
									wb.put(o.getString("uid"), o);
									updated[0] = true;
									return addFileUrl(wbs.getUid(), o, _f -> {
										updateWbSize(wb, _f);
									});
								});
							sendWbAll(WbAction.setSize, getAddWbJson(wb));
							sendWbAll(WbAction.load, getObjWbJson(wb.getId(), arr));
						} catch (Exception e) {
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
					final String ruid = wbs.getUid();
					if (clean) {
						clearAll(roomId, wb.getId());
					}
					wb.put(wuid, file);
					updateWbSize(wb, fi);
					updated[0] = true;
					sendWbAll(WbAction.setSize, getAddWbJson(wb));
					WbWebSocketHelper.sendWbFile(roomId, wb.getId(), ruid, file, fi);
				}
					break;
			}
			if (updated[0]) {
				WhiteboardCache.update(roomId, wb);
			}
		}
	}

	private void sendWbOthers(WbAction a, JSONObject obj) {
		WbWebSocketHelper.sendWbOthers(roomId, a, obj, rp.getClient().getUid());
	}

	private void sendWbAll(WbAction a, JSONObject obj) {
		WbWebSocketHelper.sendWbAll(roomId, a, obj);
	}

	private void addUndo(Long wbId, UndoObject u) {
		if (wbId == null) {
			return;
		}
		if (!undoList.containsKey(wbId)) {
			undoList.put(wbId, new LimitedLinkedList<>());
		}
		undoList.get(wbId).push(u);
	}

	private UndoObject getUndo(Long wbId) {
		if (wbId == null || !undoList.containsKey(wbId)) {
			return null;
		}
		Deque<UndoObject> deq = undoList.get(wbId);
		return deq.isEmpty() ? null : deq.pop();
	}

	private static class LimitedLinkedList<T> extends LinkedList<T> {
		private static final long serialVersionUID = 1L;

		@Override
		public void push(T e) {
			super.push(e);
			while (size() > UNDO_SIZE) {
				removeLast();
			}
		}
	}
}
