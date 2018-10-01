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
package org.apache.openmeetings.service.mail.template;

import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.wicket.util.string.Strings;
import org.junit.Assert;
import org.junit.Test;

public class TestEmailTemplate extends AbstractWicketTester {
	private static void checkTemplate(String eml) {
		Assert.assertFalse("Body should be not empty", Strings.isEmpty(eml));
	}

	@Test
	public void testNullLocale() {
		User u = new User();
		u.setLanguageId(666L);
		checkTemplate(InvitationTemplate.getEmail(u, "testuser", "email", "message"));
	}

	@Test
	public void testTemplateGeneration() {
		User u = new User();
		u.setLanguageId(rnd.nextInt(30));
		UserContact uc = new UserContact();
		uc.setOwner(u);
		uc.setContact(new User());
		checkTemplate(FeedbackTemplate.getEmail("testuser", "email", "message"));
		checkTemplate(InvitationTemplate.getEmail(u, "testuser", "email", "message"));
		checkTemplate(RegisterUserTemplate.getEmail("testuser", "email", "message"));
		checkTemplate(RequestContactConfirmTemplate.getEmail(uc));
		checkTemplate(RequestContactTemplate.getEmail(u, new User()));
		checkTemplate(ResetPasswordTemplate.getEmail("link"));
	}
}
