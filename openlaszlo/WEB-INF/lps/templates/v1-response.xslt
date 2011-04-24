<?xml version="1.0" encoding="utf-8"?>

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  
  <xsl:output method="html" indent="yes"/>
  
  <xsl:param name="lps"><xsl:value-of select="/*/request/@lps"/></xsl:param>
  <xsl:param name="assets"><xsl:value-of select="/*/request/@lps"/>/lps/assets</xsl:param>
  <xsl:param name="query_args" select="/*/request/@query_args"/>
  <xsl:param name="url" select="/*/request/@url"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>
          <xsl:value-of select="canvas/@title"/>
        </title>
      </head>
      <body marginwidth="0" marginheight="0" topmargin="0" leftmargin="0"
            bgcolor="{canvas/@bgcolor}">
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,79,0"
                width="{canvas/@width}" height="{canvas/@height}">
          <param name="movie" value="{$url}?lzt=swf{$query_args}"/>
          <param name="quality" value="best"/>
          <param name="scale" value="noscale"/>
          <param name="salign" value="LT"/>
          <param name="menu" value="false"/>
          <param name="bgcolor" value="{/canvas/@bgcolor}"/>
          <embed src="{$url}?lzt=swf{$query_args}"
                 quality="best"
                 scale="noscale"
                 salign="lt"
                 width="{/canvas/@width}"
                 height="{/canvas/@height}"
                 bgcolor="{/canvas/@bgcolor}"
                 type="application/x-shockwave-flash"
                 pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"/>
        </object>

        <xsl:apply-templates select="/canvas/warnings"/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="canvas/warnings">
    <div id="warnings">
      <h2>Compilation Warnings</h2>
      <p class="warning">
        <xsl:for-each select="error">
          <xsl:if test="position() > 1">
            <br/>
          </xsl:if>
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </p>
    </div>
    
  </xsl:template>
</xsl:stylesheet>
