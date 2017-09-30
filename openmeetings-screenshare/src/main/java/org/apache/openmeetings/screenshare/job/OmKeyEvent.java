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
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toUpperCase;
import static javax.swing.KeyStroke.getKeyStroke;
import static org.apache.openmeetings.screenshare.util.Util.getInt;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.KeyStroke;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;

public class OmKeyEvent {
	private static final Logger log = getLogger(OmKeyEvent.class);
	private static final Map<Integer, Integer> KEY_MAP = new HashMap<>();
	private static final Map<Character, Integer> CHAR_MAP = new HashMap<>();
	private static final List<Character> _UMLAUTS = Arrays.asList('ß', 'ö', 'Ö', 'ä', 'Ä', 'ü', 'Ü');
	private static final Set<Character> UMLAUTS = Collections.unmodifiableSet(_UMLAUTS.stream().collect(Collectors.toSet()));
	private static final Set<Character> UNPRINTABLE = Collections.unmodifiableSet(Stream.concat(_UMLAUTS.stream(), Stream.of('§')).collect(Collectors.toSet()));
	static {
		KEY_MAP.put(13, KeyEvent.VK_ENTER);
		KEY_MAP.put(16, 0);
		KEY_MAP.put(20, KeyEvent.VK_CAPS_LOCK);
		KEY_MAP.put(43, KeyEvent.VK_ADD); //normal + -> numpad + ????
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

		CHAR_MAP.put(Character.valueOf('#'), KeyEvent.VK_NUMBER_SIGN);
		CHAR_MAP.put(Character.valueOf('<'), KeyEvent.VK_LESS);
		CHAR_MAP.put(Character.valueOf('.'), KeyEvent.VK_PERIOD);
		CHAR_MAP.put(Character.valueOf(','), KeyEvent.VK_COMMA);
		CHAR_MAP.put(Character.valueOf('-'), KeyEvent.VK_MINUS);
		CHAR_MAP.put(Character.valueOf('='), KeyEvent.VK_EQUALS);
		CHAR_MAP.put(Character.valueOf('['), KeyEvent.VK_OPEN_BRACKET);
		CHAR_MAP.put(Character.valueOf(']'), KeyEvent.VK_CLOSE_BRACKET);
		CHAR_MAP.put(Character.valueOf(';'), KeyEvent.VK_SEMICOLON);
		CHAR_MAP.put(Character.valueOf('\''), KeyEvent.VK_QUOTE);
		CHAR_MAP.put(Character.valueOf('\\'), KeyEvent.VK_BACK_SLASH);
		CHAR_MAP.put(Character.valueOf('`'), KeyEvent.VK_BACK_QUOTE);
		CHAR_MAP.put(Character.valueOf('/'), KeyEvent.VK_SLASH);
	}
	private boolean alt = false;
	private boolean ctrl = false;
	private boolean shift = false;
	private int inKey = 0;
	private int key = 0;
	private char ch = 0;

	public OmKeyEvent(Map<String, Object> obj) {
		alt = TRUE.equals(obj.get("alt"));
		ctrl = TRUE.equals(obj.get("ctrl"));
		shift = TRUE.equals(obj.get("shift")) || isUpperCase(ch);
		ch = (char)getInt(obj, "char");
		key = inKey = getInt(obj, "key");
		Integer _key = null;
		if (CharUtils.isAsciiPrintable(ch)) {
			boolean alpha = Character.isAlphabetic(ch);
			if (alpha) { // can't be combined due to different types
				key = getKeyStroke(toUpperCase(ch), 0).getKeyCode();
			} else {
				key = getKeyStroke(Character.valueOf(ch), 0).getKeyCode();
			}
			if (key == 0) {
				_key = CHAR_MAP.get(ch);
				if (_key == null) {
					// fallback
					key = inKey;
				}
			}
			if (!alpha && _key == null) {
				_key = KEY_MAP.get(key);
			}
		} else {
			_key = KEY_MAP.get(key);
		}
		this.key = _key == null ? key : _key;
		log.debug("sequence:: shift {}, ch {}, orig {} -> key {}({}), map {}", shift, ch == 0 ? ' ' : ch, inKey, key, Integer.toHexString(key), _key);
	}

	private static int getVowel(char ch) {
		int vowel = ch;
		switch(toUpperCase(ch)) {
			case 'Ö':
				vowel = KeyEvent.VK_O;
				break;
			case 'Ä':
				vowel = KeyEvent.VK_A;
				break;
			case 'Ü':
				vowel = KeyEvent.VK_U;
				break;
		}
		return vowel;
	}

	public void press(RemoteJob r) {
		List<Integer> list = new ArrayList<>();
		if (UNPRINTABLE.contains(ch)) {
			if (SystemUtils.IS_OS_LINUX) {
				r.press(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_U);
				String hex = Integer.toHexString(ch);
				log.debug("sequence:: hex {}", hex);
				for (int i = 0; i < hex.length(); ++i) {
					r.press(KeyStroke.getKeyStroke(toUpperCase(hex.charAt(i)), 0).getKeyCode());
				}
				r.press(KeyEvent.VK_ENTER);
			} else if (SystemUtils.IS_OS_MAC) {
				if (ch == 'ß') {
					r.press(KeyEvent.VK_ALT, KeyEvent.VK_S);
				} else {
					if (UMLAUTS.contains(ch)) {
						r.press(KeyEvent.VK_ALT, KeyEvent.VK_U);
						if (shift) {
							list.add(KeyEvent.VK_SHIFT);
						}
						list.add(getVowel(ch));
						r.press(list);
					}
				}
			} else if (SystemUtils.IS_OS_WINDOWS && UMLAUTS.contains(ch)) {
				list.add(KeyEvent.VK_ALT);
				list.add(KeyEvent.VK_ADD);
				String code = String.format("%04d", (int)ch);
				for (int i = 0; i < code.length(); ++i) {
					list.add(KeyEvent.VK_NUMPAD0 + code.charAt(i));
				}
				r.press(list);
			}
		} else {
			if (shift) {
				list.add(KeyEvent.VK_SHIFT);
			}
			if (alt) {
				list.add(KeyEvent.VK_ALT);
			}
			if (ctrl) {
				list.add(KeyEvent.VK_CONTROL);
			}
			if (key != 0) {
				list.add(key);
			}
			r.press(list);
		}
	}
}
