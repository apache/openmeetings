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
package org.apache.openmeetings.web.app;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;

/**
 * Map property values to OpenMeetings labelid's and get the string
 * 
 * @author swagner
 *
 */
public class LabelResourceLoader implements IStringResourceLoader {
	
	private static Map<String,Long> STRING_LABEL_MAPPING = new HashMap<String,Long>();
	
	static {
		STRING_LABEL_MAPPING.put("PagingNavigator.first", 368L);
		STRING_LABEL_MAPPING.put("PagingNavigator.previous", 369L);
		STRING_LABEL_MAPPING.put("PagingNavigator.next", 370L);
		STRING_LABEL_MAPPING.put("PagingNavigator.last", 371L);
	}

	public String loadStringResource(Class<?> clazz, String key, Locale locale,
			String style, String variation) {
		return getStringByPropertyKey(key);
	}

	public String loadStringResource(Component component, String key,
			Locale locale, String style, String variation) {
		return getStringByPropertyKey(key);
	}
	
	private String getStringByPropertyKey(String key) {
		Long labelId = STRING_LABEL_MAPPING.get(key);
		if (labelId == null) 
			return null;
		return Application.getString(labelId);
	}

}
