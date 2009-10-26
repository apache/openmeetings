<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisOperation"%>
<%@ page import="org.apache.axis2.description.AxisService"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.apache.axis2.transport.http.AxisServlet"%>
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
<html>
  <head>
    <jsp:include page="include/httpbase.jsp"/>
    <title>List Single service</title>
    <link href="axis2-web/css/axis-style.css" rel="stylesheet" type="text/css">
  </head>
  <body>
  <jsp:include page="include/header.inc"></jsp:include>
    <jsp:include page="include/link-footer.jsp"></jsp:include>
  <%
        String prifix = request.getAttribute("frontendHostUrl") + (String)request.getSession().getAttribute(Constants.SERVICE_PATH) +"services/";
    %>
        <%
            String isFault = (String)request.getSession().getAttribute(Constants.IS_FAULTY);
            String servicName = request.getParameter("serviceName");
            if(Constants.IS_FAULTY.equals(isFault)){
                Hashtable errornessservices =(Hashtable)request.getSession().getAttribute(Constants.ERROR_SERVICE_MAP);
                %>
                    <h3>This Web axisService has deployment faults</h3><%
                     %><font color="red" ><%=(String)errornessservices.get(servicName) %></font>
                <%

                    }else {

                    AxisService axisService =
                            (AxisService) request.getSession().getAttribute(Constants.SINGLE_SERVICE);
                    if(axisService!=null){
           Iterator opItr = axisService.getOperations();
            //operationsList = operations.values();
          String  serviceName = axisService.getName();
            %><h2><font color="blue"><a href="<%=prifix + axisService.getName()%>?wsdl"><%=serviceName%></a></font></h2>
           <font color="blue">Service EPR : </font><font color="black"><%=prifix + axisService.getName()%></font><br>
           <h4>Service Description : <font color="black"><%=axisService.getServiceDescription()%></h4>
           <i><font color="blue">Service Status : <%=axisService.isActive()?"Active":"InActive"%></font></i><br>
           <%
            if (opItr.hasNext()) {
                %><i>Available operations</i><%
            } else {
                %><i> There are no Operations specified</i><%
            }
               opItr = axisService.getOperations();
           %><ul><%
            while (opItr.hasNext()) {
                AxisOperation axisOperation = (AxisOperation) opItr.next();
                %><li><%=axisOperation.getName().getLocalPart()%></li>
                <%
            }
           %></ul>
           <%
                    } else{
                           %>
                <h3><font color="red" >No services found in this location.</font></h3>
 <%
                    }

            }
        %>
<jsp:include page="include/footer.inc"></jsp:include>
        </body>
</html>
