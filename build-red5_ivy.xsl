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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:output omit-xml-declaration="no"/>
    <xsl:template match="node()|@*">
      <xsl:copy>
         <xsl:apply-templates select="node()|@*"/>
      </xsl:copy>
    </xsl:template>

    <xsl:template match="//*[@name='jaudiotagger']">
		<xsl:text disable-output-escaping="yes">
			&lt;dependency org="org" name="jaudiotagger" rev="2.0.4-SNAPSHOT" &gt;
				&lt;include type="jar" /&gt;
			&lt;/dependency&gt;
		</xsl:text>
	</xsl:template>
    <xsl:template match="//*[@name='bcprov-jdk16']">
		<xsl:text disable-output-escaping="yes">
			&lt;dependency org="org.bouncycastle" name="bcprov-jdk16" rev="1.45" conf="java6-&gt;*" &gt;
				&lt;include type="jar" /&gt;
			&lt;/dependency&gt;
		</xsl:text>
	</xsl:template>
    <xsl:template match="//*[@name='sysout-over-slf4j']">
		<xsl:text disable-output-escaping="yes">
			&lt;dependency org="uk.org.lidalia" name="sysout-over-slf4j" rev="1.0.2" &gt;
				&lt;include type="jar" /&gt;
			&lt;/dependency&gt;
		</xsl:text>
	</xsl:template>
    <xsl:template match="//*[@name='httpcore']">
		<xsl:text disable-output-escaping="yes">
			&lt;dependency org="org.apache.httpcomponents" name="httpcore" rev="4.2.1" &gt;
				&lt;include type="jar" /&gt;
			&lt;/dependency&gt;
		</xsl:text>
	</xsl:template>
    <xsl:template match="//*[@name='httpclient']">
		<xsl:text disable-output-escaping="yes">
			&lt;dependency org="org.apache.httpcomponents" name="httpclient" rev="4.2" &gt;
				&lt;include type="jar" /&gt;
			&lt;/dependency&gt;
		</xsl:text>
	</xsl:template>
</xsl:stylesheet>
