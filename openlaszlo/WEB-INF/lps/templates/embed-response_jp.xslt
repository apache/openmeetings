<?xml version="1.0" encoding="utf-8"?>

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  
  <xsl:output method="text"/>
  
  <xsl:param name="url" select="//request/@url"/>
  <xsl:param name="query_args" select="//request/@query_args"/>
  
  <xsl:template match="/">
document.write('&lt;object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"\n');
document.write('codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,79,0"\n');
document.write('width="<xsl:value-of select="canvas/@width"/>" height="<xsl:value-of select="canvas/@height"/>"&gt;\n');
document.write('&lt;param name="movie" value="<xsl:value-of select="$url"/>?lzt=swf<xsl:value-of select="$query_args"/>"/ &gt;\n');
document.write('&lt;param name="quality" value="best"/ &gt;\n');
document.write('&lt;param name="scale" value="noscale"/ &gt;\n');
document.write('&lt;param name="salign" value="LT"/ &gt;\n');
document.write('&lt;param name="menu" value="false"/ &gt;\n');
document.write('&lt;param name="bgcolor" value="<xsl:value-of select="/canvas/@bgcolor"/>"/ &gt;\n');
document.write('&lt;embed src="<xsl:value-of select="$url"/>?lzt=swf<xsl:value-of select="$query_args"/>"\n');
document.write('quality="best"\n');
document.write('scale="noscale"\n');
document.write('salign="lt"\n');
document.write('width="<xsl:value-of select="/canvas/@width"/>"\n');
document.write('height="<xsl:value-of select="/canvas/@height"/>"\n');
document.write('bgcolor="<xsl:value-of select="/canvas/@bgcolor"/>"\n');
document.write('type="application/x-shockwave-flash"\n');
document.write('pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash"/ &gt;\n');
document.write('&lt;/object&gt;');
  </xsl:template>
</xsl:stylesheet>
