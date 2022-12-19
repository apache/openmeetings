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

import java.io.Serializable;

import org.apache.openmeetings.util.NullStringer;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class UndoObject implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Type {
		ADD
		, REMOVE
		, MODIFY
	}
	private final WbAction action;
	private final String origObject;
	private final Type type;
	private final String object;

	public UndoObject(WbAction action, JSONObject origObj, Type type, JSONObject obj) {
		this.action = action;
		this.origObject = origObj.toString(new NullStringer());
		this.type = type;
		this.object = obj.toString(new NullStringer());
	}

	public UndoObject(WbAction action, JSONObject origObj, Type type, JSONArray arr) {
		this.action = action;
		this.origObject = origObj.toString(new NullStringer());
		this.type = type;
		this.object = arr.toString(new NullStringer());
	}

	public WbAction getAction() {
		return action;
	}

	public JSONObject getOrigObject() {
		return new JSONObject(origObject);
	}

	public Type getType() {
		return type;
	}

	public String getObject() {
		return object;
	}

	@Override
	public String toString() {
		return "UndoObject [type=" + type + "]";
	}
}
