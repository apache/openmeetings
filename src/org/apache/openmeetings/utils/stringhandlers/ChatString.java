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
package org.apache.openmeetings.utils.stringhandlers;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ChatString {
	private static final Logger log = Red5LoggerFactory.getLogger(
			ChatString.class, OpenmeetingsVariables.webAppRootKey);

	private static String htmlToText(String source) {
		String result = source;
		result = Strings.replaceAll(result, "<", "&lt;").toString();
		result = Strings.replaceAll(result, ">", "&gt;").toString();
		return result;
	}
	
	public static LinkedList<String[]> parseChatString(String message, LinkedList<LinkedList<String>> emotFiles, boolean allowHTML) {
		try {
			LinkedList<String[]> list = new LinkedList<String[]>();

			// log.debug("this.link(message) "+this.link(message));

			if (!allowHTML)
				message = htmlToText(message);
			
			String[] messageStr = { "text", message };
			list.add(messageStr);

			for (LinkedList<String> emot : emotFiles) {
				// log.error("CHECK EMOT: "+ emot.get(0) + emot.get(1) +
				// emot.size());
				list = splitStr(list, emot.get(0), emot.get(1),
						emot.get(emot.size() - 2));

				if (emot.size() > 4) {
					// log.error("CHECK EMOT ASIAN: "+ emot.get(0) + emot.get(2)
					// + emot.size());
					list = splitStr(list, emot.get(0), emot.get(2),
							emot.get(emot.size() - 2));
				}
			}

			// log.debug("#########  ");
			// for (Iterator<String[]> iter = list.iterator();iter.hasNext();){
			// String[] stringArray = iter.next();
			// //stringArray[1] = this.link(stringArray[1]);
			// log.debug(stringArray[0]+"||"+stringArray[1]);
			// }

			return list;

		} catch (Exception err) {
			log.error("[parseChatString]", err);
		}
		return null;
	}

	private static LinkedList<String[]> splitStr(LinkedList<String[]> list,
			String image, String regexp, String spaces) {

		LinkedList<String[]> newList = new LinkedList<String[]>();

		for (Iterator<String[]> iter = list.iterator(); iter.hasNext();) {

			String[] messageObj = iter.next();
			String messageTye = messageObj[0];

			if (messageTye.equals("text")) {
				String messageStr = messageObj[1];

				String[] newStr = messageStr.split(regexp);

				for (int k = 0; k < newStr.length; k++) {
					String[] textA = { "text", newStr[k] };
					newList.add(textA);
					if (k + 1 != newStr.length) {
						String[] imageA = { "image", image, spaces,
								regexp.replace("\\", "") };
						newList.add(imageA);
					}
				}
			} else {

				newList.add(messageObj);
			}

		}

		return newList;
	}

	public static LinkedList<LinkedList<String>> replaceAllRegExp(LinkedList<LinkedList<String>> emotFiles) {
		LinkedList<LinkedList<String>> emotfilesListNew = new LinkedList<LinkedList<String>>();
		try {
			for (LinkedList<String> emot : emotFiles) {
				// log.error("FILE: "+emot.get(0));
				String westernMeaning = checkforRegex(emot.get(1));
				emot.set(1, westernMeaning);
				// log.error("westernMeaning "+westernMeaning);
				if (emot.size() > 2) {
					String asianMeaning = checkforRegex(emot.get(2));
					emot.set(2, asianMeaning);
					// log.error("westernMeaning "+asianMeaning);
				}
				emotfilesListNew.add(emot);
			}
		} catch (Exception err) {
			log.error("[replaceAllRegExp]", err);
		}
		return emotfilesListNew;
	}

	/**
	 * Replace characters having special meaning in regular expressions
	 * 
	 */
	private static String checkforRegex(String aRegexFragment) {
		final StringBuilder result = new StringBuilder();

		final StringCharacterIterator iterator = new StringCharacterIterator(
				aRegexFragment);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			/*
			 * All literals need to have backslashes doubled.
			 */
			if (character == '.') {
				result.append("\\.");
			} else if (character == '\\') {
				result.append("\\\\");
			} else if (character == '?') {
				result.append("\\?");
			} else if (character == '*') {
				result.append("\\*");
			} else if (character == '+') {
				result.append("\\+");
			} else if (character == '&') {
				result.append("\\&");
			} else if (character == ':') {
				result.append("\\:");
			} else if (character == '{') {
				result.append("\\{");
			} else if (character == '}') {
				result.append("\\}");
			} else if (character == '[') {
				result.append("\\[");
			} else if (character == ']') {
				result.append("\\]");
			} else if (character == '(') {
				result.append("\\(");
			} else if (character == ')') {
				result.append("\\)");
			} else if (character == '^') {
				result.append("\\^");
			} else if (character == '$') {
				result.append("\\$");
			} else if (character == '|') {
				result.append("\\|");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public static String link(String input) {
		try {

			String tReturn = "";

			String parts[] = input.split(" ");

			for (int t = 0; t < parts.length; t++) {

				String text = parts[t];

				// System.out.println("Part 1 "+text);

				Matcher matcher = Pattern
						.compile(
								"(^|[ \t\r\n])((ftp|http|https|gopher|mailto|news|nntp|telnet|wais|file|prospero|aim|webcal):(([A-Za-z0-9$_.+!*(),;/?:@&~=-])|%[A-Fa-f0-9]{2}){2,}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*(),;/?:@&~=%-]*))?([A-Za-z0-9$_+!*();/?:~-]))")
						.matcher(text);

				if (matcher.find()) {
					text = matcher
							.replaceFirst("<u><FONT color=\"#0000CC\"><a href='"
									+ text + "'>" + text + "</a></FONT></u>");

				}

				// System.out.println("Part 2 "+text);

				if (t != 0) {
					tReturn += " ";
				}

				tReturn += text;

			}

			return tReturn;

		} catch (Exception e) {
			log.error("[link]", e);
		}
		return "";
	}
}
