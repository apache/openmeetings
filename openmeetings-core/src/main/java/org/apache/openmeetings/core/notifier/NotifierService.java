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
package org.apache.openmeetings.core.notifier;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author iarkh
 *
 */
@Service
public class NotifierService {
	private static final Logger log = LoggerFactory.getLogger(NotifierService.class);

	private List<INotifier> notifiers = new ArrayList<>();

	public void addNotifier(INotifier n) {
		notifiers.add(n);
	}

	public void notify(User u, Appointment a, Invitation inv) {
		if (inv == null) {
			log.error("Error retrieving Invitation for member {} in Appointment {}"
					, u.getAddress().getEmail(), a.getTitle());
			return;
		}
		for (INotifier n : notifiers) {
			try {
				n.notify(u, a, inv);
			} catch (Exception e) {
				log.error("Unexpected exception while sending notifications", e);
			}
		}
	}
}
