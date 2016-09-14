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
package org.apache.openmeetings.screenshare.job;

import static java.lang.Boolean.TRUE;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import static org.apache.openmeetings.screenshare.util.Util.getInt;
import static org.slf4j.LoggerFactory.getLogger;

public class OmKeyEvent {
	private static final Logger log = getLogger(OmKeyEvent.class);
	private static final Map<Integer, Integer> KEY_MAP = new HashMap<>();
	static {
		KEY_MAP.put(13, KeyEvent.VK_ENTER);
		KEY_MAP.put(16, 0);
		KEY_MAP.put(20, KeyEvent.VK_CAPS_LOCK);
		KEY_MAP.put(46, KeyEvent.VK_DELETE);
		KEY_MAP.put(110, KeyEvent.VK_DECIMAL);
		KEY_MAP.put(186, KeyEvent.VK_SEMICOLON);
		KEY_MAP.put(187, KeyEvent.VK_EQUALS);
		KEY_MAP.put(188, KeyEvent.VK_COMMA);
		KEY_MAP.put(189, KeyEvent.VK_MINUS);
		KEY_MAP.put(190, KeyEvent.VK_PERIOD);
		KEY_MAP.put(191, KeyEvent.VK_SLASH);
		KEY_MAP.put(219, KeyEvent.VK_OPEN_BRACKET);
		KEY_MAP.put(220, KeyEvent.VK_BACK_SLASH);
		KEY_MAP.put(221, KeyEvent.VK_CLOSE_BRACKET);
		KEY_MAP.put(222, KeyEvent.VK_QUOTE);
	}
	private boolean alt = false;
	private boolean ctrl = false;
	private boolean shift = false;
	private int key = 0;
	private char ch = 0;
	
	public OmKeyEvent(int key) {
		this(key, false);
	}

	public OmKeyEvent(int key, boolean shift) {
		this.key = key;
		this.shift = shift;
	}

	public OmKeyEvent(Map<String, Object> obj) {
		alt = TRUE.equals(obj.get("alt"));
		ctrl = TRUE.equals(obj.get("ctrl"));
		shift = TRUE.equals(obj.get("shift"));
		ch = (char)getInt(obj, "char");
		Integer _key = null;
		int key = getInt(obj, "key");
		if (key == 0) {
			if (ch == 61) {
				this.key = KeyEvent.VK_EQUALS;
			}
		} else {
			_key = KEY_MAP.get(key);
			this.key = _key == null ? key : _key;
		}
		log.debug("sequence:: shift {}, orig {} -> key {}, _key {}", shift, key, this.key, _key);
	}

	public int[] sequence() {
		List<Integer> list = new ArrayList<>();
		if (alt) {
			list.add(KeyEvent.VK_ALT);
		}
		if (ctrl) {
			list.add(KeyEvent.VK_CONTROL);
		}
		if (shift) {
			list.add(KeyEvent.VK_SHIFT);
		}
		if (key != 0) {
			list.add(key);
		}
		return list.stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OmKeyEvent)) {
			return false;
		}
		OmKeyEvent other = (OmKeyEvent) obj;
		if (key != other.key) {
			return false;
		}
		return true;
	}
}
