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
package org.apache.openmeetings.web.service.mail.template;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.wicket.util.string.Strings;
import org.apache.openmeetings.service.mail.template.InvitationTemplate;
import org.apache.openmeetings.service.mail.template.FeedbackTemplate;
import org.apache.openmeetings.service.mail.template.RegisterUserTemplate;
import org.apache.openmeetings.service.mail.template.RequestContactConfirmTemplate;
import org.apache.openmeetings.service.mail.template.RequestContactTemplate;
import org.apache.openmeetings.service.mail.template.ResetPasswordTemplate;
import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestEmailTemplate extends AbstractWicketTesterTest {
	private static void checkTemplate(String eml) {
		assertFalse(Strings.isEmpty(eml), "Body should be not empty");
	}

	@Test
	void testNullLocale() {
		User u = new User();
		u.setLanguageId(666L);
		checkTemplate(InvitationTemplate.getEmail(u, "testuser", "email", "message", true));
	}

	private static User templateUser() {
		User u = new User();
		u.setLanguageId(rnd.nextInt(40));
		return u;
	}

	@Test
	void testTemplateGeneration() {
		User u = templateUser();
		UserContact uc = new UserContact();
		uc.setOwner(u);
		uc.setContact(new User());
		checkTemplate(FeedbackTemplate.getEmail("testuser", "email", "message"));
		checkTemplate(RegisterUserTemplate.getEmail("testuser", "email", "message"));
		checkTemplate(RequestContactConfirmTemplate.getEmail(uc));
		checkTemplate(RequestContactTemplate.getEmail(u, new User()));
		checkTemplate(ResetPasswordTemplate.getEmail("link"));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void testInvitationTemplateGeneration(boolean room) {
		User u = templateUser();
		checkTemplate(InvitationTemplate.getEmail(u, "testuser", "email", "message", room));
	}
}
