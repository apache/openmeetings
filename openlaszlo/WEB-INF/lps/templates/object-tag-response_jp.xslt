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
  <xsl:param name="url" select="/*/request/@url"/>
  <xsl:param name="query_args" select="/*/request/@query_args"/>
  
  <xsl:template match="/">
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
            codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,79,0"
            width="{canvas/@width}" height="{canvas/@height}">
      <param name="movie" value="{$url}?lzt=swf{$query_args}"/>
      <param name="quality" value="best"/>
      <param name="scale" value="exactfit"/>
      <param name="salign" value="LT"/>
      <param name="menu" value="false"/>
      <param name="bgcolor" value="{/canvas/@bgcolor}"/>
      <embed src="{$url}?lzt=swf{$query_args}"
             quality="best"
             scale="exactfit"
             salign="lt"
             width="{/canvas/@width}"
             height="{/canvas/@height}"
             bgcolor="{/canvas/@bgcolor}"
             type="application/x-shockwave-flash"
             pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"/>
    </object>
  </xsl:template>
</xsl:stylesheet>
