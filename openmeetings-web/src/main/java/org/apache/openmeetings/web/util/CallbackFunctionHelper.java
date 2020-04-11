/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openmeetings.web.util;

import static java.util.UUID.randomUUID;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.util.string.StringValue;

public class CallbackFunctionHelper {
	private CallbackFunctionHelper() {}

	private static StringBuilder getNamedFunctionStr(String name, AbstractDefaultAjaxBehavior b, CallbackParameter... extraParameters) {
		StringBuilder sb = new StringBuilder();
		sb.append("function ").append(name).append("(");
		boolean first = true;
		for (CallbackParameter curExtraParameter : extraParameters) {
			if (curExtraParameter.getFunctionParameterName() != null) {
				if (first) {
					first = false;
				} else {
					sb.append(',');
				}
				sb.append(curExtraParameter.getFunctionParameterName());
			}
		}
		sb.append(") {\n");
		sb.append(b.getCallbackFunctionBody(extraParameters));
		sb.append("}\n");
		return sb;
	}

	public static StringValue getParam(Component cmp, String name) {
		return cmp.getRequest().getRequestParameters().getParameterValue(name);
	}

	public static JavaScriptHeaderItem getNamedFunction(String name, AbstractDefaultAjaxBehavior b, CallbackParameter... extraParameters) {
		String uid = randomUUID().toString();
		return JavaScriptHeaderItem.forScript(getNamedFunctionStr(name, b, extraParameters), String.format("%s-%s", name, uid));
	}
}
