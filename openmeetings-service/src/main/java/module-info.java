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

module org.apache.openmeetings.service {
	exports org.apache.openmeetings.service.calendar;
	exports org.apache.openmeetings.service.calendar.caldav;
	exports org.apache.openmeetings.service.mail;
	exports org.apache.openmeetings.service.mail.template;
	exports org.apache.openmeetings.service.mail.template.subject;
	exports org.apache.openmeetings.service.room;
	exports org.apache.openmeetings.service.scheduler;

	//FIXME TODO temporary
	exports org.apache.jackrabbit.webdav;
	exports org.apache.jackrabbit.webdav.xml;
	exports org.apache.jackrabbit.webdav.property;
	exports org.apache.jackrabbit.webdav.client.methods;

	requires org.apache.commons.lang3;
	requires org.apache.commons.text;

	requires transitive org.apache.openmeetings.db;
	requires org.apache.openmeetings.core;
	requires org.apache.openmeetings.util;

	requires jakarta.annotation;
	requires jakarta.inject;
	requires jakarta.servlet;

	requires spring.context;

	requires org.apache.wicket.core;
	requires org.apache.wicket.request;
	requires org.apache.wicket.spring;
	requires org.apache.wicket.util;

	requires org.apache.httpcomponents.httpcore;
	requires org.apache.httpcomponents.httpclient;
	//requires jackrabbit.webdav; FIXME TODO have to be placed back as soon as `jackrabbit.webdav` will be jackarta compatible
	requires caldav4j;
	requires java.xml;
	requires ical4j.core;

	requires org.slf4j;
}
