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
package org.apache.openmeetings.web.app;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.parser.filter.WicketTagIdentifier;
import org.apache.wicket.markup.resolver.WicketMessageResolver;

public class MessageResolver extends WicketMessageResolver {
	private static final long serialVersionUID = -5755259669383826988L;
	private final static String TAG_NAME = "ommessage";
	
	static	{
		// register "wicket:ommessage"
		WicketTagIdentifier.registerWellKnownTagName(TAG_NAME);
	}
	
	@Override
	public Component resolve(MarkupContainer container, MarkupStream markupStream, ComponentTag tag) {
		if (tag instanceof WicketTag) {
			WicketTag wtag = (WicketTag)tag;
			if (TAG_NAME.equals(wtag.getName())) {
				Long messageKey = wtag.getAttributes().getAsLong("key");
				if (messageKey != null) {
					final String id = "_message_" + container.getPage().getAutoIndex();
					Label label = new Label(id, WebSession.getString(messageKey));
					label.setRenderBodyOnly(container.getApplication()
						.getMarkupSettings()
						.getStripWicketTags());

					return label;
				}
			}
		}
		return super.resolve(container, markupStream, tag);
	}
}
