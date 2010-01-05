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
                 javax.xml.namespace.QName,
                 java.util.Collection,
                 java.util.Iterator"%>
 <%@ page import="org.apache.axis2.description.AxisModule" %>
 <%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="include/adminheader.jsp"></jsp:include>
<h1>Globally Engaged Modules</h1>
     <%
         boolean foundModules = false;
         boolean wroteUL = false;
         String modulename = "";
         Collection moduleCol = (Collection) request.getSession().getAttribute(Constants.MODULE_MAP);
         request.getSession().setAttribute(Constants.MODULE_MAP, null);
         if (moduleCol != null && moduleCol.size() > 0) {
             for (Iterator iterator = moduleCol.iterator(); iterator.hasNext();) {
                 AxisModule axisOperation = (AxisModule) iterator.next();
                 modulename = axisOperation.getName();
                 if (!wroteUL) {
                     wroteUL = true;
     %>
	<ul>
<%
		}
     %><li><%=modulename%></li>
     <br> <%
             }
		if (wroteUL){
%>
	</ul>
<%
		}
         } else{
     %>
    <h2><font color="blue">There are no modules engaged globally</font></h2> 
                 <%
         }
                 %>
<jsp:include page="include/adminfooter.inc"></jsp:include>
