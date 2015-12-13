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

public class ResetPasswordTemplate extends VelocityLoader {

	@Autowired
	private FieldManager fieldManager;

	private static final String templateName = "resetPass.vm";

	private static final Logger log = Red5LoggerFactory.getLogger(
			FeedbackTemplate.class, OpenmeetingsVariables.webAppRootKey);

	public String getResetPasswordTemplate(String reset_link,
			Long default_lang_id) {
		try {

			super.init();

			Fieldlanguagesvalues labelid514 = fieldManager
					.getFieldByIdAndLanguage(new Long(514), default_lang_id);
			Fieldlanguagesvalues labelid515 = fieldManager
					.getFieldByIdAndLanguage(new Long(515), default_lang_id);
			Fieldlanguagesvalues labelid516 = fieldManager
					.getFieldByIdAndLanguage(new Long(516), default_lang_id);

			/* lets make a Context and put data into it */
			VelocityContext context = new VelocityContext();
			context.put("reset_link", reset_link);
			context.put("reset_link2", reset_link);
			context.put("labelid513", fieldManager.getString(513L, default_lang_id));
			context.put("labelid514", labelid514.getValue());
			context.put("labelid515", labelid515.getValue());
			context.put("labelid516", labelid516.getValue());

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
