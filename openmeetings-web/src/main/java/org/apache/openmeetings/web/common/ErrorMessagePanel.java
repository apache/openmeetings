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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.PrintWriter;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ErrorMessagePanel extends Panel {
	private static final Logger log = Red5LoggerFactory.getLogger(ErrorMessagePanel.class, getWebAppRootKey());
	private static final long serialVersionUID = 1L;

	public ErrorMessagePanel(String id, String msg, Throwable err) {
		super(id);

		log.error(msg, err);
		add(new Label("msg", msg));
		StringBuilderWriter sw = new StringBuilderWriter();
		err.printStackTrace(new PrintWriter(sw));
		add(new Label("err", sw.toString()));
	}
}
