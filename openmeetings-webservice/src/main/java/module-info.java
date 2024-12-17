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

module org.apache.openmeetings.webservice {
	exports org.apache.openmeetings.webservice.util;

	requires org.apache.openmeetings.core;
	requires transitive org.apache.openmeetings.db;
	requires org.apache.openmeetings.service;
	requires org.apache.openmeetings.util;

	requires com.github.openjson;
	requires org.apache.commons.codec;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;

	requires jakarta.annotation;
	requires jakarta.inject;
	requires jakarta.servlet;
	requires jakarta.xml.ws;
	requires jakarta.ws.rs;

	requires org.apache.cxf.core;
	requires org.apache.cxf.frontend.jaxrs;

	requires org.apache.pdfbox;

	requires org.apache.wicket.core;
	requires org.apache.wicket.extensions;
	requires org.apache.wicket.util;

	requires io.swagger.v3.oas.annotations;
	requires java.desktop;

	requires spring.beans;
	requires spring.context;

	requires org.slf4j;
}
