package org.openmeetings.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openmeetings.utils.stringhandlers.ChatString;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

import junit.framework.TestCase;

public class TestChatLinking extends TestCase {
	
	private Pattern patternUrl = null;
	private Matcher matchers = null;

	//private String url = "^(([\\w]+)?:\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*?\\/)?([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$";
	private String url = "(?i)(\\b(http://|https://|www.|ftp://|file:/|mailto:)\\S+)(\\s+)";
	private String linkBefore = "<u><FONT color=\"#0000CC\"><a href=\"";
	private String linkMiddleOne = "\" title=\"";
	private String linkMiddleTwo = "\" target=\"_blank\">";
	private String linkAfter = "</a></FONT></u>";
	private String output, protocol;
	
	public void testChatParser(){
		try {
			
			String text = "hasn http://www.google.de peter hasn http://www.google.de ";
			
			System.out.println(this.link(text));
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	
	public String link(String input) {
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
			e.printStackTrace();
		}
		return "";
	}

}
