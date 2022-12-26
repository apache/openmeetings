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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getDefaultLang;

import java.security.SecureRandom;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.AttributeModifier;
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

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;

public class Captcha extends Panel {
	private static final long serialVersionUID = 1L;
	private OmLanguage lang;
	private String randomText;
	private final CaptchaImageResource captchaImageResource = new CaptchaImageResource() {
		private static final long serialVersionUID = 1L;
		private SecureRandom rnd = new SecureRandom();

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
			char[] b = new char[num];
			for (int i = 0; i < num; ++i) {
				b[i] = (char)randomInt(lang.getRangeStart(), lang.getRangeEnd());
			}
			return new String(b);
		}
	};
	private Image captcha = new Image("captcha", captchaImageResource);
	private final RequiredTextField<String> captchaText = new RequiredTextField<>("captchaText", Model.of(""));

	public Captcha(String id) {
		super(id);
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		lang = LabelDao.getOmLanguage(WebSession.get().getLocale(), getDefaultLang());
		add(captcha.setOutputMarkupId(true));
		add(captchaText.setLabel(new ResourceModel("captcha.text")).add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> validatable) {
				if (!randomText.equals(validatable.getValue())) {
					validatable.error(new ValidationError(getString("bad.captcha.text")));
				}
			}
		}).setOutputMarkupId(true).add(AttributeModifier.append("placeholder", lang.getTip())));
		add(new BootstrapAjaxLink<>("refresh", Model.of(""), Buttons.Type.Outline_Info, new ResourceModel("lbl.refresh")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				captchaImageResource.invalidate();
				target.add(captcha);
			}

			@Override
			protected Icon newIcon(String markupId) {
				return new Icon(markupId, FontAwesome6IconType.rotate_s);
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
