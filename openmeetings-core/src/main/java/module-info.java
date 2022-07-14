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

	requires transitive org.apache.openmeetings.db;

	requires transitive org.apache.commons.io;
	requires transitive java.annotation;
	requires transitive java.xml;

	requires xstream;

	requires jodconverter.core;
	requires jodconverter.local;
	requires org.apache.directory.ldap.api.all;
	requires javax.inject;
	requires asterisk.java;
	requires jain.sip.ri;
	requires org.apache.tika.parsers;
	requires jakarta.mail;
}
