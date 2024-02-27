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
module org.apache.openmeetings.util {
	exports org.apache.openmeetings.util;
	exports org.apache.openmeetings.util.crypt;
	exports org.apache.openmeetings.util.mail;
	exports org.apache.openmeetings.util.process;
	exports org.apache.openmeetings.util.ws;
	exports org.apache.openmeetings.util.logging;

	requires jakarta.activation;
	requires jakarta.servlet;

	requires com.github.openjson;

	requires org.apache.commons.codec;
	requires transitive org.apache.commons.lang3;

	requires org.apache.tika.core;

	requires org.apache.wicket.util;
	requires org.apache.wicket.core;
	requires org.apache.wicket.extensions;

	requires org.slf4j;
	requires spring.context;

	requires org.bouncycastle.provider;
	requires ical4j.core;
	requires org.aspectj.tools;
	requires org.dom4j;
	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;

	requires io.prometheus.metrics.core;
	requires io.prometheus.metrics.model;
}
