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

module org.apache.openmeetings.web {
	requires org.apache.openmeetings.install;
	requires org.apache.openmeetings.mediaserver;
	requires org.apache.openmeetings.webservice;

	requires org.apache.wicket.auth.roles;
	requires org.apache.wicket.devutils;
	requires wicketstuff.dashboard.core;
	requires wicketstuff.datastore.hazelcast;
	requires wicketstuff.select2;
	requires wicketstuff.urlfragment;
	requires jqplot;
	requires jqplot4java;
	requires wicket.bootstrap.extensions;
	requires wicket.bootstrap.core;
	requires wicket.bootstrap.themes;
	requires wicket.webjars;
	requires wicket.jquery.ui;
	requires wicket.jquery.ui.calendar;
	requires wicket.jquery.ui.core;
	requires wicket.jquery.ui.plugins;

	requires java.net.http;
	requires java.sql;
	requires javax.websocket.api;
	requires com.hazelcast.core;
	requires org.danekja.jdk.serializable.functional;
	requires spring.orm;
	requires java.management;
	requires simpleclient.servlet;
}
