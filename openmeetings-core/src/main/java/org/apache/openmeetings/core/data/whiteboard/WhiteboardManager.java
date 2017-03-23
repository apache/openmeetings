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
package org.apache.openmeetings.core.data.whiteboard;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Map;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class WhiteboardManager {
	private static final Logger log = Red5LoggerFactory.getLogger(WhiteboardManager.class, webAppRootKey);

	@Autowired
	private WhiteboardCache whitebardCache;

	@SuppressWarnings("unchecked")
	public void add(Long roomId, Map<Integer, Object> whiteboardObj, Long whiteBoardId) {
		/* FIXME TODO check this
		try {
			log.debug("add: ", whiteboardObj);

			String action = whiteboardObj.get(2).toString();

			log.debug("action: " + action);
			log.debug("actionObject: " + whiteboardObj.get(3));

			List<Object> actionObject = (List<Object>) whiteboardObj.get(3);

			Whiteboard wb = whitebardCache.get(roomId, whiteBoardId);
			if (action.equals("moveMap")) {
				wb.setX(Integer.valueOf(actionObject.get(1).toString()));
				wb.setY(Integer.valueOf(actionObject.get(2).toString()));
			} else if (action.equals("draw") || action.equals("redo")) {
				// log.debug(actionObject);
				// log.debug(actionObject.size()-1);
				// log.debug(actionObject.get(actionObject.size()-1));
				if (actionObject != null && !actionObject.isEmpty()) {
					String objectType = actionObject.get(0).toString();
					if (!objectType.equals("pointerWhiteBoard")) {
						String objectOID = actionObject.get(actionObject.size() - 1).toString();
						log.debug("objectOID: " + objectOID);
						wb.add(objectOID, actionObject);
					}
				}
			} else if (action.equals("clear")) {
				wb.clear();
			} else if (action.equals("delete") || action.equals("undo")) {
				wb.remove(actionObject);
			} else if (action.equals("size") || action.equals("editProp")
					|| action.equals("editTextMindMapNode")
					|| action.equals("editText") || action.equals("swf")
					|| action.equals("flv")
					|| action.equals("editTextMindMapColor")
					|| action.equals("editTextMindMapFontColor")) {
				String objectOID = actionObject.get(actionObject.size() - 1).toString();
				// List roomItem = roomList.get(objectOID);
				List<Object> currentObject = wb.get(objectOID);
				if ("paint".equals(actionObject.get(0))) {
					actionObject.set(1, currentObject.get(1));
				}
				wb.add(objectOID, actionObject);

				if (action.equals("swf")) {
					log.debug("actionObject " + actionObject);
					if (actionObject.get(0).equals("swf")) {
						if (actionObject.get(8) != currentObject.get(8)) {
							String baseObjectName = actionObject.get(actionObject.size() - 1).toString();
							Integer slidesNumber = Integer.valueOf(actionObject.get(8).toString());

							log.debug("updateObjectsToSlideNumber :: " + baseObjectName + "," + slidesNumber);

							for (Entry<String, List<Object>> me : wb.entrySet()) {
								List<Object> actionObjectStored = me.getValue();

								if (actionObjectStored.get(0).equals("ellipse")
										|| actionObjectStored.get(0).equals("drawarrow")
										|| actionObjectStored.get(0).equals("line")
										|| actionObjectStored.get(0).equals("clipart")
										|| actionObjectStored.get(0).equals("paint")
										|| actionObjectStored.get(0).equals("rectangle")
										|| actionObjectStored.get(0).equals("uline")
										|| actionObjectStored.get(0).equals("image")
										|| actionObjectStored.get(0).equals("letter")) {

									Map<String, Object> swfObj = (Map<String, Object>) actionObjectStored.get(actionObjectStored.size() - 7);
									log.debug("swfObj :1: " + swfObj);

									if (swfObj != null) {
										if (swfObj.get("name").equals(baseObjectName)) {
											swfObj.put("isVisible", Integer.parseInt(swfObj.get("slide").toString()) == slidesNumber);
											actionObjectStored.set(actionObjectStored.size() - 7, swfObj);
										}
									}

									log.debug("swfObj :1: " + swfObj);
								}
							}
						}
					}
				}
			} else if (action.equals("clearSlide")) {
				for (Object objectName : actionObject) {
					wb.remove(objectName);
				}
			} else if (action.equals("whiteboardObj")) {
				wb.setFullFit((Boolean) actionObject.get(1));
				wb.setZoom((Integer) actionObject.get(2));
			} else {
				log.warn("Unkown Type: " + action + " actionObject: " + actionObject);
			}
		} catch (Exception err) {
			log.error("[add]", err);
		}
		*/
	}
}
