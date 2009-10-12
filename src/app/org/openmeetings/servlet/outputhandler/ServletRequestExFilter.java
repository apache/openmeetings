package org.openmeetings.servlet.outputhandler;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.openmeetings.servlet.outputhandler.HttpServletRequestEx;

public class ServletRequestExFilter implements Filter {

	private FilterConfig config = null;  

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
		config = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		//We need our filter only to handle download requests, therefore we only
		//need it with GET requests. In addition, it can only work with GET requests
		//in its current condition
		if  ( req.getMethod() == "GET" )
		{
			HttpServletRequestEx requestEx = new HttpServletRequestEx(req, config.getInitParameter("uriEncoding"));
			chain.doFilter(requestEx, response);
		}
		else
		{
			chain.doFilter(req, response);
		}
	}
}

