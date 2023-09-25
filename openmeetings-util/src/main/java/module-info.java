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

	requires transitive org.apache.wicket.util;
	requires transitive org.apache.wicket.core;
	requires transitive org.apache.wicket.extensions;
	requires transitive org.apache.commons.lang3;
	requires transitive com.github.openjson;
	requires transitive org.slf4j;
	requires transitive javax.servlet.api;
	requires transitive org.apache.tika.core;
	requires transitive spring.context;
	requires transitive org.apache.commons.codec;

	requires org.bouncycastle.provider;
	requires jakarta.activation;
	requires org.mnode.ical4j.core;
	requires org.aspectj.tools;
	requires org.dom4j;
	requires simpleclient;
	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;
}
