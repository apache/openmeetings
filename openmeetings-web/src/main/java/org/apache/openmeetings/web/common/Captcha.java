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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Random;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxLink;

public class Captcha extends Panel {
	private static final long serialVersionUID = 1L;
	private String randomText;
	private final CaptchaImageResource captchaImageResource = new CaptchaImageResource() {
		private static final long serialVersionUID = 1L;
		private Random rnd = new Random();

		@Override
		protected byte[] render() {
			randomText = randomString(6, 8);
			getChallengeIdModel().setObject(randomText);
			return super.render();
		}

		private int randomInt(int min, int max) {
			return rnd.nextInt(max - min) + min;
		}

		private String randomString(int min, int max) {
			int num = randomInt(min, max);
			byte[] b = new byte[num];
			for (int i = 0; i < num; ++i) {
				b[i] = (byte)randomInt('a', 'z');
			}
			return new String(b, UTF_8);
		}
	};
	private final Image captcha = new Image("captcha", captchaImageResource);
	private final RequiredTextField<String> captchaText = new RequiredTextField<>("captchaText", Model.of(""));

	public Captcha(String id) {
		super(id);
		setOutputMarkupId(true);
		add(captcha.setOutputMarkupId(true));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(captchaText.setLabel(new ResourceModel("captcha.text")).add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				if (!randomText.equals(validatable.getValue())) {
					validatable.error(new ValidationError(getString("bad.captcha.text")));
				}
			}
		}).setOutputMarkupId(true));
		add(new AjaxLink<String>("refresh") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				captchaImageResource.invalidate();
				target.add(captcha);
			}

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				behavior.setOption("icon", Options.asString(JQueryIcon.REFRESH));
				behavior.setOption("showLabel", false);
			}
		});
	}

	public Image refresh(IPartialPageRequestHandler handler) {
		captchaImageResource.invalidate();
		captchaText.setModelObject("");
		if (handler != null) {
			handler.add(captchaText, captcha);
		}
		return captcha;
	}
}
