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
package org.openlaszlo.generator.lfc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Node extends Eventable {
	
	@XmlAttribute
	public String classroot;
	@XmlAttribute
	public String cloneManager;
	@XmlAttribute
	public String data;
	@XmlAttribute
	public String datapath;
	@XmlAttribute
	public String defaultplacement;
	@XmlAttribute
	public String id;
	@XmlAttribute
	public Boolean ignoreplacement;
	@XmlAttribute
	public String immediateparent;
	@XmlAttribute
	public Boolean inited;
	@XmlEnum
	public enum initstage {early, normal, late };
	@XmlAttribute
	public String name;
	@XmlAttribute
	public Integer nodeLevel;
	@XmlAttribute
	public String options;
	@XmlAttribute
	public String parent;
	@XmlAttribute
	public String placement;
	@XmlAttribute
	public String styleclass;
	@XmlAttribute
	public String subnodes;
	@XmlAttribute
	public String transition;
	@XmlAttribute
	public String onconstruct;
	@XmlAttribute
	public String ondata;
	@XmlAttribute
	public String oninit; 

}
