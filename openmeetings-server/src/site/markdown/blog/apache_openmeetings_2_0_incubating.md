<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: Apache OpenMeetings 2.0 Incubating released
date: '2012-07-26T09:18:41+00:00'
permalink: apache_openmeetings_2_0_incubating
-->

# Apache OpenMeetings 2.0 Incubating released

### We are happy to announce Version 2.0 of Apache OpenMeetings Incubating!

<br/>

<span>This is our first release as Apache project and there have been major changes almost everywhere in the application.</span><br/>
<br/>
<span><strong>Restyled UI</strong></span>
<span >The
 UI has been refactored to have a common look and feel. Additionally
most of the icons are now loaded at runtime. That means you can change
colors and icons at runtime without changing the source code. More info:
 </span>
 <a href="http://incubator.apache.org/openmeetings/themes-and-branding.html" target="_blank">http://incubator.apache.org/openmeetings/themes-and-branding.html</a>
<br/>
<br/>
<span><strong>New Calendar</strong></span>
<span >The
 calendar was completely refactored and got a new UI that was built from
 scratch that also adds some new functions. For example it is possible
now to password protect invitations sent via the calendar. Further the
timezone handling has been refactored and there is now also a SOAP/REST
API to handle calendar Events ( </span>
<a href="http://incubator.apache.org/openmeetings/SoapRestAPI.html" target="_blank">http://incubator.apache.org/openmeetings/SoapRestAPI.html</a>
<span > )</span><br/>
<br/>
<span><strong>Integration with Asterisk</strong></span>
<span >The
 application contains now modules to directly integrate OpenMeetings
with Asterisk for SIP/VoIP integration. It enables you to dial in as
well as dial out of conference room to SIP or ordinary phones. More
info: </span>
<a href="http://incubator.apache.org/openmeetings/voip-sip-integration.html" target="_blank">http://incubator.apache.org/openmeetings/voip-sip-integration.html</a>
<br/>
<br/>
<span><strong>Install/Backup/Import via command line</strong></span>
<span >Additionally
 to the Web-Interface you can now do all basic operations via console. Just cd to your OpenMeetings installation directory and type
“admin” (or ./admin.sh) and you will see all the options available (OpenMeetings service
 should be shut down while doing those operations)</span><br/>
<br/>
<span><strong>Using SWF10 for Video Components</strong></span>
<span >All
 audio/video related components now use SWF10 for broadcasting and
receiving audio and video signals. That makes it possible to use for
example the echo cancellation feature build in the SWF10 Flash Player.</span><br/>
<br/>
<span >There
 are a lot more improvements for example to recording, screen sharing
and new layout options. To see the full list please review our Release
Notes for Version 2.0:</span><br/>
<a href="https://issues.apache.org/jira/secure/ReleaseNote.jspa?projectId=12312720&amp;version=12319197" target="_blank">https://issues.apache.org/jira/secure/ReleaseNote.jspa?projectId=12312720&amp;version=12319197</a>
<br/>
<br/>
<span><strong>There are also new Integration Plugins in the pipe!</strong></span><br/>
<br/>
<span><strong>Integration into Atlassian Jira</strong></span>
<span >There is a plugin in our SVN that will be released soon for integration with Atlassian Jira, you can watch a demo here: </span>
<a href="https://blogs.apache.org/openmeetings/entry/jira_integration_apache_openmeetings_demo" target="_blank">https://blogs.apache.org/openmeetings/entry/jira_integration_apache_openmeetings_demo</a>
<br/>
<br/>
<span><strong>Integration into Atlassian Confluence</strong></span>
<span > Same for Atlassian Confluence Wiki, you can watch a demo here: </span>
<a href="https://blogs.apache.org/openmeetings/entry/demo_video_about_upcoming_atlassian" target="_blank">https://blogs.apache.org/openmeetings/entry/demo_video_about_upcoming_atlassian</a>
<br/>
<br/>
<span><strong>Important Changes</strong></span>
<span >OpenOffice
 service does not need to be running as permanent service. But you have to set the
path to OpenOffice (or LibreOffice) and JODConverter tools in
OpenMeetings configuration. OpenMeetings (and JODConverter) will start
and stop the OpenOffice service when they need it.</span><br/>
<br/>
<span><strong>Upgrading from Version 1.9 or prior</strong></span>
<span >
 To update from an old version of OpenMeetings to 2.x you should use the
 integrated Backup and Import tool that exists since around Version 1.3.
 You should follow our documentation, see: </span>
 <a href="http://incubator.apache.org/openmeetings/Upgrade.html" target="_blank">http://incubator.apache.org/openmeetings/Upgrade.html</a>
<br clear="all"/>
<br/>
<b>Downloads</b> of sources and binaries are available from the mirrors linked here:<br/>
<a href="http://incubator.apache.org/openmeetings/downloads.html" target="_blank">http://incubator.apache.org/openmeetings/downloads.html</a><br/>
<br/>
All downloads can be verified using the Apache OpenMeetings code signing <a href="http://www.apache.org/dist/incubator/openmeetings/KEYS" target="_blank">KEYS</a>
