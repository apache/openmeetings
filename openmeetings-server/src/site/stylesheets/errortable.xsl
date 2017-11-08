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
	<xsl:param name="languagesDir"/>
	<xsl:output method="xml"/>

	<xsl:template match="ROOT">
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
		<title>Openmeetings Errors table</title>
		<author email="dev@openmeetings.apache.org">Apache OpenMeetings Documentation Robot</author>
	</properties>
	<body>
		<section name="Openmeetings Errors table">
		<table>
			<tr>
				<th>Error Code</th>
				<th>Description</th>
			</tr>
			<xsl:apply-templates/>
		</table>
		</section>
	</body>
</document>
	</xsl:template>

	<xsl:template match="row">
		<xsl:variable name="englishPath"><xsl:value-of select="concat($languagesDir, '/Application.properties.xml')"/></xsl:variable>
		<xsl:variable name="descId" select="field[@name='labelKey']"/>
			<tr>
				<td><xsl:value-of select="$descId"/></td>
				<td>
					<xsl:value-of select="document($englishPath)/properties/entry[@key=$descId]/text()" />
				</td>
			</tr>
	</xsl:template>
</xsl:stylesheet>
