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
	requires org.apache.openmeetings.core;
	requires org.apache.openmeetings.db;
	requires org.apache.openmeetings.install;
	requires org.apache.openmeetings.mediaserver;
	requires org.apache.openmeetings.service;
	requires org.apache.openmeetings.util;
	requires org.apache.openmeetings.webservice;

	requires com.github.openjson;

	requires totp;

	requires org.apache.commons.io;
	requires org.apache.commons.fileupload2.core;
	requires org.apache.commons.fileupload2.jakarta;
	requires org.apache.commons.lang3;

	requires org.apache.httpcomponents.httpclient;

	requires org.apache.wicket.auth.roles;
	requires org.apache.wicket.core;
	requires org.apache.wicket.devutils;
	requires org.apache.wicket.extensions;
	requires org.apache.wicket.ioc;
	requires org.apache.wicket.request;
	requires org.apache.wicket.spring;
	requires org.apache.wicket.util;
	requires org.apache.wicket.websocket.core;

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

	requires jakarta.annotation;
	requires jakarta.inject;
	requires jakarta.servlet;
	requires jakarta.websocket.client;
	requires jakarta.ws.rs;

	requires java.net.http;
	requires java.management;
	requires java.sql;

	requires com.hazelcast.core;
	requires org.danekja.jdk.serializable.functional;
	requires xstream;

	requires spring.beans;
	requires spring.context;
	requires spring.orm;
	requires spring.web;

	requires org.slf4j;

	requires prometheus.metrics.core;
	requires prometheus.metrics.model;
	requires prometheus.metrics.exporter.servlet.jakarta;
}
