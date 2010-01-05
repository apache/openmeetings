<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisService,
                 java.util.Collection,
                 java.util.HashMap,
                 java.util.Iterator"%>
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
<h1>Turn On Service</h1>
<form method="get" name="serviceActivate" action="axis2-admin/activateService">
  <table width="100%"  border="0">
<tr>
  <td colspan="2" >
     <p>The services that are inactive are listed below. Although you can activate the services from this page, once system is restarted the services will be inactive again</p>
  </td>
  </tr>
  <tr>
  <%
HashMap services = (HashMap)request.getSession().getAttribute(Constants.SERVICE_MAP);
Collection col = services.values();
String html = "";
int count = 0;

for (Iterator iterator = col.iterator(); iterator.hasNext();) {
	AxisService axisServices = (AxisService) iterator.next();
	if(!axisServices.isActive()){
		count++;
		html += "<option value='" + axisServices.getName() + "'>";
		html += axisServices.getName() + "</option>";
	}
}
request.getSession().setAttribute(Constants.SERVICE_MAP,null);
if (count > 0) {
%>
  
    <td width="20%"> Select Service : </td>
    <td width="80%">
       <select name="axisService" class="selectBoxes">
		<%=html%>
		</select>
  </tr>
  <tr>
    <td width="20%">Activate Service </td>
    <td width="80%"><input type="checkbox" name="turnon">
    </td>
  </tr>
  <tr>
  <td>&nbsp;</td>
  <td>
    <input name="submit" type="submit" value=" Activate " >
   <input name="reset" type="reset" value=" Clear " >
  </td>
<%
} else {
	%>
	<td colspan="2">No inactive services present.</td>
	<%
}
%>
  </tr>

</table>
</form>
<jsp:include page="include/adminfooter.inc"></jsp:include>
