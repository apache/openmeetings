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
package org.openmeetings.utils.sms;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.http.BulkSmsHTTPGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

/**
 * 
 * @author iarkh
 * 
 */

public class SMSHandler {

	private static final Logger log = Red5LoggerFactory.getLogger(
			SMSHandler.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	protected Configurationmanagement cfgManagement;
	@Autowired
	protected TaskExecutor taskExecutor;

	private BulkSmsHTTPGateway gateway = null;
	
	private boolean checkBalance() throws Exception {
		if (gateway == null) {
			String smsProvider = cfgManagement.getConfValue("sms.provider", String.class, null);
			String smsUsername = cfgManagement.getConfValue("sms.username", String.class, null);
			String smsUserpass = cfgManagement.getConfValue("sms.userpass", String.class, null);

			if (smsProvider == null || smsProvider.length() == 0 ||
					smsUsername == null || smsUsername.length() == 0) {
				log.error("SMS Provider is not configured properly!");
				return false;
			}
			gateway = new BulkSmsHTTPGateway(smsProvider, smsUsername, smsUserpass);
			gateway.setOutbound(true);
			Service.getInstance().addGateway(gateway);
			Service.getInstance().startService();
		}
		return gateway.queryBalance() >= 1; 
	}
	
	public boolean sendSMS(String phone, String subj) {
		try {
			taskExecutor.execute(new SMSSenderTask(phone, subj));
			return true;
		} catch (Exception ex) {
			log.error("sendSMS", ex);
			return false;
		}
	}
	
	protected class SMSSenderTask implements Runnable {

		private final String phone;
		private final String subject;

		public SMSSenderTask(String phone, String subject) {
			this.phone = phone;
			this.subject = subject;
		}

		public void run() {
			this.send();
		}

		/**
		 * Sending an SMS with the given values.
		 * @return <code>true</code> if sms was sent successfully, <code>false</code> otherwise.
		 */
		public boolean send() {
			try {
				log.debug("SMS sending to: " + phone + ", subject is: " + subject);
				if (checkBalance()) {
					OutboundMessage msg = new OutboundMessage(phone, subject);
					return Service.getInstance().sendMessage(msg);
				} else {
					log.error("Error: insufficient funds on SMS provider account!");
					return false; 
				}
			} catch (Exception ex) {
				log.error("Error sending sms: ", ex);
				return false;
			} 
		}
	}
}
