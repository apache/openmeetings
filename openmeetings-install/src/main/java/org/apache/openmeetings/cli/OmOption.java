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
package org.apache.openmeetings.cli;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.Option;

public class OmOption extends Option {
	private static final long serialVersionUID = 1L;
	private String group = null;
	private Map<String, Boolean> optional = null;
	private int order = 0;
	private String helpPrefix = "";

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description, boolean optional) {
		this(group, 0, opt, longOpt, hasArg, description);
		setOptional(optional);
	}

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description, String optional) {
		this(group, 0, opt, longOpt, hasArg, description);
		setOptional(optional, true);
	}

	public OmOption(String group, int order, String opt, String longOpt, boolean hasArg,
			String description) {
		super(opt, longOpt, hasArg, description);
		this.group = group;
		this.setOrder(order);
	}

	public OmOption(String group, String opt, String longOpt, boolean hasArg,
			String description) {
		this(group, 0, opt, longOpt, hasArg, description);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isOptional(String group) {
		boolean result = false;
		if (optional != null) {
			String[] grps = group.split(",");
			for (String g : grps) {
				result |= optional.getOrDefault(g, false);
			}
		}
		return result;
	}

	public void setOptional(boolean val) {
		setOptional(group, val);
	}

	public void setOptional(String group, boolean val) {
		optional = Stream.of(group.split(","))
				.collect(Collectors.toMap(Function.identity(), s -> val));
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getHelpPrefix() {
		return helpPrefix;
	}

	public void setHelpPrefix(StringBuilder helpPrefix) {
		this.helpPrefix = helpPrefix.toString();
	}

	public void setHelpPrefix(String helpPrefix) {
		this.helpPrefix = helpPrefix;
	}
}
