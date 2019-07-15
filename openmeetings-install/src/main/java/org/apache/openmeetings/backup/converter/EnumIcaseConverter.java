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
package org.apache.openmeetings.backup.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.entity.basic.Configuration;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class EnumIcaseConverter implements Converter<Configuration.Type> {
	private Map<String, Configuration.Type> icaseMap = new HashMap<>();

	public EnumIcaseConverter() {
		//default constructor is for export
	}

	public EnumIcaseConverter(Configuration.Type[] values) {
		Arrays.stream(values).forEach(t -> icaseMap.put(t.name().toUpperCase(Locale.ROOT), t));
	}

	@Override
	public Configuration.Type read(InputNode node) throws Exception {
		String name = node.getValue();
		return name == null ? null : icaseMap.get(name.toUpperCase(Locale.ROOT));
	}

	@Override
	public void write(OutputNode node, Configuration.Type value) throws Exception {
		node.setData(true);
		node.setValue(String.valueOf(value == null ? null : value.name()));
	}
}
