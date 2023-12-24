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

module org.apache.openmeetings.core {
	exports org.apache.openmeetings.core.converter;
	exports org.apache.openmeetings.core.data.file;
	exports org.apache.openmeetings.core.ldap;
	exports org.apache.openmeetings.core.mail;
	exports org.apache.openmeetings.core.notifier;
	exports org.apache.openmeetings.core.rss;
	exports org.apache.openmeetings.core.sip;
	exports org.apache.openmeetings.core.util;

	requires transitive com.github.openjson;

	requires jakarta.annotation;
	requires jakarta.inject;
	requires jakarta.mail;

	requires org.apache.commons.io;
	requires org.apache.commons.lang3;

	requires org.apache.tika.core;
	requires org.apache.tika.parser.image;

	requires transitive org.apache.openmeetings.db;
	requires transitive org.apache.openmeetings.util;

	requires org.apache.wicket.core;
	requires org.apache.wicket.websocket.core;
	requires org.apache.wicket.util;

	requires java.xml;

	requires xstream;

	requires jodconverter.core;
	requires jodconverter.local;
	requires org.apache.directory.ldap.api.all;
	requires asterisk.java;
	requires jain.sip.ri;

	requires spring.context;

	requires org.slf4j;
}
