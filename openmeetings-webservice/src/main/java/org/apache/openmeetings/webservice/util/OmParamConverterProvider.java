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
package org.apache.openmeetings.webservice.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;

import org.apache.openmeetings.db.dto.calendar.AppointmentDTO;
import org.apache.openmeetings.db.dto.user.UserDTO;

public class OmParamConverterProvider implements ParamConverterProvider {

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
		if (Calendar.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>)new CalendarParamConverter();
		} else if (Date.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>)new DateParamConverter();
		} else if (AppointmentDTO.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>)new AppointmentParamConverter();
		} else if (UserDTO.class.isAssignableFrom(rawType)) {
			return (ParamConverter<T>)new UserParamConverter();
		}
		return null;
	}
}
