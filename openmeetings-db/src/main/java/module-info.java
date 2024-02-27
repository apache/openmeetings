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
module org.apache.openmeetings.db {
	exports org.apache.openmeetings;
	exports org.apache.openmeetings.db.bind;
	exports org.apache.openmeetings.db.bind.adapter;
	exports org.apache.openmeetings.db.dao;
	exports org.apache.openmeetings.db.dao.basic;
	exports org.apache.openmeetings.db.dao.calendar;
	exports org.apache.openmeetings.db.dao.file;
	exports org.apache.openmeetings.db.dao.label;
	exports org.apache.openmeetings.db.dao.log;
	exports org.apache.openmeetings.db.dao.record;
	exports org.apache.openmeetings.db.dao.room;
	exports org.apache.openmeetings.db.dao.server;
	exports org.apache.openmeetings.db.dao.user;
	exports org.apache.openmeetings.db.dto.basic;
	exports org.apache.openmeetings.db.dto.calendar;
	exports org.apache.openmeetings.db.dto.file;
	exports org.apache.openmeetings.db.dto.record;
	exports org.apache.openmeetings.db.dto.room;
	exports org.apache.openmeetings.db.dto.user;
	exports org.apache.openmeetings.db.entity;
	exports org.apache.openmeetings.db.entity.basic;
	exports org.apache.openmeetings.db.entity.calendar;
	exports org.apache.openmeetings.db.entity.file;
	exports org.apache.openmeetings.db.entity.label;
	exports org.apache.openmeetings.db.entity.log;
	exports org.apache.openmeetings.db.entity.record;
	exports org.apache.openmeetings.db.entity.room;
	exports org.apache.openmeetings.db.entity.server;
	exports org.apache.openmeetings.db.entity.user;
	exports org.apache.openmeetings.db.manager;
	exports org.apache.openmeetings.db.mapper;
	exports org.apache.openmeetings.db.util;
	exports org.apache.openmeetings.db.util.ws;

	requires transitive com.github.openjson;

	requires org.apache.commons.codec;
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;

	requires org.apache.openjpa;

	requires transitive org.apache.openmeetings.util;

	requires org.apache.wicket.core;
	requires org.apache.wicket.extensions;
	requires org.apache.wicket.request;
	requires org.apache.wicket.util;
	requires org.apache.wicket.websocket.core;

	requires spring.beans;
	requires spring.web;
	requires spring.core;
	requires spring.tx;
	requires spring.context;

	requires jakarta.annotation;
	requires jakarta.inject;
	requires transitive jakarta.persistence;
	requires jakarta.servlet;
	requires jakarta.xml.bind;

	requires org.dom4j;
	requires org.slf4j;
	requires ical4j.core;
}
