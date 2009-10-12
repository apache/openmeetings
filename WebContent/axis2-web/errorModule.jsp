<%@ page import="org.apache.axis2.Constants,
                 java.util.Hashtable"%>
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="include/adminheader.jsp"></jsp:include>
  <%
      Hashtable errornessModules =(Hashtable)request.getSession().getAttribute(Constants.ERROR_MODULE_MAP);
      String moduleName = request.getParameter("moduleName");
  %> <h3>The Module has deployment faults</h3><%
  %><font color="red" ><%=(String)errornessModules.get(moduleName) %></font><%
  %>
<jsp:include page="include/adminfooter.inc"></jsp:include>