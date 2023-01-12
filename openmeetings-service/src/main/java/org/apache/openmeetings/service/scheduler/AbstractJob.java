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
package org.apache.openmeetings.service.scheduler;

import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.ObjLongConsumer;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.user.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


public abstract class AbstractJob {
	private static Logger log = LoggerFactory.getLogger(AbstractJob.class);
	@Inject
	GroupDao groupDao;
	@Inject
	RecordingDao recordingDao;

	void processExpiringRecordings(boolean notified, ObjLongConsumer<Recording> consumer) {
		if (!isInitComplete()) {
			return;
		}
		for (Group g : groupDao.getLimited()) {
			for (Recording rec : recordingDao.getExpiring(g.getId(), g.getReminderDays(), notified)) {
				try {
					long days = g.getRecordingTtl() - ChronoUnit.DAYS.between(rec.getInserted().toInstant(), Instant.now());
					consumer.accept(rec, days);
				} catch (Exception e) {
					log.error("Uexpected exception while processing expiring recordings emails", e);
				}
			}
		}
	}
}
