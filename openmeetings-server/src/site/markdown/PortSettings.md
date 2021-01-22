<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->

# Port settings

## Default Configuration
- Port 5443: HTTPS (For web interface)
- Port 5080: HTTP (For unsecured web interface, useful if SSL proxy is being used)

## Configure alternative ports

You need to change `$OM_HOME/conf/server.xml` file, OpenMeetings server need to be restarted so that changes are online.

## Preventing Firewall issues

A common way of bypassing the firewall is to change HTTP port to 80

## OpenMeetings over SSL

You can run OpenMeetings completely over SSL. See <a href="HTTPS.html">HTTPS guide</a>.
