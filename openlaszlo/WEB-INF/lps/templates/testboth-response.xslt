<?xml version="1.0" encoding="utf-8"?>
<!--
  html-response.xslt
--> 
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2006 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:output method="html"
              indent="yes"
              doctype-public="-//W3C//DTD HTML 4.01 Frameset//EN"
              doctype-system="http://www.w3.org/TR/html4/frameset.dtd"/>

<xsl:template match="/">
<html>
  <head>
    <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
    <title>
      <xsl:value-of select="/canvas/@title"/>
    </title>
    <style type="text/css">
      img {border: 0}
      body {margin: 0}
    </style>    
  </head>
  <frameset cols="50%, 50%">
    <frame src="{/canvas/request/@url}?lzr=dhtml{/canvas/request/@query_args}"/>
    <frame src="{/canvas/request/@url}?lzr={/canvas/@runtime}{/canvas/request/@query_args}"/>
  </frameset>
</html>
</xsl:template>

<xsl:template match="canvas/warnings">
        <xsl:for-each select="error">
          <xsl:if test="position() > 1">
            <br/>
          </xsl:if>
          <xsl:apply-templates select="."/>
        </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
