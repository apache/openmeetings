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

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_ID;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_TYPE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_HEIGHT;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_OMTYPE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_SLIDE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_TYPE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_WIDTH;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_ZOOM;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ITEMS_KEY;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.getObjWbJson;
import static org.apache.openmeetings.web.room.wb.WbWebSocketHelper.getWbJson;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_STATUS;
import static org.apache.wicket.AttributeModifier.append;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

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
import org.apache.openmeetings.web.app.WhiteboardManager;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.util.NullStringer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.github.openjson.JSONTokener;

import jakarta.inject.Inject;

public class WbPanel extends AbstractWbPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(WbPanel.class);
	private static final String PARAM_UPDATED = "updated";
	private static final int UPLOAD_WB_LEFT = 0;
	private static final int UPLOAD_WB_TOP = 0;
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	private static final int UNDO_SIZE = 20;
	private final Long roomId;
	private long wb2save = -1;
	private final Map<Long, Deque<UndoObject>> undoList = new HashMap<>();
	private final Map<Long, Deque<UndoObject>> redoList = new HashMap<>();
	private final NameDialog fileName = new NameDialog("filename") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			String res = saveWb(roomId, wb2save, getModelObject());
			if (!Strings.isEmpty(res)) {
				error("Unexpected error while saving WB: " + res);
				target.add(feedback);
			} else {
				super.onSubmit(target);
			}
		}

		@Override
		protected IModel<String> getTitle() {
			return new ResourceModel("199");
		}

		@Override
		protected IModel<String> getLabel() {
			return new ResourceModel("200");
		}

		@Override
		protected IModel<String> getAddBtnLabel() {
			return new ResourceModel("144");
		}
	};
	private final SerializableBiConsumer<Whiteboard, Boolean> addUndo = (wb, redo) -> {
		JSONArray arr = getArray(wb.toJson(), null);
		if (arr.length() != 0) {
			if (Boolean.FALSE.equals(redo)) {
				cleanRedo(wb.getId());
			}
			addUndo(wb.getId(), new UndoObject(WbAction.CLEAR_ALL
					, new JSONObject().put("wbId", wb.getId())
					, UndoObject.Type.REMOVE, arr));
		}
	};

	@Inject
	private WhiteboardManager wbm;
	@Inject
	private FileItemDao fileDao;

	public WbPanel(String id, RoomPanel rp) {
		super(id, rp);
		this.roomId = rp.getRoom().getId();
		if (rp.getRoom().isHidden(RoomElement.WHITEBOARD)) {
			setVisible(false);
		} else {
			add(new ListView<>("clipart"
					, List.of(OmFileHelper.getPublicClipartsDir().list())
						.stream()
						.sorted()
						.toList())
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem<String> item) {
					String cls = "clipart-" + item.getIndex();
					item.add(append(ATTR_CLASS, cls), append("data-mode", cls)
							, append("data-image", item.getModelObject()).setSeparator(""));
				}
			}, fileName);
		}
	}

	@Override
	void internalWbLoad(StringBuilder sb) {
		Long langId = rp.getClient().getUser().getLanguageId();
		wbm.initFiles(rp.getRoom(), langId, (wbs, wbIdx, roomFiles)
				-> roomFiles.forEach(rf -> addFileToWb(wbs, wbIdx, rf.getFile(), false, false))
		);
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
	public void reloadWb(IPartialPageRequestHandler handler) {
		StringBuilder sb = new StringBuilder("WbArea.doCleanAll();");
		internalWbLoad(sb);
		handler.appendJavaScript(sb);
	}

	@Override
	public void processWbAction(WbAction a, JSONObject obj, IPartialPageRequestHandler handler) throws IOException {
		Client c = rp.getClient();
		if (c == null) {
			return;
		}
		doAction(c, a, obj, false, handler);
	}

	private void doAction(Client c, WbAction a, JSONObject obj, boolean redo, IPartialPageRequestHandler handler) throws IOException {
		if (doActionGeneral(c, a, obj, handler)) {
			return;
		}
		doActionInternal(c, a, obj, redo, handler);
	}

	private void doActionInternal(Client c, WbAction a, JSONObject obj, boolean redo, IPartialPageRequestHandler handler) throws IOException {
		//presenter-right
		if (c.hasRight(Right.PRESENTER)) {
			doActionPresenter(c, a, obj, redo);
		}
		//wb-right
		if (c.hasRight(Right.PRESENTER) || c.hasRight(Right.WHITEBOARD)) {
			doActionWhiteboard(c, a, obj, false, handler);
		}
	}

	private boolean doActionGeneral(Client c, WbAction a, JSONObject obj, IPartialPageRequestHandler handler) {
		switch (a) {
			case CREATE_OBJ, MODIFY_OBJ:
			{
				JSONObject o = obj.optJSONObject("obj");
				if (o != null && "pointer".equals(o.getString(ATTR_OMTYPE))) {
					sendWbOthers(a, obj);
					return true;
				}
			}
				break;
			case DOWNLOAD:
			{
				boolean moder = c.hasRight(Room.Right.MODERATOR);
				Room r = rp.getRoom();
				if (moder && !r.isHidden(RoomElement.ACTION_MENU)) {
					rp.startDownload(handler, obj.getString("type"), obj.getString("fuid"));
				}
				return true;
			}
			case LOAD_VIDEOS:
				loadVideos(handler);
				return true;
			default:
				break;
		}
		return false;
	}

	private void doActionPresenter(Client c, WbAction a, JSONObject obj, boolean redo) {
		switch (a) {
			case CREATE_WB:
			{
				Whiteboard wb = wbm.add(roomId, c.getUser().getLanguageId());
				sendWbAll(WbAction.CREATE_WB, wb.getAddJson());
			}
				break;
			case REMOVE_WB:
			{
				long id = obj.optLong("wbId", -1);
				if (id > -1) {
					long prevId = obj.optLong("prevWbId", -1);
					wbm.remove(roomId, id, prevId);
					sendWbAll(WbAction.REMOVE_WB, obj);
				}
			}
				break;
			case ACTIVATE_WB:
			{
				long wbId = obj.optLong("wbId", -1);
				if (wbId > -1) {
					wbm.activate(roomId, wbId);
					sendWbAll(WbAction.ACTIVATE_WB, obj);
				}
			}
				break;
			case RENAME_WB:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.optLong("wbId", -1));
				if (wb != null) {
					wbm.update(roomId, wb.setName(obj.getString("name")));
					sendWbAll(WbAction.RENAME_WB, obj);
				}
			}
				break;
			case SET_SLIDE:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.optLong("wbId", -1));
				if (wb != null) {
					wb.setSlide(obj.optInt(ATTR_SLIDE, 0));
					wbm.update(roomId, wb);
					sendWbOthers(WbAction.SET_SLIDE, obj);
				}
			}
				break;
			case CLEAR_ALL:
			{
				wbm.clearAll(roomId, obj.getLong("wbId"), redo, addUndo);
			}
				break;
			case SET_SIZE:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
				wb.setWidth(obj.getInt(ATTR_WIDTH));
				wb.setHeight(obj.getInt(ATTR_HEIGHT));
				wb.setZoom(obj.getDouble(ATTR_ZOOM));
				wb.setZoomMode(ZoomMode.valueOf(obj.getString("zoomMode")));
				wbm.update(roomId, wb);
				sendWbOthers(WbAction.SET_SIZE, wb.getAddJson());
			}
				break;
			default:
				break;
		}
	}

	private void doActionWhiteboard(Client c, WbAction a, JSONObject obj, boolean redo, IPartialPageRequestHandler handler) throws IOException {
		switch (a) {
			case CREATE_OBJ:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
				JSONObject o = obj.getJSONObject("obj");
				wb.put(o.getString("uid"), o);
				wbm.update(roomId, wb);
				addUndo(wb.getId(), new UndoObject(a, obj, UndoObject.Type.ADD, o));
				if (redo) {
					sendWbAll(WbAction.CREATE_OBJ, obj);
				} else {
					cleanRedo(wb.getId());
					sendWbOthers(WbAction.CREATE_OBJ, obj);
				}
			}
				break;
			case MODIFY_OBJ:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
				JSONArray arr = obj.getJSONArray("obj");
				JSONArray undo = new JSONArray();
				for (int i = 0; i < arr.length(); ++i) {
					JSONObject oi = arr.getJSONObject(i);
					String uid = oi.getString("uid");
					JSONObject po = wb.get(uid);
					if (po != null) {
						undo.put(po);
						wb.put(uid, oi);
					}
				}
				if (arr.length() != 0) {
					wbm.update(roomId, wb);
					addUndo(wb.getId(), new UndoObject(a, obj, UndoObject.Type.MODIFY, undo));
					if (redo) {
						sendWbAll(WbAction.MODIFY_OBJ, obj);
					} else {
						cleanRedo(wb.getId());
						sendWbOthers(WbAction.MODIFY_OBJ, obj);
					}
				}
			}
				break;
			case DELETE_OBJ:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
				JSONArray arr = obj.getJSONArray("obj");
				JSONArray undo = new JSONArray();
				for (int i = 0; i < arr.length(); ++i) {
					JSONObject oi = arr.getJSONObject(i);
					JSONObject u = wb.remove(oi.getString("uid"));
					if (u != null) {
						undo.put(u);
					}
				}
				if (undo.length() != 0) {
					wbm.update(roomId, wb);
					addUndo(wb.getId(), new UndoObject(a, obj, UndoObject.Type.REMOVE, undo));
				}
				if (!redo) {
					cleanRedo(wb.getId());
				}
				sendWbAll(WbAction.DELETE_OBJ, obj);
			}
				break;
			case CLEAR_SLIDE:
			{
				wbm.cleanSlide(roomId, obj.getLong("wbId"), obj.getInt(ATTR_SLIDE)
						, (wb, arr) -> {
							if (!redo) {
								cleanRedo(wb.getId());
							}
							addUndo(wb.getId(), new UndoObject(a, obj, UndoObject.Type.REMOVE, arr));
						});
			}
				break;
			case SAVE:
				wb2save = obj.getLong("wbId");
				fileName.show(handler);
				break;
			case UNDO:
			{
				Long wbId = obj.getLong("wbId");
				UndoObject uo = getUndo(wbId);
				if (uo != null) {
					Whiteboard wb = wbm.get(roomId).get(wbId);
					switch (uo.getType()) {
						case ADD:
						{
							JSONObject o = new JSONObject(uo.getObject());
							wb.remove(o.getString("uid"));
							wbm.update(roomId, wb);
							sendWbAll(WbAction.DELETE_OBJ, obj.put("obj", new JSONArray().put(o)));
						}
							break;
						case REMOVE:
						{
							JSONArray arr = new JSONArray(uo.getObject());
							for (int i  = 0; i < arr.length(); ++i) {
								JSONObject o = arr.getJSONObject(i);
								wb.put(o.getString("uid"), o);
							}
							wbm.update(roomId, wb);
							sendWbAll(WbAction.CREATE_OBJ, obj.put("obj", new JSONArray(uo.getObject())));
						}
							break;
						case MODIFY:
						{
							JSONArray arr = new JSONArray(uo.getObject());
							for (int i  = 0; i < arr.length(); ++i) {
								JSONObject o = arr.getJSONObject(i);
								wb.put(o.getString("uid"), o);
							}
							wbm.update(roomId, wb);
							sendWbAll(WbAction.MODIFY_OBJ, obj.put("obj", arr));
						}
							break;
					}
				}
			}
				break;
			case REDO:
			{
				Long wbId = obj.getLong("wbId");
				UndoObject uo = getRedo(wbId);
				if (uo != null) {
					doActionInternal(c, uo.getAction(), uo.getOrigObject(), true, handler);
				}
			}
				break;
			case VIDEO_STATUS:
			{
				Whiteboard wb = wbm.get(roomId).get(obj.getLong("wbId"));
				String uid = obj.getString("uid");
				JSONObject po = wb.get(uid);
				if (po != null && "Video".equals(po.getString(ATTR_OMTYPE))) {
					JSONObject ns = obj.getJSONObject(PARAM_STATUS);
					po.put(PARAM_STATUS, ns.put(PARAM_UPDATED, System.currentTimeMillis()));
					wbm.update(roomId, wb.put(uid, po));
					obj.put(ATTR_SLIDE, po.getInt(ATTR_SLIDE));
					sendWbAll(WbAction.VIDEO_STATUS, obj);
				}
			}
				break;
			default:
				break;
		}
	}

	private void loadVideos(IPartialPageRequestHandler handler) {
		StringBuilder sb = new StringBuilder("WbArea.initVideos(");
		JSONArray arr = new JSONArray();
		for (Entry<Long, Whiteboard> entry : wbm.list(roomId)) {
			Whiteboard wb = entry.getValue();
			for (JSONObject o : wb.list()) {
				String ft = o.optString(ATTR_FILE_TYPE);
				if (BaseFileItem.Type.RECORDING.name().equals(ft) || BaseFileItem.Type.VIDEO.name().equals(ft)) {
					JSONObject status = o.optJSONObject(PARAM_STATUS);
					if (status == null) {
						continue;
					}
					JSONObject sts = new JSONObject(status.toString()); //copy
					sts.put("pos", sts.getDouble("pos") + (System.currentTimeMillis() - sts.getLong(PARAM_UPDATED)) * 1. / 1000);
					arr.put(new JSONObject()
							.put("wbId", wb.getId())
							.put("uid", o.getString("uid"))
							.put(ATTR_SLIDE, o.getString(ATTR_SLIDE))
							.put(PARAM_STATUS, sts));
				}
			}
		}
		sb.append(arr.toString()).append(");");
		handler.appendJavaScript(sb);
	}

	@Override
	protected String getRole() {
		String role = ROLE_NONE;
		if (rp.getClient().hasRight(Right.PRESENTER)) {
			role = Right.PRESENTER.name();
		} else if (rp.getClient().hasRight(Right.WHITEBOARD)) {
			role = Right.WHITEBOARD.name();
		}
		return role;
	}

	private JSONObject addFileUrl(Client cl, String ruid, JSONObject file) {
		return addFileUrl(cl, ruid, file, null);
	}

	private JSONObject addFileUrl(Client cl, String ruid, JSONObject file, Consumer<BaseFileItem> consumer) {
		try {
			final long fid = file.optLong(ATTR_FILE_ID, -1);
			if (fid > 0) {
				BaseFileItem fi = fileDao.getAny(fid);
				if (fi != null) {
					if (consumer != null) {
						consumer.accept(fi);
					}
					return WbWebSocketHelper.addFileUrl(ruid, file, fi, cl);
				}
			}
		} catch (Exception e) {
			//no-op, non-file object
		}
		return file;
	}

	private static JSONArray getArray(JSONObject wb, UnaryOperator<JSONObject> postprocess) {
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

	private static void updateWbSize(Whiteboard wb, final BaseFileItem fi) {
		int w = fi.getWidth() == null ? DEFAULT_WIDTH : fi.getWidth();
		int h = fi.getHeight() == null ? DEFAULT_HEIGHT : fi.getHeight();
		double scale = 1. * wb.getWidth() / w;
		scale = scale < 1 ? 1 : scale;
		wb.setWidth(Math.max(wb.getWidth(), (int)(w * scale)));
		wb.setHeight(Math.max(wb.getHeight(), (int)(h * scale)));
	}

	private void addFileToWb(Whiteboards wbs, Whiteboard wb, final BaseFileItem fi, boolean clean, boolean sendAndUpdate) {
		if (fi.getId() == null || wb == null) {
			return;
		}
		switch (fi.getType()) {
			case FOLDER:
				//do nothing
				break;
			case WML_FILE:
				addWmlFileToWb(wbs, wb, fi, sendAndUpdate);
				break;
			case POLL_CHART:
				break;
			default:
			{
				String wuid = randomUUID().toString();
				JSONObject file = new JSONObject()
						.put(ATTR_FILE_ID, fi.getId())
						.put(ATTR_FILE_TYPE, fi.getType().name())
						.put("count", fi.getCount())
						.put(ATTR_TYPE, "image")
						.put("left", UPLOAD_WB_LEFT)
						.put("top", UPLOAD_WB_TOP)
						.put(ATTR_WIDTH, fi.getWidth() == null ? DEFAULT_WIDTH : fi.getWidth())
						.put(ATTR_HEIGHT, fi.getHeight() == null ? DEFAULT_HEIGHT : fi.getHeight())
						.put("uid", wuid)
						.put(ATTR_SLIDE, wb.getSlide())
						;
				if (BaseFileItem.Type.VIDEO == fi.getType() || BaseFileItem.Type.RECORDING == fi.getType()) {
					file.put(ATTR_OMTYPE, "Video");
					file.put(PARAM_STATUS, new JSONObject()
							.put("paused", true)
							.put("pos", 0.0)
							.put(PARAM_UPDATED, System.currentTimeMillis()));
				}
				final String ruid = wbs.getUid();
				if (clean) {
					wbm.clearAll(roomId, wb.getId(), false, addUndo);
				}
				wb.put(wuid, file);
				updateWbSize(wb, fi);
				if (sendAndUpdate) {
					wbm.update(roomId, wb);
					sendWbAll(WbAction.SET_SIZE, wb.getAddJson());
					WbWebSocketHelper.sendWbFile(roomId, wb.getId(), ruid, file, fi);
				}
			}
				break;
		}
	}

	private void addWmlFileToWb(Whiteboards wbs, Whiteboard wb, final BaseFileItem fi, boolean sendAndUpdate) {
		File f = fi.getFile();
		if (f.exists() && f.isFile()) {
			try (BufferedReader br = Files.newBufferedReader(f.toPath())) {
				final boolean[] updated = {false};
				JSONArray arr = getArray(new JSONObject(new JSONTokener(br)), o -> {
						wb.put(o.getString("uid"), o);
						updated[0] = true;
						return addFileUrl(rp.getClient(), wbs.getUid(), o, bf -> updateWbSize(wb, bf));
					});
				if (sendAndUpdate) {
					if (updated[0]) {
						wbm.update(roomId, wb);
					}
					sendWbAll(WbAction.SET_SIZE, wb.getAddJson());
					sendWbAll(WbAction.LOAD, getObjWbJson(wb.getId(), arr));
				}
			} catch (Exception e) {
				log.error("Unexpected error while loading WB", e);
			}
		}
	}

	private void sendFileToWb(Long wbId, final BaseFileItem fi, boolean clean) {
		if (isVisible()) {
			Whiteboards wbs = wbm.get(roomId);
			Whiteboard wb = wbs.get(wbId == null ? wbs.getActiveWb() : wbId);
			addFileToWb(wbs, wb, fi, clean, true);
		}
	}

	@Override
	public void sendFileToWb(final BaseFileItem fi, boolean clean) {
		sendFileToWb(null, fi, clean);
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
		undoList
			.computeIfAbsent(wbId, id -> new LimitedLinkedList<>())
			.push(u);
	}

	private UndoObject getUndo(Long wbId) {
		if (wbId == null) {
			return null;
		}
		Deque<UndoObject> deq = undoList.get(wbId);
		if (deq == null || deq.isEmpty()) {
			return null;
		}
		UndoObject undoObj = deq.pop();
		redoList
			.computeIfAbsent(wbId, id -> new LimitedLinkedList<>())
			.push(undoObj);
		return undoObj;
	}

	private void cleanRedo(Long wbId) {
		if (wbId == null) {
			return;
		}
		Deque<UndoObject> redoDeq = redoList.get(wbId);
		if (redoDeq != null) {
			redoDeq.clear();
		}
	}

	private UndoObject getRedo(Long wbId) {
		if (wbId == null) {
			return null;
		}
		Deque<UndoObject> deq = redoList.get(wbId);
		if (deq == null || deq.isEmpty()) {
			return null;
		}
		return deq.pop();
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

	public String saveWb(Long roomId, Long wbId, String name) {
		Whiteboard wb = wbm.get(roomId).get(wbId);
		FileItem f = new FileItem();
		f.setType(BaseFileItem.Type.WML_FILE);
		f.setRoomId(roomId);
		f.setHash(randomUUID().toString());
		f.setName(name);
		f = fileDao.update(f);
		return wb.save(f.getFile().toPath());
	}

	private StringBuilder loadWhiteboards(StringBuilder sb, Client cl, Whiteboards wbs, Set<Entry<Long, Whiteboard>> boardSet) {
		for (Entry<Long, Whiteboard> entry : boardSet) {
			Whiteboard wb = entry.getValue();
			sb.append(new StringBuilder("WbArea.create(").append(wb.getAddJson()).append(");"));
			JSONArray arr = new JSONArray();
			for (JSONObject o : wb.list()) {
				arr.put(addFileUrl(cl, wbs.getUid(), o));
			}
			sb.append("WbArea.load(").append(getObjWbJson(entry.getKey(), arr).toString(new NullStringer())).append(");");
		}
		return sb;
	}
}
