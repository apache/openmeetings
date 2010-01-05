<%@ page import="org.apache.axis2.Constants"%>
<%@ page import="org.apache.axis2.context.ConfigurationContext"%>
<%@ page import="org.apache.axis2.context.ServiceContext"%>
<%@ page import="org.apache.axis2.context.ServiceGroupContext"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Iterator"%>
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
<jsp:include page="include/adminheader.jsp"></jsp:include>
<h1>Running Context Hierarchy</h1>
<%
    ConfigurationContext configContext = (ConfigurationContext) request.getSession().getAttribute(
            Constants.CONFIG_CONTEXT);

    String[] serviceGroupIds = configContext.getServiceGroupContextIDs();
    if (serviceGroupIds.length > 0) {
%>
<ul>
    <%

   for (int i = 0; i < serviceGroupIds.length; i++) {
        String groupContextID = serviceGroupIds[i];
        ServiceGroupContext groupContext = configContext.getServiceGroupContext(groupContextID);
        %>
           <li><%=groupContextID%><font color="blue"><a href="axis2-admin/viewServiceGroupConetxt?TYPE=VIEW&ID=<%=groupContextID%>">
                    View</a></font>  <font color="red"><a href="axis2-admin/viewServiceGroupConetxt?TYPE=DELETE&ID=<%=groupContextID%>">
                    Remove</a> </font></li>
        <%
        Iterator serviceContextItr = groupContext.getServiceContexts();
            %><ul><%
        while (serviceContextItr.hasNext()) {
            ServiceContext serviceContext = (ServiceContext)serviceContextItr.next();
             String serviceConID = serviceContext.getAxisService().getName();
        %>
            <li><%=serviceConID%><font color="blue"><a href="axis2-admin/viewServiceContext?TYPE=VIEW&ID=<%=serviceConID%>&PID=<%=groupContextID%>">
                    View</a></font></li>
        <%
        }
                %></ul><hr><%
    }
    %>  </ul>
        <%
            } else {%>
	<p>No running contexts were found on the system.</p>
            <%}
%>
<jsp:include page="include/adminfooter.inc"></jsp:include>
