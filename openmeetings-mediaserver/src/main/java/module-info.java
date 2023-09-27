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

module org.apache.openmeetings.mediaserver {
	exports org.apache.openmeetings.mediaserver;

	requires com.github.openjson;

	requires org.apache.commons.lang3;

	requires org.apache.openmeetings.core;
	requires org.apache.openmeetings.db;
	requires org.apache.openmeetings.util;

	requires org.apache.wicket.ioc;
	requires org.apache.wicket.util;

	requires jakarta.annotation;
	requires jakarta.inject;

	requires spring.beans;
	requires spring.context;
	requires spring.core;

	requires kurento.client;
	requires kurento.jsonrpc.client;
	requires kurento.commons;
	requires com.google.gson;

	requires org.slf4j;
}
