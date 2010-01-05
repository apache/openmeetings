<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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
  <head>
    <jsp:include page="include/httpbase.jsp"/>
    <title>Axis 2 - Home</title>
    <link href="axis2-web/css/axis-style.css" rel="stylesheet" type="text/css"/>
  </head>

  <body>
    <jsp:include page="include/header.inc" />
    <br/>

    <h1>Welcome!</h1>
    Welcome to the new generation of Axis. If you can see this page you have
    successfully deployed the Axis2 Web Application. However, to ensure that Axis2
    is properly working, we encourage you to click on the validate link.
    <br/>
    <ul>
      <li><a href="services/listServices">Services</a>
        <br/>
        View the list of all the available services deployed in this server.
      </li>
      <li><a href="axis2-web/HappyAxis.jsp">Validate</a>
        <br/>
        Check the system to see whether all the required libraries are in place
        and view the system information.
      </li>
      <%--<li><a href="interop.jsp">Do an interop test</a>
      <br/>
      You can run Interoperability tests here
      </li>--%>
      <li><a href="axis2-admin/">Administration</a>
        <br/>
        Console for administering this Axis2 installation.
      </li>
    </ul>
    <jsp:include page="include/footer.inc"/>
  </body>
</html>
