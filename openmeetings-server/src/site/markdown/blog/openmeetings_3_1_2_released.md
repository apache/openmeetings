<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: OpenMeetings 3.1.2 released
date: '2016-08-12T22:58:06+00:00'
permalink: openmeetings_3_1_2_released
-->

# OpenMeetings 3.1.2 released

Release v3.1.2 contains 55 enhancements and fixes and 1 patch for a security vulnerability.<br/>
Security Fix: CVE-2016-3089 - Apache OpenMeetings XSS in SWF panel<br/><br/>
Other security relevant updates:
<ul>
<li>XSS in Chat window leading to DOS</li>
<li>MD5 should not be used for password encryption</li>
<li>OpenMeetings is vulnerable to session fixation</li>
<li>Private recording files were available to all users</li>
</ul>
<br/>Additionally a signed Screen-Sharing application with a valid certificate from the Apache Foundation is available since this release. Please update to this release from any previous OpenMeetings release. A detailed documentation on how to migration from older versions is available on the project website see: <a href="http://openmeetings.apache.org/Upgrade.html" target="_BLANK">http://openmeetings.apache.org/Upgrade.html</a>.<br/>
<br/>
Other fixes in admin, localisation, installer, invitations, room etc.
<br/><br/>
For a complete list of changes, see: <a href="https://www.apache.org/dist/openmeetings/3.1.2/CHANGELOG" target="_BLANK">https://www.apache.org/dist/openmeetings/3.1.2/CHANGELOG</a>
<br/><br/>
Downloads and documentation is available from our project website: <br/><a href="http://openmeetings.apache.org/downloads.html" target="_blank">http://openmeetings.apache.org/downloads.html</a><br/>
<br/>
<b>Update (15/08/2015)</b><br/>
OpenMeetings modules are now also available individually as Maven dependencies, see: <a href="https://repository.apache.org/#nexus-search;quick~openmeetings" target="_BLANK">https://repository.apache.org/#nexus-search;quick~openmeetings</a><br/><br/>
For example:<br/>
&lt;dependency&gt;<br/>
  &lt;groupId&gt;org.apache.openmeetings&lt;/groupId&gt;<br/>
  &lt;artifactId&gt;openmeetings-db&lt;/artifactId&gt;<br/>
  &lt;version&gt;3.1.2&lt;/version&gt;<br/>
&lt;/dependency&gt;<br/>
<br/>
The sync to <a href=" http://repo1.maven.org/maven2/org/apache/" target="_BLANK">http://repo1.maven.org/maven2/org/apache/</a> should be available within the next 24hours.
