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
package org.apache.openmeetings.test;

/**
 * David Bismut, david.bismut@gmail.com
 * Intern, SETLabs, Infosys Technologies Ltd. May 2004 - Jul 2004
 * Ecole des Mines de Nantes, France
 */
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.accessibility.AccessibleHypertext;
import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * A "HTML" JEditorPane embedded with a special HTMLDocument that detects urls
 * and displays them as hyperlinks. When CTRL is pressed, the urls are
 * clickable.
 * 
 * @author David Bismut
 *  
 */
public class EditorPaneLinkDetector extends JEditorPane {
	private static final long serialVersionUID = 2811878994346374017L;

	/**
	 * Creates a <code>EditorPaneLinkDetector</code>.
	 */
	public EditorPaneLinkDetector() {

		HTMLEditorKit htmlkit = new HTMLEditorKit();

		StyleSheet styles = htmlkit.getStyleSheet();
		StyleSheet ss = new StyleSheet();

		ss.addStyleSheet(styles);

		ss.addRule("body {font-family:arial;font-size:12pt}");
		ss.addRule("p {font-family:arial;margin:2}");

		HTMLDocument doc = new HTMLDocLinkDetector(ss);

		setEditorKit(htmlkit);

		setDocument(doc);

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {

				AccessibleJTextComponent context = (AccessibleJTextComponent) getAccessibleContext()
						.getAccessibleEditableText();

				AccessibleHypertext accText = (AccessibleHypertext) context
						.getAccessibleText();

				int index = accText.getIndexAtPoint(e.getPoint());

				int linkIndex = accText.getLinkIndex(index);
				if (linkIndex == -1) {
					setToolTipText(null);
					return;
				}

				String linkDesc = accText.getLink(linkIndex)
						.getAccessibleActionDescription(0);

				String toolTipText = "<html><body style='margin: 3'>"
						+ linkDesc
						+ "<br><b>CTRL + click to follow link</b></body></html>";
				setToolTipText(toolTipText);
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					if (isEditable())
						setEditable(false);
				} else {
					if (!isEditable())
						setEditable(true);
				}

			}

			public void keyReleased(KeyEvent e) {
				
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					setEditable(true);
				}

			}
		});
	}

	protected class HTMLDocLinkDetector extends HTMLDocument {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1226244167782160437L;

		public HTMLDocLinkDetector(StyleSheet ss) {
			super(ss);
			
			setAsynchronousLoadPriority(4);
			setTokenThreshold(100);
			setParser(new ParserDelegator());
		}

		/**
		 * Returns true if the Element contains a HTML.Tag.A attribute, false
		 * otherwise.
		 * 
		 * @param e
		 *            the Element to be checkd
		 * @return
		 */
		protected boolean isLink(Element e) {
			
			return (e.getAttributes().getAttribute(HTML.Tag.A) != null);

		}

		/**
		 * This method corrects or creates a url contained in an Element as an
		 * hyperlink.
		 * 
		 * @param e
		 *            the Element to be computed
		 * @throws BadLocationException
		 */
		protected void computeLinks(Element e) throws BadLocationException {
			
			int caretPos = getCaretPosition();
			try {
				if (isLink(e))
					correctLink(e);
				else
					createLink(e);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			setCaretPosition(Math.min(caretPos, getLength()));
		}

		/**
		 * The method corrects the url inside an Element, that is supposed to be
		 * an element containing a link only. This function is typically called
		 * when the url is beeing edited. What the function does is to remove
		 * the html tags, so the url is actually edited in plain text and not as
		 * an hyperlink.
		 * 
		 * @param e
		 *            the Element that contains the url
		 * @throws BadLocationException
		 * @throws IOException
		 */
		protected void correctLink(Element e) throws BadLocationException,
				IOException {
			
			int length = e.getEndOffset() - e.getStartOffset();

			boolean endOfDoc = e.getEndOffset() == getLength() + 1;

			// to avoid catching the final '\n' of the document.
			if (endOfDoc)
				length--;

			String text = getText(e.getStartOffset(), length);

			setOuterHTML(e, text);

			// insert final spaces ignored by the html
			Matcher spaceMatcher = Pattern.compile("(\\s+)$").matcher(text);

			if (spaceMatcher.find()) {
				String endingSpaces = spaceMatcher.group(1);
				insertString(Math.min(getLength(), e.getEndOffset()),
						endingSpaces, null);
			}
		}

		/**
		 * The method check if the element contains a url in plain text, and if
		 * so, it creates the html tag HTML.Tag.A to have the url displayed as
		 * an hyperlink.
		 * 
		 * @param e
		 *            element that contains the url
		 * @throws BadLocationException
		 * @throws IOException
		 */
		protected void createLink(Element e) throws BadLocationException,
				IOException {
			
			int caretPos = getCaretPosition();

			int startOffset = e.getStartOffset();
			int length = e.getEndOffset() - e.getStartOffset();

			boolean endOfDoc = e.getEndOffset() == getLength() + 1;
			// to avoid catching the final '\n' of the document.
			if (endOfDoc)
				length--;

			String text = getText(startOffset, length);

			Matcher matcher = Pattern.compile(
					"(?i)(\\b(http://|https://|www.|ftp://|file:/|mailto:)\\S+)(\\s+)")
					.matcher(text);

			if (matcher.find()) {
				String url = matcher.group(1);
				//String prefix = matcher.group(2);
				String endingSpaces = matcher.group(3);

				// to ignore characters after the caret
				int validPos = startOffset + matcher.start(3) + 1;
				if (validPos > caretPos)
					return;

				Matcher dotEndMatcher = Pattern.compile("([\\W&&[^/]]+)$")
						.matcher(url);

				//Ending non alpha characters like [.,?%] shouldn't be included
				// in the url.
				String endingDots = "";
				if (dotEndMatcher.find()) {
					endingDots = dotEndMatcher.group(1);
					url = dotEndMatcher.replaceFirst("");
				}

				text = matcher.replaceFirst("<a href='" + url + "'>" + url
						+ "</a>" + endingDots + endingSpaces);

				setOuterHTML(e, text);

				// insert initial spaces ignored by the html
				Matcher spaceMatcher = Pattern.compile("^(\\s+)").matcher(text);

				if (spaceMatcher.find()) {
					String initialSpaces = spaceMatcher.group(1);
					insertString(startOffset, initialSpaces, null);
				}

				// insert final spaces ignored by the html
				spaceMatcher = Pattern.compile("(\\s+)$").matcher(text);

				if (spaceMatcher.find()) {
					String extraSpaces = spaceMatcher.group(1);
					int endoffset = e.getEndOffset();
					if (extraSpaces.charAt(extraSpaces.length() - 1) == '\n') {
						extraSpaces = extraSpaces.substring(0, extraSpaces
								.length() - 1);
						endoffset--;
					}
					insertString(Math.min(getLength(), endoffset), extraSpaces,
							null);
				}
			}
		}

		public void remove(int offs, int len) throws BadLocationException {

			super.remove(offs, len);
			Element e = getCharacterElement(offs - len);
			computeLinks(e);
		}

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {

			super.insertString(offs, str, a);
			Element e = getCharacterElement(offs);
			computeLinks(e);
		}
	}
}
