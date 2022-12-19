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

import java.util.stream.Stream;

public enum WbAction {
	CREATE_WB("createWb")
	, REMOVE_WB("removeWb")
	, ACTIVATE_WB("activateWb")
	, RENAME_WB("renameWb")
	, SET_SLIDE("setSlide")
	, CREATE_OBJ("createObj")
	, MODIFY_OBJ("modifyObj")
	, DELETE_OBJ("deleteObj")
	, CLEAR_ALL("clearAll")
	, CLEAR_SLIDE("clearSlide")
	, SAVE("save")
	, LOAD("load")
	, UNDO("undo")
	, REDO("redo")
	, SET_SIZE("setSize")
	, DOWNLOAD("download")
	, START_RECORDING("startRecording")
	, STOP_RECORDING("stopRecording")
	, VIDEO_STATUS("videoStatus")
	, LOAD_VIDEOS("loadVideos");

	private final String jsName;

	private WbAction(String jsName) {
		this.jsName = jsName;
	}

	public String jsName() {
		return jsName;
	}

	public static WbAction of(String jsName) {
		return Stream.of(WbAction.values())
				.filter(a -> a.jsName.equals(jsName))
				.findAny()
				.orElse(null);
	}
}
