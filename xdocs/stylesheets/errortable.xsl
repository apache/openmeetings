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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>
	
	<xsl:template match="ROOT">
<html>
	<head><title>Errors Table</title></head>
	<body>
		<h3>The table of OM errors</h3>
		<table>
			<tr>
				<th>Code</th>
				<th>Type</th>
				<th>Description</th>
			</tr>
			<xsl:apply-templates/>
		</table>
	</body>
</html>
	</xsl:template>
	
	<xsl:template match="row">
			<tr>
				<td>-<xsl:value-of select="field[@name='errorvalues_id']"/></td>
				<td>
					<xsl:variable name="typeId" select="field[@name='errortype_id']"/>
					<xsl:variable name="x">
						<xsl:choose>
							<xsl:when test="$typeId='1'">322</xsl:when>
							<xsl:otherwise>323</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:value-of select="document('../../WebContent/languages/english.xml')/language/string[@id=$x]/value" />
				</td>
				<td>
					<xsl:variable name="descId" select="field[@name='fieldvalues_id']"/>
					<xsl:value-of select="document('../../WebContent/languages/english.xml')/language/string[@id=$descId]/value" />
				</td>
			</tr>
	</xsl:template>
</xsl:stylesheet>