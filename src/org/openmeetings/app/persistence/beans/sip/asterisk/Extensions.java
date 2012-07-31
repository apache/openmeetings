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
package org.openmeetings.app.persistence.beans.sip.asterisk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Entity
@Table(name = "extensions")
@Root(name="extension")
public class Extensions implements Serializable {
	private static final long serialVersionUID = -2669622904594408644L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Element(data = true)
    private Integer id;

    @Column(name = "context", nullable = false)
    private String context = "";

    @Column(name = "exten", nullable = false)
	@Element(data = true)
    private String exten = "";

    @Column(name = "priority", nullable = false)
	@Element(data = true)
    private Integer priority = 0;

    @Column(name = "app", nullable = false)
	@Element(data = true)
    private String app = "";

    @Column(name = "appdata", nullable = false)
	@Element(data = true)
    private String appdata = "";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getExten() {
        return exten;
    }

    public void setExten(String exten) {
        this.exten = exten;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAppdata() {
        return appdata;
    }

    public void setAppdata(String appdata) {
        this.appdata = appdata;
    }
}