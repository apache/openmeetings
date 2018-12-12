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

import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_ID;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_TYPE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_SLIDE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_TYPE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ITEMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_STATUS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.getObjWbJson;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.getWbJson;
import static org.apache.wicket.AttributeModifier.append;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboard.ZoomMode;
import org.apache.openmeetings.db.dto.room.Whiteboards;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.WhiteboardManager;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.Method;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.github.openjson.JSONTokener;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class WbPanel extends AbstractWbPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(WbPanel.class, getWebAppRootKey());
	private static final int UPLOAD_WB_LEFT = 0;
	private static final int UPLOAD_WB_TOP = 0;
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	private static final int UNDO_SIZE = 20;
	public static final ResourceReference WB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "wb.js");
	private final Long roomId;
	private long wb2save = -1;
	private final Map<Long, Deque<UndoObject>> undoList = new HashMap<>();
	private final NameDialog fileName = new NameDialog("filename") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
			String res = saveWb(roomId, wb2save, getModelObject());
			if (!Strings.isEmpty(res)) {
				error("Unexpected error while saving WB: " + res);
				target.add(feedback);
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
			return getString("144");
		}
	};

	public WbPanel(String id, RoomPanel rp) {
		super(id, rp);
		this.roomId = rp.getRoom().getId();
		if (rp.getRoom().isHidden(RoomElement.Whiteboard)) {
			setVisible(false);
		} else {
			add(new ListView<String>("clipart"
					, Arrays.asList(OmFileHelper.getPublicClipartsDir().list())
						.stream()
						.sorted()
						.collect(Collectors.toList()))
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<String> item) {
					String cls = String.format("clipart-%s", item.getIndex());
					item.add(append(ATTR_CLASS, cls), append("data-mode", cls)
							, append("data-image", item.getModelObject()).setSeparator(""));
				}
			}, fileName);
		}
	}

	@Override
	void internalWbLoad(StringBuilder sb) {
		Long langId = rp.getClient().getUser().getLanguageId();
		WhiteboardManager wbm = getBean(WhiteboardManager.class);
		if (!wbm.contains(roomId) && rp.getRoom().getFiles() != null && !rp.getRoom().getFiles().isEmpty()) {
			if (wbm.tryLock(roomId)) {
				try {
					TreeMap<Long, List<BaseFileItem>> files = new TreeMap<>();
					for (RoomFile rf : rp.getRoom().getFiles()) {
						List<BaseFileItem> bfl = files.get(rf.getWbIdx());
						if (bfl == null) {
							files.put(rf.getWbIdx(), new ArrayList<>());
							bfl = files.get(rf.getWbIdx());
						}
						bfl.add(rf.getFile());
					}
					Whiteboards _wbs = wbm.get(roomId, langId);
					for (Map.Entry<Long, List<BaseFileItem>> e : files.entrySet()) {
						Whiteboard wb = wbm.add(roomId, langId);
						_wbs.setActiveWb(wb.getId());
						for (BaseFileItem fi : e.getValue()) {
							sendFileToWb(fi, false);
						}
					}
				} finally {
					wbm.unlock(roomId);
				}
			}
		}
		Whiteboards wbs = wbm.get(roomId, langId);
		loadWhiteboards(sb, rp.getClient(), wbs, wbm.list(roomId));
		JSONObject wbj = getWbJson(wbs.getActiveWb());
		sb.append("WbArea.activateWb(").append(wbj).append(");");
		Whiteboard wb = wbs.get(wbs.getActiveWb());
		if (wb != null) {
			sb.append("WbArea.setSlide(").append(wbj.put(ATTR_SLIDE, wb.getSlide())).append(");");
		}
		sb.append("WbArea.loadVideos();");
	}

	@Override
	protected void updateWbActionAttributes(AjaxRequestAttributes attributes) {
		attributes.setMethod(Method.POST);
	}

	@Override
	protected void processWbAction(WbAction a, JSONObject obj, AjaxRequestTarget target) throws IOException {
		Client c = rp.getClient();
		if (c == null) {
			return;
		}
		WhiteboardManager wbm = getBean(WhiteboardManager.class);
		switch (a) {
			case createObj:
			case modifyObj:
			{
				JSONObject o = obj.optJSONObject("obj");
				if (o != null && "pointer".equals(o.getString(ATTR_TYPE))) {
					sendWbOthers(a, obj);
					return;
				}
			}
				break;
			case downloadPdf:
			{
				boolean moder = c.hasRight(Room.Right.moderator);
				Room r = rp.getRoom();
				if ((moder && !r.isHidden(RoomElement.ActionMenu)) || (!moder && r.isAllowUserQuestions())) {
					try (PDDocument doc = new PDDocument()) {
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
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						doc.save(baos);
						rp.startDownload(target, baos.toByteArray());
					}
				}
				return;
			}
			case loadVideos:
			{
				StringBuilder sb = new StringBuilder("WbArea.initVideos(");
				JSONArray arr = new JSONArray();
				for (Entry<Long, Whiteboard> entry : wbm.list(roomId)) {
					Whiteboard wb = entry.getValue();
					for (JSONObject o : wb.list()) {
						String ft = o.optString(ATTR_FILE_TYPE);
						if (BaseFileItem.Type.Recording.name().equals(ft) || BaseFileItem.Type.Video.name().equals(ft)) {
							JSONObject _sts = o.optJSONObject(PARAM_STATUS);
							if (_sts == null) {
								continue;
							}
							JSONObject sts = new JSONObject(_sts.toString()); //copy
							sts.put("pos", sts.getDouble("pos") + (System.currentTimeMillis() - sts.getLong("updated")) * 1. / 1000);
							arr.put(new JSONObject()
									.put("wbId", wb.getId())
									.put("uid", o.getString("uid"))
									.put(ATTR_SLIDE, o.getString(ATTR_SLIDE))
									.put(PARAM_STATUS, sts));
						}
					}
				}
				sb.append(arr.toString()).append(");");
				target.appendJavaScript(sb);
				return;
			}
			default:
				break;
		}

		//presenter-right
		if (c.hasRight(Right.presenter)) {
			switch (a) {
				case createWb:
				{
					Whiteboard wb = wbm.add(roomId, c.getUser().getLanguageId());
					sendWbAll(WbAction.createWb, getAddWbJson(wb));
				}
					break;
				case removeWb:
				{
					long id = obj.optLong("wbId", -1);
					if (id > -1) {
						wbm.remove(roomId, id);
						sendWbAll(WbAction.removeWb, obj);
					}
				}
					break;
				case activateWb:
				{
					long _id = obj.optLong("wbId", -1);
					if (_id > -1) {
						wbm.activate(roomId, _id);
						sendWbAll(WbAction.activateWb, obj);
					}
				}
					break;
				case renameWb:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.optLong("wbId", -1));
					if (wb != null) {
						wbm.update(roomId, wb.setName(obj.getString("name")));
						sendWbAll(WbAction.renameWb, obj);
					}
				}
					break;
				case setSlide:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.optLong("wbId", -1));
					if (wb != null) {
						wb.setSlide(obj.optInt(ATTR_SLIDE, 0));
						wbm.update(roomId, wb);
						sendWbOthers(WbAction.setSlide, obj);
					}
				}
					break;
				case clearAll:
				{
					clearAll(roomId, obj.getLong("wbId"));
				}
					break;
				case setSize:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
					wb.setZoom(obj.getDouble("zoom"));
					wb.setZoomMode(ZoomMode.valueOf(obj.getString("zoomMode")));
					wbm.update(roomId, wb);
					sendWbOthers(WbAction.setSize, getAddWbJson(wb));
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
					Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
					JSONObject o = obj.getJSONObject("obj");
					wb.put(o.getString("uid"), o);
					wbm.update(roomId, wb);
					addUndo(wb.getId(), new UndoObject(UndoObject.Type.add, o));
					sendWbOthers(WbAction.createObj, obj);
				}
					break;
				case modifyObj:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
					JSONArray arr = obj.getJSONArray("obj");
					JSONArray undo = new JSONArray();
					for (int i = 0; i < arr.length(); ++i) {
						JSONObject _o = arr.getJSONObject(i);
						String uid = _o.getString("uid");
						JSONObject po = wb.get(uid);
						if (po != null) {
							undo.put(po);
							wb.put(uid, _o);
						}
					}
					if (arr.length() != 0) {
						wbm.update(roomId, wb);
						addUndo(wb.getId(), new UndoObject(UndoObject.Type.modify, undo));
					}
					sendWbOthers(WbAction.modifyObj, obj);
				}
					break;
				case deleteObj:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
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
						wbm.update(roomId, wb);
						addUndo(wb.getId(), new UndoObject(UndoObject.Type.remove, undo));
					}
					sendWbAll(WbAction.deleteObj, obj);
				}
					break;
				case clearSlide:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
					JSONArray arr = wb.clearSlide(obj.getInt(ATTR_SLIDE));
					if (arr.length() != 0) {
						wbm.update(roomId, wb);
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
						Whiteboard wb = wbm.get(roomId).get(wbId);
						switch (uo.getType()) {
							case add:
							{
								JSONObject o = new JSONObject(uo.getObject());
								wb.remove(o.getString("uid"));
								wbm.update(roomId, wb);
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
								wbm.update(roomId, wb);
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
								wbm.update(roomId, wb);
								sendWbAll(WbAction.modifyObj, obj.put("obj", arr));
							}
								break;
						}
					}
				}
					break;
				case videoStatus:
				{
					Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
					String uid = obj.getString("uid");
					JSONObject po = wb.get(uid);
					if (po != null && "video".equals(po.getString(ATTR_TYPE))) {
						JSONObject ns = obj.getJSONObject(PARAM_STATUS);
						po.put(PARAM_STATUS, ns.put("updated", System.currentTimeMillis()));
						wbm.update(roomId, wb.put(uid, po));
						obj.put(ATTR_SLIDE, po.getInt(ATTR_SLIDE));
						sendWbAll(WbAction.videoStatus, obj);
					}
				}
					break;
				default:
					break;
			}
		}
	}

	private static JSONObject getAddWbJson(final Whiteboard wb) {
		return new JSONObject().put("wbId", wb.getId())
				.put("name", wb.getName())
				.put("width", wb.getWidth())
				.put("height", wb.getHeight())
				.put("zoom", wb.getZoom())
				.put("zoomMode", wb.getZoomMode().name());
	}

	@Override
	protected String getRole() {
		String role = ROLE_NONE;
		if (rp.getClient().hasRight(Right.presenter)) {
			role = Right.presenter.name();
		} else if (rp.getClient().hasRight(Right.whiteBoard)) {
			role = Right.whiteBoard.name();
		}
		return role;
	}

	private static JSONObject addFileUrl(Client cl, String ruid, JSONObject _file) {
		return addFileUrl(cl, ruid, _file, null);
	}

	private static JSONObject addFileUrl(Client cl, String ruid, JSONObject _file, Consumer<BaseFileItem> consumer) {
		try {
			final long fid = _file.optLong(ATTR_FILE_ID, -1);
			if (fid > 0) {
				BaseFileItem fi = getBean(FileItemDao.class).getAny(fid);
				if (fi != null) {
					if (consumer != null) {
						consumer.accept(fi);
					}
					return WbWebSocketHelper.addFileUrl(ruid, _file, fi, cl);
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
		WhiteboardManager wbm = getBean(WhiteboardManager.class);
		Whiteboard wb = wbm.get(roomId).get(wbId);
		if (wb == null) {
			return;
		}
		JSONArray arr = getArray(wb.toJson(), null);
		if (arr.length() != 0) {
			addUndo(wb.getId(), new UndoObject(UndoObject.Type.remove, arr));
		}
		wb = wbm.clear(roomId, wbId);
		sendWbAll(WbAction.clearAll, new JSONObject().put("wbId", wbId));
		sendWbAll(WbAction.setSize, getAddWbJson(wb));
	}

	private static void updateWbSize(Whiteboard wb, final BaseFileItem fi) {
		int w = fi.getWidth() == null ? DEFAULT_WIDTH : fi.getWidth();
		int h = fi.getHeight() == null ? DEFAULT_HEIGHT : fi.getHeight();
		double scale = 1. * wb.getWidth() / w;
		scale = scale < 1 ? 1 : scale;
		wb.setWidth(Math.max(wb.getWidth(), (int)(w * scale)));
		wb.setHeight(Math.max(wb.getHeight(), (int)(h * scale)));
	}

	@Override
	public void sendFileToWb(final BaseFileItem fi, boolean clean) {
		if (isVisible() && fi.getId() != null) {
			WhiteboardManager wbm = getBean(WhiteboardManager.class);
			Whiteboards wbs = wbm.get(roomId);
			String wuid = UUID.randomUUID().toString();
			Whiteboard wb = wbs.get(wbs.getActiveWb());
			if (wb == null) {
				return;
			}
			switch (fi.getType()) {
				case Folder:
					//do nothing
					break;
				case WmlFile:
				{
					File f = fi.getFile();
					if (f.exists() && f.isFile()) {
						try (BufferedReader br = Files.newBufferedReader(f.toPath())) {
							final boolean[] updated = {false};
							JSONArray arr = getArray(new JSONObject(new JSONTokener(br)), o -> {
									wb.put(o.getString("uid"), o);
									updated[0] = true;
									return addFileUrl(rp.getClient(), wbs.getUid(), o, _f -> updateWbSize(wb, _f));
								});
							if (updated[0]) {
								wbm.update(roomId, wb);
							}
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
							.put(ATTR_FILE_ID, fi.getId())
							.put(ATTR_FILE_TYPE, fi.getType().name())
							.put("count", fi.getCount())
							.put(ATTR_TYPE, "image")
							.put("left", UPLOAD_WB_LEFT)
							.put("top", UPLOAD_WB_TOP)
							.put("width", fi.getWidth() == null ? DEFAULT_WIDTH : fi.getWidth())
							.put("height", fi.getHeight() == null ? DEFAULT_HEIGHT : fi.getHeight())
							.put("uid", wuid)
							.put(ATTR_SLIDE, wb.getSlide())
							;
					if (FileItem.Type.Video == fi.getType() || FileItem.Type.Recording == fi.getType()) {
						file.put(ATTR_TYPE, "video");
						file.put(PARAM_STATUS, new JSONObject()
								.put("paused", true)
								.put("pos", 0.0)
								.put("updated", System.currentTimeMillis()));
					}
					final String ruid = wbs.getUid();
					if (clean) {
						clearAll(roomId, wb.getId());
					}
					wb.put(wuid, file);
					updateWbSize(wb, fi);
					wbm.update(roomId, wb);
					sendWbAll(WbAction.setSize, getAddWbJson(wb));
					WbWebSocketHelper.sendWbFile(roomId, wb.getId(), ruid, file, fi);
				}
					break;
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

	public static String saveWb(Long roomId, Long wbId, String name) {
		Whiteboard wb = getBean(WhiteboardManager.class).get(roomId).get(wbId);
		FileItem f = new FileItem();
		f.setType(BaseFileItem.Type.WmlFile);
		f.setRoomId(roomId);
		f.setHash(UUID.randomUUID().toString());
		f.setName(name);
		f = getBean(FileItemDao.class).update(f);
		return wb.save(f.getFile().toPath());
	}

	private static StringBuilder loadWhiteboards(StringBuilder sb, Client cl, Whiteboards wbs, Set<Entry<Long, Whiteboard>> boardSet) {
		for (Entry<Long, Whiteboard> entry : boardSet) {
			Whiteboard wb = entry.getValue();
			sb.append(new StringBuilder("WbArea.create(").append(getAddWbJson(wb)).append(");"));
			JSONArray arr = new JSONArray();
			for (JSONObject o : wb.list()) {
				arr.put(addFileUrl(cl, wbs.getUid(), o));
			}
			sb.append("WbArea.load(").append(getObjWbJson(entry.getKey(), arr).toString(new NullStringer())).append(");");
		}
		return sb;
	}
}
