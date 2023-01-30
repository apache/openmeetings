<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: OpenMeetings 5.0.0 release - Migration to HTML5 webRTC and Wicket 9.0.0
date: '2020-08-28T04:46:46+00:00'
permalink: openmeetings-5-0-0-release
-->

# OpenMeetings 5.0.0 release - Migration to HTML5 webRTC and Wicket 9.0.0

OpenMeetings v5.0.0 is the first main release of a HTML5 version of the Web-Conferencing software. All features are using native browser components. There are no plugins required and you can share your webcam and microphone as well as your screen just using HTML5 and JavaScript!
 <br/>
 <br/>
<b>Main new features in 5.0.0:</b>
<ul>
<li>Mic/Webcam sharing and viewing in HTML5</li>
<li>Screen sharing and conference room recording in HTML5</li>
<li>Playback of videos and recordings HTML5</li>
<li>Whiteboard in HTML5</li>
<li>Support for mobile and tablet clients using HTML5 enabled browsers</li>
</ul>
The migration to HTML5 took 2-3 years and was done in several iterations of partially replacing elements with HTML5. There have been 4 beta releases of v5.0.0 and 200++ Jira tickets as part of version 5, probably 1000++ in the overall migration.
<br/><br/>
<b>Using latest release Apache Wicket 9.0.0</b>
 <br/> <br/>
OpenMeetings uses the latest release of Apache Wicket 9.0.0, especially it uses features around:
<ul>
<li>Real-Time messaging using WebSockets</li>
<li>CSP Policy - Mic/Webcam and Screen access require more strict and reworked browser Content Security Policy (CSP) set via Wicket components</li>
<li>Wicket components for styling and forms for Admin functionality (User and Room management)</li>
</ul>
<b>Security in Web-Conferencing and HTML5</b>
 <br/> <br/>
In order to enable mic/webcam and screen access in HTML5 OpenMeetings needs to follow a strict browser security model as required by browsers and webRTC standards (see <a href="https://webrtc-security.github.io/" target="_BLANK">https://webrtc-security.github.io/</a>). This includes implementing HTTPS for all connections and also some browser specific features. <br/>
Overview about security and session initialisation at: <a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/OpenMeetings+HTML5+Session+Initialisation+and+Security" target="_BLANK">OpenMeetings+HTML5+Session+Initialisation+and+Security</a>
 <br/> <br/>
<b>Downloads for v5.0.0 are available at <a href="https://openmeetings.apache.org/downloads.html" target="_BLANK">openmeetings.apache.org/downloads.html</a></b>
 <br/> <br/>
