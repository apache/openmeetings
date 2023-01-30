<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: Apache OpenMeetings 3.0.3 announced
date: '2014-09-08T08:15:55+00:00'
permalink: apache_openmeetings_3_0_3
-->

# Apache OpenMeetings 3.0.3 announced

The OpenMeetings PMC announced the 3rd minor release of the 3.0 version of the Web-Conferencing application. Apache OpenMeetings 3.0.3  addresses 50+ issues. One main focus was resolving stability issues of the screen recording feature. It is now a requirement for the (Apache Tomcat) server, as well as for clients that want to use the screen sharing capabilities to have Java7 installed. Further bugs have been fixed around OAuth, LDAP integration and the command line installer, ( <a href="https://www.apache.org/dist/openmeetings/3.0.3/CHANGELOG" target="_BLANK">see full Changelog</a> ).<br/>
<br/>
Latest versions to <a href="http://openmeetings.apache.org/downloads.html" target="_BLANK">download</a>:
<ul>

<li>
								<a class="externalLink" href="http://www.apache.org/dyn/closer.cgi/openmeetings/3.0.3/bin/apache-openmeetings-3.0.3.zip">apache-openmeetings-3.0.3.zip</a>
								<a class="externalLink" href="http://www.apache.org/dist/openmeetings/3.0.3/bin/apache-openmeetings-3.0.3.zip.asc">[SIG]</a>
								<a class="externalLink" href="http://www.apache.org/dist/openmeetings/3.0.3/bin/apache-openmeetings-3.0.3.zip.md5">[MD5]</a>
							</li>

<li>
								<a class="externalLink" href="http://www.apache.org/dyn/closer.cgi/openmeetings/3.0.3/bin/apache-openmeetings-3.0.3.tar.gz">apache-openmeetings-3.0.3.tar.gz</a>
								<a class="externalLink" href="http://www.apache.org/dist/openmeetings/3.0.3/bin/apache-openmeetings-3.0.3.tar.gz.asc">[SIG]</a>
								<a class="externalLink" href="http://www.apache.org/dist/openmeetings/3.0.3/bin/apache-openmeetings-3.0.3.tar.gz.md5">[MD5]</a>
							</li>
						</ul>
<br/>
Exciting new feature of version 3 of OpenMeetings is the usage of Apache Wicket for the UI. Since version 3.0.0 OpenMeetings is a HTML5 and Flash hybrid. Some of the real time communication has been migrated to HTML5. But most of the real-time communication remains to be handled in RTMP (Flash Streaming). The next version 3.1 reduces the usage of Flash further to only the video streaming components using Apache Flex. <br/>
<br/>
Further the project is using now a Modular Maven build process including the generating of the <a href="http://openmeetings.apache.org/" target="_blank">project website</a>.

