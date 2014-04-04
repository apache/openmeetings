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

import java.text.ParseException;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.AbstractMarkupFilter;
import org.apache.wicket.markup.parser.filter.WicketMessageTagHandler;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.util.string.Strings;

public class MessageTagHandler extends AbstractMarkupFilter implements
		IComponentResolver {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The id automatically assigned to tags with wicket:message attribute but
	 * without id
	 */
	public final static String WICKET_MESSAGE_CONTAINER_ID = "_message_attr_";

	/**
	 * Constructor for the IComponentResolver role.
	 */
	public MessageTagHandler() {
		this(null);
	}

	/**
	 * Constructor for the IMarkupFilter role.
	 */
	public MessageTagHandler(final MarkupResourceStream markupResourceStream) {
		super(markupResourceStream);
	}

	@Override
	protected final MarkupElement onComponentTag(ComponentTag tag)
			throws ParseException {
		if (tag.isClose()) {
			return tag;
		}

		final String wicketMessageAttribute = tag.getAttributes().getString(
				getWicketMessageAttrName());

		if (Strings.isEmpty(wicketMessageAttribute) == false) {
			// check if this tag is raw markup
			if (tag.getId() == null) {
				// if this is a raw tag we need to set the id to something so
				// that wicket will not merge this as raw markup and instead
				// pass it on to a resolver
				tag.setId(getWicketMessageIdPrefix());
				tag.setAutoComponentTag(true);
				tag.setModified(true);
			}
			tag.addBehavior(new AttributeLocalizer(getWicketMessageAttrName()));
		}

		return tag;
	}

	/**
	 * Attribute localizing behavior. See the javadoc of
	 * {@link WicketMessageTagHandler} for details.
	 * 
	 * @author Igor Vaynberg (ivaynberg)
	 */
	public static class AttributeLocalizer extends Behavior {
		private static final long serialVersionUID = 1L;

		private final String wicketMessageAttrName;

		public AttributeLocalizer(String wicketMessageAttrName) {
			this.wicketMessageAttrName = wicketMessageAttrName;
		}

		@Override
		public void onComponentTag(final Component component,
				final ComponentTag tag) {
			String expr = tag.getAttributes().getString(wicketMessageAttrName);
			if (!Strings.isEmpty(expr)) {
				expr = expr.trim();

				String[] attrsAndKeys = Strings.split(expr, ',');

				for (String attrAndKey : attrsAndKeys) {
					int colon = attrAndKey.lastIndexOf(":");
					// make sure the attribute-key pair is valid
					if (attrAndKey.length() < 3 || colon < 1
							|| colon > attrAndKey.length() - 2) {
						throw new WicketRuntimeException(
								"wicket:message attribute contains an invalid value [["
										+ expr
										+ "]], must be of form (attr:key)+");
					}

					String attr = attrAndKey.substring(0, colon);
					String key = attrAndKey.substring(colon + 1);
					final String value = WebSession.getString(Long.parseLong(key));
					tag.put(attr, value);
				}
			}
		}
	}

	public Component resolve(final MarkupContainer container, final MarkupStream markupStream,
			final ComponentTag tag) {
		// localize any raw markup that has wicket:message attrs
		if ((tag != null)
				&& (tag.getId().startsWith(getWicketMessageIdPrefix()))) {
			Component wc;
			int autoIndex = container.getPage().getAutoIndex();
			String id = getWicketMessageIdPrefix() + autoIndex;

			if (tag.isOpenClose()) {
				wc = new WebComponent(id);
			} else {
				wc = new TransparentWebMarkupContainer(id);
			}

			return wc;
		}
		return null;
	}

	private String getWicketMessageAttrName() {
		String wicketNamespace = getWicketNamespace();
		return wicketNamespace + ':' + "ommessage";
	}

	private String getWicketMessageIdPrefix() {
		return getWicketNamespace() + WICKET_MESSAGE_CONTAINER_ID;
	}
}
