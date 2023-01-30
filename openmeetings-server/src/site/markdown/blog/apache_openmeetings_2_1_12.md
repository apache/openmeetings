<!--
Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0
-->
<!---
layout: post
title: Apache OpenMeetings 2.1.1 in Azure VMDepot
date: '2013-10-05T04:38:59+00:00'
permalink: apache_openmeetings_2_1_12
-->

# Apache OpenMeetings 2.1.1 in Azure VMDepot

<p>Azure is Microsofts Cloud based service, similar to Amazon or the great <a href="http://cloudstack.apache.org" target="_blank">Apache Cloudstack</a>. Microsoft Open Technologies provides a VMDepot for sharing images for Azure. Which is great cause it can save you quite a bit of time!</p>
<p>
The  OpenMeetings project made version v2.1.1 of Apache OpenMeetings available at VMDepot: </p>
<p><a href="http://vmdepot.msopentech.com/Vhd/Show?vhdId=5568&amp;version=5604" target="_BLANK">http://vmdepot.msopentech.com/Vhd/Show?vhdId=5568&amp;version=5604</a></p>
<p>
For anyone not familiar with Azure those are step by step instructions on how to get your copy of the VM running:</p>
<h4>1) Login your Azure admin panel at https://manage.windowsazure.com</h4>

<a href="https://blogs.apache.org/openmeetings/mediaresource/84b123fd-e51e-4c76-b482-1cdb526a4185"><img src="https://blogs.apache.org/openmeetings/mediaresource/84b123fd-e51e-4c76-b482-1cdb526a4185?t=true" alt="install_step_browse.png"></img></a>

<h4>2) Browse the public VMs and select the OpenMeetings image</h4>

<a href="https://blogs.apache.org/openmeetings/mediaresource/84020922-a78b-4b16-a900-5d432329bc7e"><img src="https://blogs.apache.org/openmeetings/mediaresource/84020922-a78b-4b16-a900-5d432329bc7e?t=true" alt="install_step_choose.png"></img></a>

<h4>3) Add the ports 1935 (RTMP), 5080 (HTTP) and 22 (SSH) to the endpoint config</h4>

<a href="https://blogs.apache.org/openmeetings/mediaresource/2fbf5285-e057-4401-9cfd-88dcb8cbdb9c"><img src="https://blogs.apache.org/openmeetings/mediaresource/2fbf5285-e057-4401-9cfd-88dcb8cbdb9c?t=true" alt="install_step_add_endpoints.png"></img></a>

<br/>
<p>Then start the VM and goto the URL: $VM_URL:5080/openmeetings</p>
<br/>
<p>The passwords are:</p>
<ul>
<li><p>SSH: openmeetings.cloudapp.net </p>
<p>user: azureuser</p>
<p>pass: ?7Qy%W[[{%H7z{E</p>
</li>
<li><p>MySQL</p>
<p>MySQL root: root</p>
<p>pass: KsVECoENs$x:Uy(</p>
</li>
<li><p>Openmeetings database</p>
<p>user: om_admin</p>
<p>pass: $=z:[Mjx(m+HAEr</p>
</li>
<li><p>OpenMeetings</p>
<p>user: admin</p>
<p>pass: &#126;9$bfg+}^&amp;/&lt;X&#126;a</p>
</li>
</ul>
<br/><br/>
<h4>Great work Maxim and Vasiliy !</h4>
<br/><br/>
