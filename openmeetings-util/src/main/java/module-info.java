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
module apache.openmeetings.util {
	exports org.apache.openmeetings.util;
	exports org.apache.openmeetings.util.crypt;
	exports org.apache.openmeetings.util.mail;
	exports org.apache.openmeetings.util.process;
	exports org.apache.openmeetings.util.ws;
	exports org.apache.openmeetings.util.logging;

	requires transitive javax.servlet.api;

	requires transitive wicket.util;
	requires transitive wicket.core;
	requires transitive wicket.extensions;

	requires transitive org.apache.commons.codec;
	requires transitive org.apache.commons.lang3;

	requires transitive com.github.openjson;
	requires transitive org.slf4j;
	requires transitive logback.classic;
	requires transitive logback.core;
	requires transitive dom4j;
	requires transitive jcip.annotations;
	requires transitive org.bouncycastle.provider;
	requires transitive java.activation;
	requires transitive org.mnode.ical4j.core;
	requires transitive tika.core;
	requires org.aspectj.tools;
	requires spring.context;
	requires simpleclient;
}
