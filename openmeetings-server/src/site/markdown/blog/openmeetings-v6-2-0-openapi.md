<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: OpenMeetings v6.2.0 - OpenAPI Swagger API, Node, PHP integration and Mobile version
date: '2021-10-25T20:55:06+00:00'
permalink: openmeetings-v6-2-0-openapi
-->

# OpenMeetings v6.2.0 - OpenAPI Swagger API, Node, PHP integration and Mobile version

<h2>Mobile conference room improvements</h2>
OpenMeetings v6.2.0 improves the Mobile version of OpenMeetings: UI improvements around viewport and the ability to save OpenMeetings to the Mobile device home screen so it can be started similar to a native mobile app:<br/><br/>
<b>(1) Add to Home Screen  / (2) Home screen icon / (3) iOS in landscape mode</b><br/>
<img src="https://blogs.apache.org/openmeetings/mediaresource/541df6d3-f069-4646-9cad-b2bded861e67" alt="IMG_0349.jpeg" style="width:200px;height:215px;"/>
<img src="https://blogs.apache.org/openmeetings/mediaresource/7a6aaeb8-d67e-4a04-9fbb-5137a6e59b0a" alt="IMG_0350.jpeg" style="width:200px;height:231px;"/>
<img src="https://blogs.apache.org/openmeetings/mediaresource/2dd64082-9061-402d-9aba-1e18b51ac5ef" alt="Screenshot_ios.png" style="width:400px;height:225px;"/>
<br/><br/>
The Mobile version of OpenMeetings uses only HTML5/webRTC and requires iOS/Safari v15.x (current version) in order to share webcam and microphone successfully. Android/Chrome works fine since earlier versions (v81), however best practise is to keep your browser up to date (as of this moment current is v95 for Chrome/Android). More detailed screenshots and description are at <a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/OpenMeetings+Mobile" target="_blank">OpenMeetings Mobile wiki</a>
<br/><br/>
<h2>OpenAPI v3 integration API and Node/NPM + PHP/Composer client modules</h2>
<img src="https://blogs.apache.org/openmeetings/mediaresource/8b0b34c2-2bc6-4174-beea-a95038ca72ef?t=true" alt="OpenAPI_Logo_Pantone-1.png" />
<img src="https://blogs.apache.org/openmeetings/mediaresource/f70911d1-dfab-4903-8a66-e84273642a4c?t=true" alt="nodejs-new-pantone-black-small-removebg-preview.png" style="width:65px;height:40px;"/>
<img src="https://blogs.apache.org/openmeetings/mediaresource/bbe3ee60-ef4f-40cd-83c5-980bce89464b" alt="new-php-logo.png" style="width:68px;height:36px;"/>
<br/>
Additionally v6.2.0 of OpenMeetings introduces an OpenAPI v3 Rest API spec published in swagger format: <b>
<a href="https://openmeetings.apache.org/swagger/" target="_blank">https://openmeetings.apache.org/swagger/</a></b>.
<br/><br/>
Examples of the integration are also updated at the <a href="https://openmeetings.apache.org/RestAPISample.html" target="_blank">RestAPISamples</a>. Community contributed modules for using this API include for example:
<ul>
<li><a href="https://openmeetings.apache.org/RestAPISample.html#how-to-integrate-using-nodejs" target="_blank">Node.js module for using API</a></li>
<li><a href="https://openmeetings.apache.org/RestAPISample.html#how-to-integrate-using-php-and-composer" target="_blank">PHP composer module for using API</a></li>
</ul>
Via the integration API it enables integrating OpenMeetings conference rooms into any Node or PHP based website or API. Other community plugins using this API for Moodle, SugarCRM, Drupal, Joomla can be found in the Configuration>Plugins section at <a href="https://openmeetings.apache.org" target="_blank">https://openmeetings.apache.org</a><br/>
<br/>
<h2>Other issues fixed in v6.2.0 and downloads</h2>
<b>Summary:</b> UI improvements, stability fixes, mobile version and adds OpenAPI spec in 3.0.x format.<br/>
<br/>
<b>Stability and UI:</b>
<ul>
<li>UI fixes</li>
<li>Modal PopUpFix</li>
<li>Upgrade to Bootstrap5</li>
<li>Fixes for Mobile Version and landscape mode</li>
<li>Improve ability for starting from Home Screen on Mobile device</li>
</ul>
<b>Integration:</b>
<ul>
<li>OpenAPI Spec in swagger format see <a href="https://openmeetings.apache.org/swagger/" target="_blank">https://openmeetings.apache.org/swagger/</a></li>
<li>Improved Integration examples for Node and PHP</li>
</ul>

Some other fixes and improvements, 28 issues were addressed.<br/>
Full ChangeLog is available from <a href="https://github.com/apache/openmeetings/blob/6.2.0/CHANGELOG.md">ChangeLog on Github</a>
 <br/><br/>
Downloads of v6.2.0 are available from <a href="https://openmeetings.apache.org/downloads.html" target="_BLANK">openmeetings.apache.org/downloads.html</a>
 <br/> <br/>
