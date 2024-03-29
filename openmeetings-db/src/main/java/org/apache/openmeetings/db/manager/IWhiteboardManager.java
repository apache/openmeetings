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
package org.apache.openmeetings.db.manager;

import java.util.function.BiConsumer;

import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.dto.room.Whiteboards;

import com.github.openjson.JSONArray;

public interface IWhiteboardManager {
	Whiteboards get(Long roomId);
	void reset(Long roomId, Long userId);
	void clearAll(Long roomId, long wbId, boolean redo, BiConsumer<Whiteboard, Boolean> consumer);
	void cleanSlide(Long roomId, long wbId, int slide, BiConsumer<Whiteboard, JSONArray> consumer);
}
