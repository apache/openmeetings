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
package org.apache.openmeetings.test.chat;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestChatLinking {
	private static final Logger log = Red5LoggerFactory.getLogger(TestChatLinking.class, webAppRootKey);
	
	@Test
	public void testChatParser(){
		try {
			
			String text = "hasn http://www.google.de peter hasn http://www.google.de ";
			
			System.out.println(link(text));
			
		} catch (Exception err) {
			log.error("Error", err);
		}
	}
	
	
	private String link(String input) {
		try {
			
			String tReturn = "";
			
			String parts[] = input.split(" ");
			
			for (int t=0;t<parts.length;t++) {
				
				String text = parts[t];
	
				//System.out.println("Part 1 "+text);
				
				Matcher matcher = Pattern.compile("(^|[ \t\r\n])((ftp|http|https|gopher|mailto|news|nntp|telnet|wais|file|prospero|aim|webcal):(([A-Za-z0-9$_.+!*(),;/?:@&~=-])|%[A-Fa-f0-9]{2}){2,}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*(),;/?:@&~=%-]*))?([A-Za-z0-9$_+!*();/?:~-]))").matcher(text);

				if (matcher.find()) {
					text = matcher.replaceFirst("<u><FONT color=\"#0000CC\"><a href='" + text + "'>" + text
							+ "</a></FONT></u>");

				}
				
				//System.out.println("Part 2 "+text);
				
				if (t != 0) {
					tReturn += " ";
				}
				
				tReturn += text;
			
			}
			
			return tReturn;
			
		} catch (Exception e) {
			log.error("Error", e);
		}
		return "";
	}

}
