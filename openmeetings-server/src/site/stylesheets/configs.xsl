<?xml version="1.0" encoding="UTF-8"?>
<!--
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml"/>

	<xsl:template match="root">
<document>
<xsl:comment>
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
</xsl:comment>
	<properties>
		<title>List of general configuration options</title>
		<author email="dev@openmeetings.apache.org">Apache OpenMeetings Team</author>
	</properties>
	<body>
		<section name="List of general configuration options">
			<p>In Administration &gt; Configuration there are a number of
				configuration values.
			</p>
			<p></p>
			<table>
				<tr>
					<th> key </th>
					<th> default </th>
					<th> meaning </th>
					<th> availabe since OpenMeetings version </th>
				</tr>
			<xsl:apply-templates/>
			</table>
		</section>
	</body>
</document>
	</xsl:template>

	<xsl:template match="config">
				<tr>
					<td><xsl:value-of select="key"/></td>
					<td><xsl:value-of select="value"/></td>
					<td><xsl:value-of select="comment"/></td>
					<td><xsl:value-of select="fromVersion"/></td>
				</tr>
	</xsl:template>
</xsl:stylesheet>
