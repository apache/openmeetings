<%@ page import="org.apache.axis2.Constants" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.apache.axis2.context.ServiceContext" %>
<%@ page import="org.apache.axis2.context.ServiceGroupContext" %>
<%@ page import="org.apache.axis2.description.AxisService" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<jsp:include page="include/adminheader.jsp"/>
<h1>Running Context Hierarchy</h1>
<%
    ServiceContext serviceContext = (ServiceContext) request.getSession().getAttribute("ServiceContext");
    String type = (String) request.getSession().getAttribute("TYPE");
    if (serviceContext != null) {
        if (type != null) {
            if ("VIEW".equals(type)) {
                Iterator propertyNames = serviceContext.getPropertyNames();
%>
<h4>Persistance Properties</h4><ul>
    <%
        while (propertyNames.hasNext()) {
            String key = (String) propertyNames.next();
            Object property = serviceContext.getProperty(key);
    %>
    <li><%=key%> : <%=property.toString()%></li>
    <%
        }
    %></ul>
<%
    }
} else {
%> <h4>No Service Context Found</h4><%
        }
    }
%>
<jsp:include page="include/adminfooter.inc"/>