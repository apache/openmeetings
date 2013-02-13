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
import java.util.Date;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class InvitationTemplate extends VelocityLoader {

	private static final String templateName = "invitation.vm";

	@Autowired
	private FieldManager fieldManager;
	@Autowired
	private ConfigurationDao configurationDao;

	private static final Logger log = Red5LoggerFactory.getLogger(
			InvitationTemplate.class, OpenmeetingsVariables.webAppRootKey);

	public String getRegisterInvitationTemplate(String user, String message,
			String invitation_link, Long default_lang_id, Date dStart, Date dEnd) {
		try {

			super.init();

			Fieldlanguagesvalues labelid501 = fieldManager
					.getFieldByIdAndLanguage(new Long(501), default_lang_id);
			Fieldlanguagesvalues labelid502 = fieldManager
					.getFieldByIdAndLanguage(new Long(502), default_lang_id);
			Fieldlanguagesvalues labelid503 = fieldManager
					.getFieldByIdAndLanguage(new Long(503), default_lang_id);
			Fieldlanguagesvalues labelid504 = fieldManager
					.getFieldByIdAndLanguage(new Long(504), default_lang_id);
			Fieldlanguagesvalues labelid505 = fieldManager
					.getFieldByIdAndLanguage(new Long(505), default_lang_id);
			// Fieldlanguagesvalues labelid570 =
			// fieldmanagment.getFieldByIdAndLanguage(new Long(570),
			// default_lang_id);
			// Fieldlanguagesvalues labelid571 =
			// fieldmanagment.getFieldByIdAndLanguage(new Long(571),
			// default_lang_id);

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();

			context.put("user", user);
			context.put("message", message);
			context.put("invitation_link", invitation_link);
			context.put("invitation_link2", invitation_link);

			context.put("labelid500", fieldManager.getString(500L, default_lang_id));
			context.put("labelid501", labelid501.getValue());
			context.put("labelid502", labelid502.getValue());
			context.put("labelid503", labelid503.getValue());
			context.put("labelid504", labelid504.getValue());
			context.put("labelid505", labelid505.getValue());
			context.put("APP_NAME", configurationDao.getAppName());


			/* lets render a template */
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(templateName, "UTF-8", context, w);

			return w.toString();

		} catch (Exception e) {
			log.error("Problem merging template : ", e);
			// System.out.println("Problem merging template : " + e );
		}
		return null;
	}

	/**
	 * 
	 * @param user
	 * @param message
	 * @param invitation_link
	 * @param default_lang_id
	 * @return
	 */
	public String getReminderInvitationTemplate(String user, String message,
			String invitation_link, Long default_lang_id) {

		try {

			super.init();

			Fieldlanguagesvalues labelid623 = fieldManager
					.getFieldByIdAndLanguage(new Long(623), default_lang_id);
			Fieldlanguagesvalues labelid624 = fieldManager
					.getFieldByIdAndLanguage(new Long(624), default_lang_id);
			Fieldlanguagesvalues labelid625 = fieldManager
					.getFieldByIdAndLanguage(new Long(625), default_lang_id);
			Fieldlanguagesvalues labelid626 = fieldManager
					.getFieldByIdAndLanguage(new Long(626), default_lang_id);
			Fieldlanguagesvalues labelid627 = fieldManager
					.getFieldByIdAndLanguage(new Long(627), default_lang_id);

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();

			context.put("user", user);
			context.put("message", message);
			context.put("invitation_link", invitation_link);
			context.put("invitation_link2", invitation_link);
			context.put("labelid500", fieldManager.getString(622L, default_lang_id));
			context.put("labelid501", labelid623.getValue());
			context.put("labelid502", labelid624.getValue());
			context.put("labelid503", labelid625.getValue());
			context.put("labelid504", labelid626.getValue());
			context.put("labelid505", labelid627.getValue());
			context.put("APP_NAME", configurationDao.getAppName());

			/* lets render a template */
			StringWriter w = new StringWriter();
			Velocity.mergeTemplate(templateName, "UTF-8", context, w);

			return w.toString();

		} catch (Exception e) {
			log.error("Problem merging template : ", e);
			// System.out.println("Problem merging template : " + e );
		}
		return null;
	}

}
