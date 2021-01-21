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
			<div class="container grid-striped">
				<div class="row font-weight-bold mb-3">
					<div class="col-1 p-2"> # </div>
					<div class="col-3 text-break p-2"> key </div>
					<div class="col-3 text-break p-2"> default </div>
					<div class="col-4 text-break p-2"> meaning </div>
					<div class="col-1 p-2"> version </div>
				</div>
			<xsl:apply-templates/>
			</div>
		</section>
	</body>
</document>
	</xsl:template>

	<xsl:template match="config">
				<div class="row mb-3">
					<div class="col-1 p-2"><xsl:value-of select="position() div 2"/></div>
					<div class="col-3 text-break p-2">
						<xsl:choose>
							<xsl:when test="type='BOOL'">
								<div class="pr-2 fas fa-toggle-on text-success fa-2x" title="Boolean"></div>
							</xsl:when>
							<xsl:when test="type='NUMBER'">
								<div class="pr-2 fas fa-2x" title="Number">#</div>
							</xsl:when>
							<xsl:otherwise>
								<div class="pr-2 fas fa-font fa-2x" title="String"></div>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="key"/>
					</div>
					<div class="col-3 text-break font-italic font-weight-bold p-2"><xsl:value-of select="value"/></div>
					<div class="col-4 text-break p-2"><xsl:value-of select="comment"/></div>
					<div class="col-1 p-2"><xsl:value-of select="fromVersion"/></div>
				</div>
	</xsl:template>
</xsl:stylesheet>
