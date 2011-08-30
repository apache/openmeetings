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

	public Map getParameterMap() {		
		return new HashMap<String, String>(requestParams_);
	}

	public Enumeration getParameterNames() {
		return Collections.enumeration(requestParams_.keySet());
	}

	public String[] getParameterValues(String name) {
		ArrayList<String> values = new ArrayList<String>(requestParams_.values());
		return values.toArray(new String[0]);
	}
}

