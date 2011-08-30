package org.openmeetings.app.conference.whiteboard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.remote.red5.WhiteBoardObjectListManager;
import org.openmeetings.app.remote.red5.WhiteBoardObjectListManagerById;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class WhiteboardManagement {

	private static final Logger log = Red5LoggerFactory.getLogger(
			WhiteboardManagement.class, ScopeApplicationAdapter.webAppRootKey);

	private WhiteboardManagement() {
	}

	private static WhiteboardManagement instance = null;

	public static synchronized WhiteboardManagement getInstance() {
		if (instance == null) {
			instance = new WhiteboardManagement();
		}
		return instance;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addWhiteBoardObject(Long room_id, Map whiteboardObj) {
		try {
			log.debug("addWhiteBoardObject: " + whiteboardObj);

			// log.debug("whiteboardObj 0: Event: "+whiteboardObj.get(0));
			// log.debug("whiteboardObj 1: Event: "+whiteboardObj.get(1));
			// log.debug("whiteboardObj 2: Event: "+whiteboardObj.get(2));
			// log.debug("whiteboardObj 3: Event: "+whiteboardObj.get(3));

			// log.debug("whiteboardObj NUMB3: Event: "+whiteboardObj.get(3).getClass().getName());

			// Date dateOfEvent = (Date) whiteboardObj.get(1);
			String action = whiteboardObj.get(2).toString();
			List actionObject = (List) whiteboardObj.get(3);

			log.debug("action: " + action);

			if (action.equals("draw") || action.equals("redo")) {
				HashMap<String, List> roomList = WhiteBoardObjectListManager
						.getInstance().getWhiteBoardObjectListByRoomId(room_id);

				// log.debug(actionObject);
				// log.debug(actionObject.size()-1);
				// log.debug(actionObject.get(actionObject.size()-1));
				String objectType = actionObject.get(0).toString();
				if (!objectType.equals("pointerWhiteBoard")) {
					String objectOID = actionObject
							.get(actionObject.size() - 1).toString();
					log.debug("objectOID: " + objectOID);
					roomList.put(objectOID, actionObject);
					WhiteBoardObjectListManager.getInstance()
							.setWhiteBoardObjectListRoomObj(room_id, roomList);
				}
			} else if (action.equals("clear")) {

				WhiteBoardObjectListManager.getInstance().setWhiteBoardObject(
						room_id, new WhiteboardObject());

			} else if (action.equals("delete") || action.equals("undo")) {
				HashMap<String, List> roomList = WhiteBoardObjectListManager
						.getInstance().getWhiteBoardObjectListByRoomId(room_id);
				String objectOID = actionObject.get(actionObject.size() - 1)
						.toString();
				String objectType = actionObject.get(0).toString();
				log.debug("removal of objectOID: " + objectOID);

				// "ellipse"
				// || this.baseactionobjectList[i][0] == "drawarrow"
				// || this.baseactionobjectList[i][0] == "line"
				// || this.baseactionobjectList[i][0] == "paint"
				// || this.baseactionobjectList[i][0] == "rectangle"
				// || this.baseactionobjectList[i][0] == "uline"
				// || this.baseactionobjectList[i][0] == "image"
				// || this.baseactionobjectList[i][0] == "letter"

				// Re-Index all items in its zIndex
				if (objectType.equals("ellipse")
						|| objectType.equals("drawarrow")
						|| objectType.equals("line")
						|| objectType.equals("paint")
						|| objectType.equals("rectangle")
						|| objectType.equals("uline")
						|| objectType.equals("image")
						|| objectType.equals("letter")
						|| objectType.equals("swf")) {

					Integer zIndex = Integer.valueOf(
							actionObject.get(actionObject.size() - 8)
									.toString()).intValue();

					log.debug("1|zIndex " + zIndex);
					log.debug("2|zIndex "
							+ actionObject.get(actionObject.size() - 8)
									.toString());
					log.debug("3|zIndex "
							+ actionObject.get(actionObject.size() - 8));

					int l = 0;
					for (Iterator debugIter = actionObject.iterator(); debugIter
							.hasNext();) {
						log.debug("4|zIndex " + l + " -- " + debugIter.next());
						l++;
					}

					for (Iterator<String> iter = roomList.keySet().iterator(); iter
							.hasNext();) {
						String whiteboardObjKey = iter.next();
						List actionObjectStored = roomList
								.get(whiteboardObjKey);

						Integer zIndexStored = Integer.valueOf(
								actionObjectStored.get(
										actionObjectStored.size() - 8)
										.toString()).intValue();

						log.debug("zIndexStored|zIndex " + zIndexStored + "|"
								+ zIndex);

						if (zIndexStored >= zIndex) {
							zIndexStored -= 1;
							log.debug("new-zIndex " + zIndexStored);
						}
						actionObjectStored.set(actionObjectStored.size() - 8,
								zIndexStored);

						roomList.put(whiteboardObjKey, actionObjectStored);
					}

				}

				roomList.remove(objectOID);
				WhiteBoardObjectListManager.getInstance()
						.setWhiteBoardObjectListRoomObj(room_id, roomList);
			} else if (action.equals("size") || action.equals("editProp")
					|| action.equals("editText") || action.equals("swf")) {
				HashMap<String, List> roomList = WhiteBoardObjectListManager
						.getInstance().getWhiteBoardObjectListByRoomId(room_id);
				String objectOID = actionObject.get(actionObject.size() - 1)
						.toString();
				// List roomItem = roomList.get(objectOID);
				List currentObject = roomList.get(objectOID);
				roomList.put(objectOID, actionObject);

				if (action.equals("swf")) {
					if (actionObject.get(0).equals("swf")) {

						if (actionObject.get(8) != currentObject.get(8)) {

							String baseObjectName = actionObject.get(
									actionObject.size() - 1).toString();
							Integer slidesNumber = Integer.valueOf(
									actionObject.get(8).toString()).intValue();

							log.debug("updateObjectsToSlideNumber :: "
									+ baseObjectName + "," + slidesNumber);

							for (Iterator<String> iter = roomList.keySet()
									.iterator(); iter.hasNext();) {
								String whiteboardObjKey = iter.next();
								List actionObjectStored = roomList
										.get(whiteboardObjKey);

								if (actionObjectStored.get(0).equals("ellipse")
										|| actionObjectStored.get(0).equals(
												"drawarrow")
										|| actionObjectStored.get(0).equals(
												"line")
										|| actionObjectStored.get(0).equals(
												"paint")
										|| actionObjectStored.get(0).equals(
												"rectangle")
										|| actionObjectStored.get(0).equals(
												"uline")
										|| actionObjectStored.get(0).equals(
												"image")
										|| actionObjectStored.get(0).equals(
												"letter")) {

									Map swfObj = (Map) actionObjectStored
											.get(actionObjectStored.size() - 7);
									log.debug("swfObj :1: " + swfObj);

									if (swfObj != null) {

										if (swfObj.get("name").equals(
												baseObjectName)) {

											if (Integer.valueOf(
													swfObj.get("slide")
															.toString())
													.intValue() == slidesNumber) {

												swfObj.put("isVisible", true);

												actionObjectStored.set(
														actionObjectStored
																.size() - 7,
														swfObj);

											} else {

												swfObj.put("isVisible", false);

												actionObjectStored.set(
														actionObjectStored
																.size() - 7,
														swfObj);

											}

										}

									}

									log.debug("swfObj :1: " + swfObj);

								}

							}

						}

					}
				}

				WhiteBoardObjectListManager.getInstance()
						.setWhiteBoardObjectListRoomObj(room_id, roomList);
			} else if (action.equals("clearSlide")) {

				HashMap<String, List> roomList = WhiteBoardObjectListManager
						.getInstance().getWhiteBoardObjectListByRoomId(room_id);

				for (String objectName : (List<String>) actionObject) {

					roomList.remove(objectName);

				}

				WhiteBoardObjectListManager.getInstance()
						.setWhiteBoardObjectListRoomObj(room_id, roomList);

			} else {
				log.warn("Unkown Type: " + action + " actionObject: "
						+ actionObject);
			}

		} catch (Exception err) {
			log.error("[addWhiteBoardObject]", err);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void updateWhiteboardObject(Long room_id, List actionObject) {
		try {

			WhiteboardObject whiteBoardObject = WhiteBoardObjectListManager
					.getInstance().getWhiteBoardObjectRoomId(room_id);

			whiteBoardObject.setFullFit((Boolean) actionObject.get(1));
			whiteBoardObject.setZoom((Integer) actionObject.get(2));

			WhiteBoardObjectListManager.getInstance().setWhiteBoardObject(
					room_id, whiteBoardObject);

		} catch (Exception err) {
			log.error("[updateWhiteboardObject]", err);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void updateWhiteboardObjectPos(Long room_id, List actionObject) {
		try {

			WhiteboardObject whiteBoardObject = WhiteBoardObjectListManager
					.getInstance().getWhiteBoardObjectRoomId(room_id);

			whiteBoardObject.setX((Integer) actionObject.get(1));
			whiteBoardObject.setY((Integer) actionObject.get(2));

			WhiteBoardObjectListManager.getInstance().setWhiteBoardObject(
					room_id, whiteBoardObject);

		} catch (Exception err) {
			log.error("[updateWhiteboardObjectPos]", err);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addWhiteBoardObjectById(Long room_id, Map whiteboardObj,
			Long whiteBoardId) {
		try {
			log.debug("addWhiteBoardObjectById: ", whiteboardObj);

			// log.debug("whiteboardObj 0: Event: "+whiteboardObj.get(0));
			// log.debug("whiteboardObj 1: Event: "+whiteboardObj.get(1));
			// log.debug("whiteboardObj 2: Event: "+whiteboardObj.get(2));
			// log.debug("whiteboardObj 3: Event: "+whiteboardObj.get(3));

			// log.debug("whiteboardObj NUMB3: Event: "+whiteboardObj.get(3).getClass().getName());

			// Date dateOfEvent = (Date) whiteboardObj.get(1);
			String action = whiteboardObj.get(2).toString();

			log.debug("action: " + action);
			log.debug("actionObject: " + whiteboardObj.get(3));

			List actionObject = (List) whiteboardObj.get(3);

			if (action.equals("moveMap")) {
				WhiteboardObject whiteboardObject = WhiteBoardObjectListManagerById
						.getInstance()
						.getWhiteBoardObjectListByRoomIdAndWhiteboard(room_id,
								whiteBoardId);

				whiteboardObject.setX(Integer.valueOf(
						actionObject.get(1).toString()).intValue());
				whiteboardObject.setY(Integer.valueOf(
						actionObject.get(2).toString()).intValue());

				WhiteBoardObjectListManagerById.getInstance()
						.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id,
								whiteboardObject, whiteBoardId);
			} else if (action.equals("draw") || action.equals("redo")) {
				WhiteboardObject whiteboardObject = WhiteBoardObjectListManagerById
						.getInstance()
						.getWhiteBoardObjectListByRoomIdAndWhiteboard(room_id,
								whiteBoardId);

				// log.debug(actionObject);
				// log.debug(actionObject.size()-1);
				// log.debug(actionObject.get(actionObject.size()-1));
				String objectType = actionObject.get(0).toString();
				if (!objectType.equals("pointerWhiteBoard")) {
					String objectOID = actionObject
							.get(actionObject.size() - 1).toString();
					log.debug("objectOID: " + objectOID);
					whiteboardObject.getRoomItems()
							.put(objectOID, actionObject);
					WhiteBoardObjectListManagerById.getInstance()
							.setWhiteBoardObjectListRoomObjAndWhiteboardId(
									room_id, whiteboardObject, whiteBoardId);
				}
			} else if (action.equals("clear")) {
				WhiteboardObject whiteboardObject = WhiteBoardObjectListManagerById
						.getInstance()
						.getWhiteBoardObjectListByRoomIdAndWhiteboard(room_id,
								whiteBoardId);
				whiteboardObject.setRoomItems(new HashMap<String, List>());
				WhiteBoardObjectListManagerById.getInstance()
						.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id,
								whiteboardObject, whiteBoardId);
			} else if (action.equals("delete") || action.equals("undo")) {
				WhiteboardObject whiteboardObject = WhiteBoardObjectListManagerById
						.getInstance()
						.getWhiteBoardObjectListByRoomIdAndWhiteboard(room_id,
								whiteBoardId);
				String objectOID = actionObject.get(actionObject.size() - 1)
						.toString();
				String objectType = actionObject.get(0).toString();
				log.debug("removal of objectOID: " + objectOID);

				log.debug("removal of objectOID: " + objectOID);

				// "ellipse"
				// || this.baseactionobjectList[i][0] == "drawarrow"
				// || this.baseactionobjectList[i][0] == "line"
				// || this.baseactionobjectList[i][0] == "paint"
				// || this.baseactionobjectList[i][0] == "rectangle"
				// || this.baseactionobjectList[i][0] == "uline"
				// || this.baseactionobjectList[i][0] == "image"
				// || this.baseactionobjectList[i][0] == "letter"

				// Re-Index all items in its zIndex
				if (objectType.equals("ellipse")
						|| objectType.equals("drawarrow")
						|| objectType.equals("line")
						|| objectType.equals("paint")
						|| objectType.equals("rectangle")
						|| objectType.equals("uline")
						|| objectType.equals("image")
						|| objectType.equals("letter")
						|| objectType.equals("clipart")
						|| objectType.equals("swf")
						|| objectType.equals("mindmapnode")
						|| objectType.equals("flv")) {

					Integer zIndex = Integer.valueOf(
							actionObject.get(actionObject.size() - 8)
									.toString()).intValue();

					log.debug("1|zIndex " + zIndex);
					log.debug("2|zIndex "
							+ actionObject.get(actionObject.size() - 8)
									.toString());
					log.debug("3|zIndex "
							+ actionObject.get(actionObject.size() - 8));

					int l = 0;
					for (Iterator debugIter = actionObject.iterator(); debugIter
							.hasNext();) {
						log.debug("4|zIndex " + l + " -- " + debugIter.next());
						l++;
					}

					for (Iterator<String> iter = whiteboardObject
							.getRoomItems().keySet().iterator(); iter.hasNext();) {
						String whiteboardObjKey = iter.next();
						List actionObjectStored = whiteboardObject
								.getRoomItems().get(whiteboardObjKey);

						Integer zIndexStored = Integer.valueOf(
								actionObjectStored.get(
										actionObjectStored.size() - 8)
										.toString()).intValue();

						log.debug("zIndexStored|zIndex " + zIndexStored + "|"
								+ zIndex);

						if (zIndexStored >= zIndex) {
							zIndexStored -= 1;
							log.debug("new-zIndex " + zIndexStored);
						}
						actionObjectStored.set(actionObjectStored.size() - 8,
								zIndexStored);

						whiteboardObject.getRoomItems().put(whiteboardObjKey,
								actionObjectStored);
					}

				}

				whiteboardObject.getRoomItems().remove(objectOID);

				WhiteBoardObjectListManagerById.getInstance()
						.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id,
								whiteboardObject, whiteBoardId);
			} else if (action.equals("size") || action.equals("editProp")
					|| action.equals("editTextMindMapNode")
					|| action.equals("editText") || action.equals("swf")
					|| action.equals("flv")
					|| action.equals("editTextMindMapColor")
					|| action.equals("editTextMindMapFontColor")) {
				WhiteboardObject whiteboardObject = WhiteBoardObjectListManagerById
						.getInstance()
						.getWhiteBoardObjectListByRoomIdAndWhiteboard(room_id,
								whiteBoardId);
				String objectOID = actionObject.get(actionObject.size() - 1)
						.toString();
				// List roomItem = roomList.get(objectOID);
				List currentObject = whiteboardObject.getRoomItems().get(
						objectOID);
				whiteboardObject.getRoomItems().put(objectOID, actionObject);

				Map roomList = whiteboardObject.getRoomItems();

				if (action.equals("swf")) {

					log.debug("actionObject " + actionObject);

					if (actionObject.get(0).equals("swf")) {

						if (actionObject.get(8) != currentObject.get(8)) {

							String baseObjectName = actionObject.get(
									actionObject.size() - 1).toString();
							Integer slidesNumber = Integer.valueOf(
									actionObject.get(8).toString()).intValue();

							log.debug("updateObjectsToSlideNumber :: "
									+ baseObjectName + "," + slidesNumber);

							for (Iterator<String> iter = roomList.keySet()
									.iterator(); iter.hasNext();) {
								String whiteboardObjKey = iter.next();
								List actionObjectStored = (List) roomList
										.get(whiteboardObjKey);

								if (actionObjectStored.get(0).equals("ellipse")
										|| actionObjectStored.get(0).equals(
												"drawarrow")
										|| actionObjectStored.get(0).equals(
												"line")
										|| actionObjectStored.get(0).equals(
												"clipart")
										|| actionObjectStored.get(0).equals(
												"paint")
										|| actionObjectStored.get(0).equals(
												"rectangle")
										|| actionObjectStored.get(0).equals(
												"uline")
										|| actionObjectStored.get(0).equals(
												"image")
										|| actionObjectStored.get(0).equals(
												"letter")) {

									Map swfObj = (Map) actionObjectStored
											.get(actionObjectStored.size() - 7);
									log.debug("swfObj :1: " + swfObj);

									if (swfObj != null) {

										if (swfObj.get("name").equals(
												baseObjectName)) {

											if (Integer.valueOf(
													swfObj.get("slide")
															.toString())
													.intValue() == slidesNumber) {

												swfObj.put("isVisible", true);

												actionObjectStored.set(
														actionObjectStored
																.size() - 7,
														swfObj);

											} else {

												swfObj.put("isVisible", false);

												actionObjectStored.set(
														actionObjectStored
																.size() - 7,
														swfObj);

											}

										}

									}

									log.debug("swfObj :1: " + swfObj);

								}

							}

						}

					}
				}

				WhiteBoardObjectListManagerById.getInstance()
						.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id,
								whiteboardObject, whiteBoardId);
			} else if (action.equals("clearSlide")) {

				WhiteboardObject whiteboardObject = WhiteBoardObjectListManagerById
						.getInstance()
						.getWhiteBoardObjectListByRoomIdAndWhiteboard(room_id,
								whiteBoardId);

				Map roomList = whiteboardObject.getRoomItems();

				for (String objectName : (List<String>) actionObject) {

					roomList.remove(objectName);

				}

				WhiteBoardObjectListManagerById.getInstance()
						.setWhiteBoardObjectListRoomObjAndWhiteboardId(room_id,
								whiteboardObject, whiteBoardId);
			} else {
				log.warn("Unkown Type: " + action + " actionObject: "
						+ actionObject);
			}

		} catch (Exception err) {
			log.error("[addWhiteBoardObject]", err);
		}
	}

}
