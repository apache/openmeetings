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
package org.openmeetings.app.backup;

import java.util.Date;

import org.openmeetings.utils.math.CalendarPatterns;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class DateConverter implements Converter<Date> {
	public Date read(InputNode node) throws Exception {
		String val = node.getValue();
		return val == null ? new Date() : CalendarPatterns.parseImportDate(val);
	}

	public void write(OutputNode node, Date value) throws Exception {
		node.setAttribute("class", "java.util.Date");
		node.setData(true);
		node.setValue(value == null ? "0" : CalendarPatterns.getExportDate(value));
	}
}