<!--=======================================================================-->
<!-- shutdown.jsp                                                          -->
<!--                                                                       -->
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!--=======================================================================-->

<%@page import="java.net.InetAddress" %>
<%@page import="java.net.UnknownHostException" %>
<%@page import="java.io.PrintWriter" %>

<%
    String ip = request.getRemoteAddr();

    InetAddress   localhost = null;
    InetAddress[] myIPs;

    boolean isValidIP = false;

    // Store my ips
    try {
        localhost= InetAddress.getLocalHost();
        if (localhost != null) {
            if (ip.equals(localhost.getHostAddress()))
                isValidIP = true;
        }
    } catch (UnknownHostException e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           "Can't resolve server's localhost.");
        return;
    }

    try {
        myIPs = InetAddress.getAllByName("localhost");
        for(int i = 0; i < myIPs.length; i++) {
            if (ip.equals(myIPs[i].getHostAddress())) {
                isValidIP = true;
                break;
            }
        }
    } catch (UnknownHostException e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           "Can't resolve server's address.");
        return;
    }

    if (! isValidIP) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                           "The server can't be shutdown from a remote machine.");
        return;
    }
%>
<HTML>
<HEAD>
<TITLE>OpenLaszlo Server: Stopped</TITLE>
</HEAD>
<BODY bgcolor="#677080">
<CENTER>
<OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
 codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,79,0"
 WIDTH=800 HEIGHT=600>
 <PARAM NAME=movie VALUE="server_stopped.swf">
 <PARAM NAME=quality VALUE=high>
 <PARAM NAME=bgcolor VALUE=#677080>
 <EMBED src="server_stopped.swf" quality=high bgcolor=#677080  WIDTH=800 HEIGHT=600
   TYPE="application/x-shockwave-flash" 
   PLUGINSPAGE="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash">
 </EMBED>
</OBJECT>
</CENTER>
</BODY>
</HTML>
<%
  String catalinaHome = System.getProperty("catalina.home");
  String os = System.getProperty("os.name"); 
  if (os.startsWith("Windows")) {
    String[] stop = { catalinaHome + "/bin/shutdown.bat" };
    Runtime.getRuntime().exec(stop);
  } else { // assume Mac/Linux 
    String[] stop = {"/bin/bash", "-c", catalinaHome + "/bin/shutdown.sh"};
    Runtime.getRuntime().exec(stop);
  }
%>
