<%@ page import="org.apache.axis2.Constants"%>
<%@ page import="org.apache.axis2.context.ConfigurationContext"%>
<%@ page import="org.apache.axis2.context.ServiceGroupContext"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map"%>
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
<h1>Runing Context hierachy</h1>
<%
    String type = (String) request.getSession().getAttribute("TYPE");
    ConfigurationContext configCtx = (ConfigurationContext) request.getSession().getAttribute("ConfigurationContext");
    ServiceGroupContext sgContext = (ServiceGroupContext) request.getSession().getAttribute("ServiceGroupContext");
    if (sgContext != null) {
        if (type != null) {
            if ("VIEW".equals(type)) {
              Iterator propertyNames = sgContext.getPropertyNames();
%>
             <h4>Persistance properties</h4><ul>
             <%
                 while (propertyNames.hasNext()) {
                     String key = (String) propertyNames.next();
                     Object property =  sgContext.getProperty(key);
              %>
                   <li><%=key%> : <%=property.toString()%></li>
              <%
                 }
                 %></ul>
                 <%
            }   else if("DELETE".equals(type)){
                 configCtx.removeServiceGroupContext(sgContext.getId());
                 %>Removed the context<%
            }
        }
    } else {
%> <h4>No service group context found</h4><%
    }
%>
<jsp:include page="include/adminfooter.inc"></jsp:include>