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
package org.apache.openmeetings.web.common;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

//TODO should be removed in favor of AjaxFormValidatingBehavior after WICKET-5178 will be fixed
public class OmAjaxFormValidatingBehavior extends AjaxFormValidatingBehavior {
	private static final long serialVersionUID = 1L;

	public OmAjaxFormValidatingBehavior(Form<?> form, String event) {
		super(form, event);
	}
	
	public static void addToAllFormComponents(final Form<?> form, final String event, final Duration throttleDelay) {
		form.visitChildren(FormComponent.class, new FormValidateVisitor(form, event, throttleDelay));
	}

	private static class FormValidateVisitor implements IVisitor<Component, Void>, IClusterable
	{
		private static final long serialVersionUID = 1L;
		private final Form<?> form;
		private final String event;
		private final Duration throttleDelay;

		private FormValidateVisitor(Form<?> form, String event, Duration throttleDelay)
		{
			this.form = form;
			this.event = event;
			this.throttleDelay = throttleDelay;
		}

		public void component(final Component component, final IVisit<Void> visit)
		{
			final AjaxFormValidatingBehavior behavior = new AjaxFormValidatingBehavior(form, event)
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void updateAjaxAttributes(final AjaxRequestAttributes attributes)
				{
					super.updateAjaxAttributes(attributes);

					if (throttleDelay != null)
					{
						String id = "throttle-" + component.getMarkupId();
						ThrottlingSettings throttlingSettings = new ThrottlingSettings(id,
							throttleDelay);
						attributes.setThrottlingSettings(throttlingSettings);
					}
					attributes.setAllowDefault(true);
				}
			};
			component.add(behavior);
			visit.dontGoDeeper();
		}
	}
}
