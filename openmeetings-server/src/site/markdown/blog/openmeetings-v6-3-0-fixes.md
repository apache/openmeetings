<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: OpenMeetings v6.3.0 - Fixes and maintenance release including preparation for JDK17
date: '2022-05-19T05:44:35+00:00'
permalink: openmeetings-v6-3-0-fixes
-->

# OpenMeetings v6.3.0 - Fixes and maintenance release including preparation for JDK17

The new version 6.3.0 includes a number of fixes, library updates and prepares for moving OpenMeetings to use JDK17. Currently it still compiles the server to be JRE 11 backwards compatible but you can obviously use JRE17 to run. But in future versions JRE17 will become mandatory. As well as Safari keeps on being an ongoing challenge with HTML5 and webRTC. Latest version 6.3.0 should provide some fixes.
 <br/>
 <br/>
<b>Stability and UI improvements</b>
<ul>
<li>Multiple Whiteboard fixes</li>
<li>Confirm popups are unified</li>
<li>Multiple fixes for latest Safari</li>
</ul>
<b>Other updates</b>
<ul>
<li>Libraries are updated with most recent versions (e.g. logging)</li>
</ul>
Notes re library updates: OpenMeetings was not affected by last years log4j issues since we are not using it. However latest v6.3.0 is updating
dependencies that include references to log4j and upgrades those.
<br/>  <br/>
Full ChangeLog is available from <a href="https://github.com/apache/openmeetings/blob/6.3.0/CHANGELOG.md">ChangeLog on Github</a>
 <br/><br/>
Downloads of v6.3.0 are available from <a href="https://openmeetings.apache.org/downloads.html" target="_BLANK">openmeetings.apache.org/downloads.html</a>
 <br/> <br/>
