<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: Apache Openmeetings 2.1.1 released
date: '2013-07-12T07:42:00+00:00'
permalink: apache_openmeetings_2_1_11
-->

# Apache Openmeetings 2.1.1 released

The Apache Openmeetings PMC is proud to announce Apache Openmeetings 2.1.1!<br/>
<br/>
This is a bug fix release for the 2.1.x tree and the second release as Top Level Apache Project!<br/>
<br/>
Please download and build the distribution yourself, or use our convenience binary package<br/>
<br/>
<ul>
	<li>Sources: <a href="http://www.apache.org/dyn/closer.cgi/openmeetings/2.1.1/src" target="_blank">http://www.apache.org/dyn/closer.cgi/openmeetings/2.1.1/src</a></li>
	<li>Binaries: <a href="http://www.apache.org/dyn/closer.cgi/openmeetings/2.1.1/bin" target="_black">http://www.apache.org/dyn/closer.cgi/openmeetings/2.1.1/bin</a></li>
</ul>
<br/>
Or in general see: <a href="http://openmeetings.apache.org/downloads.html">http://openmeetings.apache.org/downloads.html</a>
<br/>
<br/>
<b>Release Notes 2.1.1</b><br/>
<br/>
Summary: Bug fix release for 2.1.0.<br/>
This release includes some improvements in user interface, fixes for the
interview and video only rooms, and localization issues.
Configurable hot key for Mute/Unmute have been added.<br/>
<br/>
<br/>
<h3>Known Issues</h3>
<ul>
<li>Note: Video/audio SIP integration is not a part of the Apache project, check the red5sip project for this integration.</li>
<li>Issues with Recordigs that are longer then 1 hour under some circumstances https://issues.apache.org/jira/browse/OPENMEETINGS-669</li>
<li>Some issue reported with recent version of Adobe Flash Plugin https://bugzilla.mozilla.org/show_bug.cgi?id=885188</li>
<li>Multi-tabbing issues with Flash Player on Linux</li>
</ul>
<h3>        Bugs solved in that release
</h3>
<ul>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-218'>OPENMEETINGS-218</a>] -         Microphone doesn&#39;t work after clicking «unmute microphone» button
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-405'>OPENMEETINGS-405</a>] -         microphoneRateNormal &amp; microphoneRateBest - not change rate
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-432'>OPENMEETINGS-432</a>] -         video only room is coming with white board also
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-439'>OPENMEETINGS-439</a>] -         An LDAP user can&#39;t change their own profile picture.
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-481'>OPENMEETINGS-481</a>] -         When I reserve a room by sending an email, users who connected to this link from an email have the same email address as me
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-486'>OPENMEETINGS-486</a>] -         Sometimes OM stops responding to clicks
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-503'>OPENMEETINGS-503</a>] -         Openmeetings does not logout on the Recording panel
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-507'>OPENMEETINGS-507</a>] -         Error message is shown when user save the Profile settings
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-537'>OPENMEETINGS-537</a>] -         Pop menu  in conference for files shows &quot;Delete folder&quot;
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-544'>OPENMEETINGS-544</a>] -         some bug with SIP
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-579'>OPENMEETINGS-579</a>] -         In any room not close the video and audio settings windows. Video is not broadcasting.
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-586'>OPENMEETINGS-586</a>] -         FileItem owner_id is not replaced with new id while system import
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-587'>OPENMEETINGS-587</a>] -         Exclusive audio by hotkey is broken
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-598'>OPENMEETINGS-598</a>] -         dont build trunk on JDK 1.7 x64
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-608'>OPENMEETINGS-608</a>] -         Office file are uploaded but not displayed
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-609'>OPENMEETINGS-609</a>] -         The end time shown under the Book Conference Room option in New Message is Incorrect
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-618'>OPENMEETINGS-618</a>] -         Incorrect translation
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-625'>OPENMEETINGS-625</a>] -         Profile Pictures not working on LDAP Accounts
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-634'>OPENMEETINGS-634</a>] -         No menu accessable after file upload, in a special condition
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-635'>OPENMEETINGS-635</a>] -         Administration / Configuration : default_lang_id documentation
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-654'>OPENMEETINGS-654</a>] -         Interview room is broken
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-670'>OPENMEETINGS-670</a>] -         The webinar is already closed, you wil be rediredcted to some interesting offerings in X sek
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-672'>OPENMEETINGS-672</a>] -         in recordings page ,people cant logout
</li>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-687'>OPENMEETINGS-687</a>] -         Microphone is unmuted when user refresh video frame
</li>
</ul>
<h3>        Improvement
</h3>
<ul>
<li>[<a href='https://issues.apache.org/jira/browse/OPENMEETINGS-589'>OPENMEETINGS-589</a>] -         Configurable hot key for Mute/Unmute should be added
</li>
</ul>
