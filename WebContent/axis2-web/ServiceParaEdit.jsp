<%@ page import="org.apache.axis2.Constants,
                 org.apache.axis2.description.AxisOperation,
                 org.apache.axis2.description.AxisService,
                 org.apache.axis2.description.Parameter,
                 java.util.ArrayList,
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
<h1>Edit Service Parameters</h1>
  <form method="get" name="editServicepara" action="axis2-admin/editServicepara">
   <%
            AxisService axisService = (AxisService)request.getSession().
                    getAttribute(Constants.SERVICE);
             if(axisService != null ){
   %>     <table width="100%">

            <tr>
                 <td colspan="2" ><b>
           <%
                 String servicName =  axisService.getName();
                 %>Service Parameters :: <%=servicName%>
                 </b></td>
             </tr>
             <tr>
             <td colspan="2" ><input style="display:none"  name="axisService" value="<%=servicName%>"></td>
            </tr>
             <%
                 ArrayList service_para = axisService.getParameters();
                 for (int i = 0; i < service_para.size(); i++) {
                     Parameter parameter = (Parameter) service_para.get(i);
                     if (parameter.getParameterType()==Parameter.OM_PARAMETER) {
                         continue;
                     }
                     %>
                     <tr>
                     <td><%=parameter.getName()%></td>
                     <td><input type="text" value="<%=parameter.getValue()%>"
                           name="<%=(servicName + "_" + parameter.getName())%>" size="50">
                           </td>
                     </tr>
                     <%
                 }
                Iterator operations =  axisService.getOperations();
                if(operations.hasNext()){
                    %>
                    <tr>
                      <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                      <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                    </tr>
                    <tr>
                       <td colspan="2" > <b>Operation Paramaters :: </b>
                       </td>
                    </tr>
                    <%
                }

                 ArrayList op_paras ;
                        operations = axisService.getOperations();
                 while (operations.hasNext()) {
                     AxisOperation axisOperation = (AxisOperation) operations.next();
                     String operationName = axisOperation.getName().getLocalPart();
                     %>
                     <tr>
                       <td colspan="2" > &nbsp;&nbsp;&nbsp;&nbsp;</td>
                     </tr>
                     <tr>
                       <td colspan="2" ><b>Operation : <%=operationName%></b></td>
                     </tr>
                    <%
                     op_paras = axisOperation.getParameters();
                     for (int i = 0; i < op_paras.size(); i++) {
                         Parameter parameter = (Parameter) op_paras.get(i);
                         if (parameter.getParameterType()==Parameter.OM_PARAMETER) {
                             continue;
                         }
                     %>
                     <tr>
                     <td><%=parameter.getName()%></td>
                     <td><input type="text" value="<%=parameter.getValue()%>"
                           name="<%=(operationName + "_" + parameter.getName())%>" size="50">
                           </td>
                     </tr>
                     <%
                  }
                 }
                 %>
                 <tr>
                    <td>&nbsp;</td>
                <td>
                     <input name="changePara" type="submit" value=" Change " >
               </td>
               </tr>
                 </table>
                 <%
             } else {
            	%>
            	<p><%=request.getAttribute("status")%></p>
            	<%
             }

       %>
       <form>
<jsp:include page="include/adminfooter.inc"></jsp:include>
