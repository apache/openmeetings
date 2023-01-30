<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: 'Google Summer of Code 2012 at Apache OpenMeetings '
date: '2012-09-11T07:57:16+00:00'
permalink: google_summer_of_code_2012
-->

# Google Summer of Code 2012 at Apache OpenMeetings

We were very happy to have 3 official students participating in the <a href="http://www.google-melange.com/gsoc/homepage/google/gsoc2012" target="_BLANK" rel="nofollow">Google Summer of Code 2012 program</a>! There was even a 4th student that worked on his proposal even not accepted officially as there have been not enough slots available. All students met their goals some even did more then required.<br/>
As OpenMeetings is about making virtual conferences we organized a weekly virtual meeting to catch up with everybody. It was sometimes difficult to attend for everybody as the students and mentors are situated in different timezones, starting with GMT-4 to GMT+7. But we had a chance to talk to everybody face to face several times during the summer!<br/>
<br/>
<a href="https://masterbranch.com/ggrekhov" target="_BLANK" rel="nofollow">German Grekov's</a> project was about implementing a Connection Test tool that can be run upfront a meeting to test upload, download and if required ports for OpenMeetings are open.<br/>
The final result was a UI that shows the most important information to the end-user<br/>

<img src="https://cwiki.apache.org/confluence/download/attachments/27845768/screenshot.png?version=1&amp;modificationDate=1341842721000" border="0" alt="Connection+testing+tool" name="Connection+testing+tool" />
<br/>
<br/>
The test performed by the tool can be configured using a XML configuration, so that a users can customize the ports if they configured OpenMeetings to run on different ones.<br/>
German has documented the tool in more details:<br/>
<a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Connection+testing+tool" target="_BLANK" rel="nofollow">https://cwiki.apache.org/confluence/display/OPENMEETINGS/Connection+testing+tool</a>
<br/>
<br/>
His second task was to integrate a volume slider in the video pods. With a volume slider each user can adjust the audio of each video separated. Conference participants always have different microphones (with a good or bad quality), therefore it is very useful thing to have the opportunity of adjusting of some settings at runtime.<br/>
The image shows the volume slider next on the top of the video view.<br/>
<img src="https://cwiki.apache.org/confluence/download/attachments/30147797/3_2.png?version=1&amp;modificationDate=1344960634000" /><br/>
<br/>
More information about the project can be found in the wiki documentation:<br/>
<a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Volume+slider" target="_BLANK" rel="nofollow">https://cwiki.apache.org/confluence/display/OPENMEETINGS/Volume+slider</a> <br/>
<br/>
<a href="http://twitter.com/brantner_ru" target="_BLANK" rel="nofollow">Dmitry Zamula</a> had the most difficult project agenda as his task required the most knowledge of various components of OpenMeetings. His goal was to create a component that would allow conversion of word and presentation documents into thumbnails and or html files. He decided to use <a href="http://poi.apache.org/" target="_BLANK" rel="nofollow">Apache POI</a> for the conversion. The final goal of the whole component to have one day editable documents in the whiteboard of OpenMeetings. His component is one step in that direction as it converts documents in the HTML format. Based on that potentially an editor could be created where you can edit the document online. While that task is quite difficult to integrate it was further difficult as he had not only to learn OpenMeetings component set but also the one of Apache POI.<br/>
The graph shows the process of generating html formatted content<br/>
<a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Posting+documents+on+the+whiteboard+with+POI" target="_BLANK" rel="nofollow">
<img src="https://cwiki.apache.org/confluence/download/attachments/30148643/om_seq.png?version=1&amp;modificationDate=1345797669000" width="700" height="300" /></a><br/>
<br/>
Finally he was able to create documents in HTML format. The solution was a very good start to explore the chances of collaborative editing of documents within OpenMeetings.<br/>
More info can be found on his wiki page: <a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Posting+documents+on+the+whiteboard+with+POI" target="_BLANK" rel="nofollow">https://cwiki.apache.org/confluence/display/OPENMEETINGS/Posting+documents+on+the+whiteboard+with+POI</a><br/>
<br/>
<a href="https://github.com/ankurankan" target="_BLANK" rel="nofollow">Ankur Ankan</a> did realize an integration of OpenMeetings in <a href="http://www.zimbra.com/" target="_BLANK" rel="nofollow">Zimbra</a>. Zimbra is an Exchange replacement for collaboration and communication. It provides a plugin loader system and the plugins are called “Zimlets”. Zimlets are basically JavaScript coded applications.<br/>
But a Zimlet can also perform REST calls. It does that by using the Zimbra server as Proxy server to prevent cross side scripting effects.<br/>
Ankur successfully tackled those hurdles, did get familiar with the Zimlet architecture and implemented his plugin step by step.<br/>
Besides his implementation he created some docs that also contain screenshots of the plugin
<a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/Openmeetings+Plugin+for+Zimbra" target="_BLANK" rel="nofollow">https://cwiki.apache.org/confluence/display/OPENMEETINGS/Openmeetings+Plugin+for+Zimbra</a>.
<a href="" target="_BLANK" rel="nofollow">
<img src="https://cwiki.apache.org/confluence/download/attachments/27845930/ANKUR-PC-2012-jul-20-003.jpg?version=1&amp;modificationDate=1342806332000" width="700" height="400" /></a><br/>
<br/>
What really is great that he did also complete all steps to finally create a working release from his project so that the distribution of the plugin can start and users can work with it!<br/>
<br/>
<a href="https://github.com/jdolitsky" target="_BLANK" rel="nofollow">Josh Dolitsky</a> did not officially work as a GSoC student because slots were already full, but he agreed to work on the project even if not accepted! His task was to update and implement the ATutor Plugin. <a href="http://atutor.ca/" target="_BLANK" rel="nofollow">ATutor</a> is an ELearning suite from OCAD (<a href="http://www.ocadu.ca/" target="_BLANK" rel="nofollow">www.ocad.ca</a>  Canada). The plugin needed to be updated to use the latest REST API calls of OpenMeetings. Also, it needed to be refactored to remove any non-Apache License compatible dependencies.<br/>
<br/>
This is a screenshot of the Plugin in action:<br/>
<a href="" target="_BLANK" rel="nofollow"><img src="https://cwiki.apache.org/confluence/download/attachments/27851094/atutor_screen.png?version=1&amp;modificationDate=1347349812784" width="700" height="300" /></a><br/>
<br/>
The plugin is now almost ready to start a vote to release it! He did also prepare some wiki page about his results: <br/>
<a href="https://cwiki.apache.org/confluence/display/OPENMEETINGS/ATutor+plug-in+installation+and+usage" target="_BLANK" rel="nofollow">https://cwiki.apache.org/confluence/display/OPENMEETINGS/ATutor+plug-in+installation+and+usage</a>
