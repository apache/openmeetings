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
package org.openmeetings.app.backup;

import org.openmeetings.app.data.calendar.daos.AppointmentReminderTypDaoImpl;
import org.openmeetings.app.persistence.beans.calendar.AppointmentReminderTyps;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class AppointmentReminderTypeConverter extends OmConverter<AppointmentReminderTyps> {
	private AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl;
	
	public AppointmentReminderTypeConverter() {
		//default constructor is for export
	}
	
	public AppointmentReminderTypeConverter(AppointmentReminderTypDaoImpl appointmentReminderTypDaoImpl) {
		this.appointmentReminderTypDaoImpl = appointmentReminderTypDaoImpl;
	}
	
	public AppointmentReminderTyps read(InputNode node) throws Exception {
		return appointmentReminderTypDaoImpl.getAppointmentReminderTypById(getlongValue(node));
	}

	public void write(OutputNode node, AppointmentReminderTyps value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getTypId());
	}
}