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

module org.apache.openmeetings.install {
	exports org.apache.openmeetings.backup;
	exports org.apache.openmeetings.cli;
	exports org.apache.openmeetings.installation;

	requires com.github.openjson;

	requires org.apache.commons.cli;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;

	requires org.apache.openjpa;

	requires org.apache.openmeetings.core;
	requires transitive org.apache.openmeetings.db;
	requires org.apache.openmeetings.util;

	requires org.apache.wicket.core;
	requires org.apache.wicket.request;
	requires org.apache.wicket.util;

	requires jakarta.inject;
	requires jakarta.xml.bind;

	requires spring.context;
	requires spring.context.support;
	requires spring.web;

	requires java.desktop;
	requires org.quartz;
	requires xstream;

	requires org.slf4j;
}
