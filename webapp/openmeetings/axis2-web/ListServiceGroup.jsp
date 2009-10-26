<%@ page import="org.apache.axis2.Constants" %>
<%@ page import="org.apache.axis2.description.AxisService" %>
<%@ page import="org.apache.axis2.description.AxisServiceGroup" %>
<%@ page import="javax.xml.namespace.QName" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="org.apache.axis2.description.AxisModule" %>
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
<h1>Available Service Groups</h1>
<%
    Iterator axisServiceGroupIter = (Iterator) request.getSession().getAttribute(
            Constants.SERVICE_GROUP_MAP);
    request.getSession().setAttribute(Constants.SERVICE_GROUP_MAP,null);
    while (axisServiceGroupIter.hasNext()) {
        AxisServiceGroup axisServiceGroup = (AxisServiceGroup) axisServiceGroupIter.next();
        String groupName = axisServiceGroup.getServiceGroupName();
        Collection modules = axisServiceGroup.getEngagedModules();
        Iterator axisServiceIter = axisServiceGroup.getServices();
%>
<h2><%=groupName%></h2><ul>
    <%
        while (axisServiceIter.hasNext()){
            AxisService axisService = (AxisService) axisServiceIter.next();
            String serviceName = axisService.getName();
    %>
    <li><font color="blue"><a href="axis2-admin/ListSingleService?serviceName=<%=serviceName%>">
        <%=serviceName%></a></font></li>
    <%
        }
    %>
</ul>
<%
    if (modules.size() > 0) {
%>
<I>Engaged modules</I><ul>
    <%
        for (Iterator iterator = modules.iterator(); iterator.hasNext();) {
            AxisModule axisOperation = (AxisModule) iterator.next();
            String modulDesc = axisOperation.getName();
    %>
    <li><%=modulDesc%></li>
    <%
        }
    %></ul><%
        }
    }
%>
<jsp:include page="include/adminfooter.inc"/>
