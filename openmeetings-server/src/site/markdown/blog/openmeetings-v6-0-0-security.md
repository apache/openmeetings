<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: OpenMeetings v6.0.0 - Security and Performance, webRTC Stability and UI improvements
date: '2021-03-14T19:36:46+00:00'
permalink: openmeetings-v6-0-0-security
-->

# OpenMeetings v6.0.0 - Security and Performance, webRTC Stability and UI improvements

OpenMeetings v6.0.0 is the second major release since migrating all audio & video components to webRTC and HTML5. There are a number of fixes and improvements in Security as well as webRTC handling as part of this release.
 <br/>
 <br/>
<b>New features available in v6.0.0:</b>
 <br/>
 <br/>
<b>Performance metrics using Prometheus and load tests</b><br/>
v6.0.0 introduces the ability to configure <a href="https://prometheus.io/" target="_BLANK">Prometheus metrics</a> to be generated. The metrics are disabled by default. There is detailed documentation on how to configure and enable the export and what type of metrics are available - <a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Prometheus+Logging+and+Metrics" target="_BLANK">Prometheus+Logging+and+Metrics</a>.
You can find example for load tests and performance metrics using above statistics - <a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Performance+Testing" target="_BLANK">Performance+Testing</a>
<br/>
 <br/>
<b>UI partially migrated to build using NPM and dependency management</b><br/>
Most of the UI code relevant for the conference room has been migrated to be built using NPM and split into multiple partials that built separated. This enables using NPM and dependency management as well as a way to develop JavaScript which is more familiar for a lot of Front End Developers!
<br/>
 <br/>
<b>And there is a long list of improvements in v6.0.0:</b>
 <br/>
 <br/>
<b>Security</b>
<ul>
<li>TLS1.2. is used for OAuth</li>
<li>NetTest client count can be limited</li>
<li>Captcha is now configurable</li>
<li>Recordings can be globally disabled</li>
</ul>
<b>Stability</b>
<ul>
<li>Audio/video in room is more stable</li>
</ul>
<b>UI</b>
<ul>
<li>Translations are improved</li>
<li>Invitation form displays time in client time zone</li>
<li>Notifications are displayed using JS Notification API</li>
<li>Video pods size can be fixed and configurable per-user</li>
</ul>
 <br/>
Full ChangeLog is available from <a href="https://github.com/apache/openmeetings/blob/6.0.0/CHANGELOG.md">Github</a>,
downloads of v6.0.0 are available from <a href="https://openmeetings.apache.org/downloads.html" target="_BLANK">openmeetings.apache.org/downloads.html</a>
 <br/> <br/>
