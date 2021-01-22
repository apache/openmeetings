<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

#How to restrict access to your Openmeetings server

## Server Side
You can protect your OpenMeetings instance from beeing accessed from 3th party by setting up `RemoteAddrValve`

Here `$OM_HOME/conf/server.xml` (will affect the whole Tomcat)

Or here `$OM_HOME/webapps/openmeetings/META-INF/context.xml`

Please check Tomcat documentation for more details <a href="https://tomcat.apache.org/tomcat-9.0-doc/config/context.html">Docs about context</a>, <a href="https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Remote_Address_Valve">Docs about RemoteAddrValve</a>

For example: To allow access only for the clients connecting from localhost:

```
<Valve className="org.apache.catalina.valves.RemoteAddrValve" allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1"/>
```
