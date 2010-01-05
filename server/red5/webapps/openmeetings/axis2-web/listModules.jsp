 <%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>
<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisModule,
                 java.util.Collection"%>
 <%@ page import="java.util.Enumeration"%>
 <%@ page import="java.util.HashMap"%>
 <%@ page import="java.util.Hashtable"%>
 <%@ page import="java.util.Iterator"%>
 <%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="include/adminheader.jsp"></jsp:include>
  <h1>Available Modules</h1>
     <%
         boolean foundModules = false;
	boolean wroteUL = false;
         HashMap moduleMap = (HashMap)request.getSession().getAttribute(Constants.MODULE_MAP);
         request.getSession().setAttribute(Constants.MODULE_MAP,null);
         Hashtable errornesModules =(Hashtable)request.getSession().getAttribute(Constants.ERROR_MODULE_MAP);
         if (moduleMap!=null && !moduleMap.isEmpty()){
             String modulename ;
             String moduleDescription ;
             Collection moduleNames = moduleMap.values();
             for (Iterator iterator = moduleNames.iterator(); iterator.hasNext();) {
                 foundModules = true;
                 AxisModule  moduleQName = (AxisModule) iterator.next();
                 modulename = moduleQName.getName();
                 moduleDescription = moduleQName.getModuleDescription();
                 if(moduleDescription==null){
                     moduleDescription = "[ --  No module description found  --]";
                 }
        if (!wroteUL){
			wroteUL = true;
%>
	<ul>
<%
		}
     %><li><b><%=modulename%></b> : <%=moduleDescription%></li>
      <%
             }
		if (wroteUL){
%>
	</ul>
<%
		}
        }
      %>
      <%if(errornesModules.size()>0){
          %>
      <h3><font color="red">Faulty Modules</font></h3>
             <%
             Enumeration faultyModules = errornesModules.keys();
             while (faultyModules.hasMoreElements()) {
                 foundModules = true;
                 String faultyModuleName = (String) faultyModules.nextElement();
             %><a href="errorModule.jsp?moduleName=<%=faultyModuleName%>">
                    <%=faultyModuleName%></a>
                    <%
             }
      }
             if(! foundModules) {
                 %>
                 <h2><font color="blue">There are no modules deployed in the system.</font></h2>
                 <%
             }
     %>
<jsp:include page="include/adminfooter.inc"></jsp:include>