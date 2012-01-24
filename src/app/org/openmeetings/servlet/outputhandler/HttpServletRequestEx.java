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
package org.openmeetings.servlet.outputhandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class HttpServletRequestEx extends HttpServletRequestWrapper {
	private HttpServletRequest request_ = null;
	private Map<String, String> requestParams_ = null;
	private String uriEncoding;

	public HttpServletRequestEx(HttpServletRequest req, String uriEncoding)	throws IOException {
		super(req);

		this.uriEncoding = uriEncoding;

		request_ = req;
		requestParams_ = new HashMap<String, String>();
		
		String queryString = request_.getQueryString();
		
		if ( queryString == null )
		{
			return ;
		}
		
		queryString = new String(queryString.getBytes("ISO-8859-1"), "UTF-8");

		String[] params = queryString.split("&");
		for (String param : params)	{
			String[] nameValue = param.split("=");
			requestParams_.put(nameValue[0], 2 == nameValue.length? nameValue[1] : null);
		}
	}

	// Methods to replace HSR methods
	public String getParameter(String name) {
		try 
		{
			if (requestParams_.get(name) != null )
			{
				return URLDecoder.decode(requestParams_.get(name),uriEncoding);
			}
			else
			{
				return null;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			return null;
		}
	}

	public Map<String, String> getParameterMap() {		
		return new HashMap<String, String>(requestParams_);
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(requestParams_.keySet());
	}

	public String[] getParameterValues(String name) {
		ArrayList<String> values = new ArrayList<String>(requestParams_.values());
		return values.toArray(new String[0]);
	}
}

