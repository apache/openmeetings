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
package org.apache.openmeetings.templates;

import java.io.StringWriter;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class RegisterUserTemplate extends VelocityLoader {

	@Autowired
	private FieldManager fieldManager;

	private static final String templateName = "register_mail.vm";

	private static final String templateNameVerification = "register_verification_mail.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(
			RegisterUserTemplate.class, OpenmeetingsVariables.webAppRootKey);

	public String getRegisterUserWithVerificationTemplate(String username,
			String userpass, String email, Long default_lang_id,
			String verification_url) {
		try {

			super.init();

			Fieldlanguagesvalues labelid507 = fieldManager
					.getFieldByIdAndLanguage(new Long(507), default_lang_id);
			Fieldlanguagesvalues labelid508 = fieldManager
					.getFieldByIdAndLanguage(new Long(508), default_lang_id);
			Fieldlanguagesvalues labelid509 = fieldManager
					.getFieldByIdAndLanguage(new Long(509), default_lang_id);
			Fieldlanguagesvalues labelid510 = fieldManager
					.getFieldByIdAndLanguage(new Long(510), default_lang_id);
			Fieldlanguagesvalues labelid667 = fieldManager
					.getFieldByIdAndLanguage(new Long(667), default_lang_id);
			Fieldlanguagesvalues labelid668 = fieldManager
					.getFieldByIdAndLanguage(new Long(668), default_lang_id);

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();

			context.put("username", username);
			context.put("userpass", userpass);
			context.put("mail", email);
			context.put("verification_url", verification_url);
			context.put("labelid506", fieldManager.getString(506L, default_lang_id));
			context.put("labelid507", labelid507.getValue());
			context.put("labelid508", labelid508.getValue());
			context.put("labelid509", labelid509.getValue());
			context.put("labelid510", labelid510.getValue());
			context.put("labelid511", fieldManager.getString(511L, default_lang_id));
			context.put("labelid667", labelid667.getValue());
			context.put("labelid668", labelid668.getValue());

			/* lets render a template */

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(templateNameVerification, "UTF-8", context,
					w);

			// System.out.println(" template : " + w );

			return w.toString();

		} catch (Exception e) {
			log.error("Problem merging template : ", e);
			// System.out.println("Problem merging template : " + e );
		}
		return null;
	}

	public String getRegisterUserTemplate(String username, String userpass,
			String email, Long default_lang_id) {
		try {
			
			super.init();

			Fieldlanguagesvalues labelid507 = fieldManager
					.getFieldByIdAndLanguage(new Long(507), default_lang_id);
			Fieldlanguagesvalues labelid508 = fieldManager
					.getFieldByIdAndLanguage(new Long(508), default_lang_id);
			Fieldlanguagesvalues labelid509 = fieldManager
					.getFieldByIdAndLanguage(new Long(509), default_lang_id);
			Fieldlanguagesvalues labelid510 = fieldManager
					.getFieldByIdAndLanguage(new Long(510), default_lang_id);

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();

			context.put("username", username);
			context.put("userpass", userpass);
			context.put("mail", email);
			context.put("labelid506", fieldManager.getString(506L, default_lang_id));
			context.put("labelid507", labelid507.getValue());
			context.put("labelid508", labelid508.getValue());
			context.put("labelid509", labelid509.getValue());
			context.put("labelid510", labelid510.getValue());
			context.put("labelid511", fieldManager.getString(511L, default_lang_id));

			/* lets render a template */

			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(templateName, "UTF-8", context, w);

			// System.out.println(" template : " + w );

			return w.toString();

		} catch (Exception e) {
			log.error("Problem merging template : ", e);
			// System.out.println("Problem merging template : " + e );
		}
		return null;
	}
}
