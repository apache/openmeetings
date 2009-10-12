<?xml version="1.0" encoding="utf-8"?>

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  
  <xsl:output method="html"
              indent="yes"
              doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
              doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
  
  <xsl:param name="lps"><xsl:value-of select="/*/request/@lps" /></xsl:param>
  <xsl:param name="assets"><xsl:value-of
          select="/*/request/@lps" />/lps/assets</xsl:param>
  <xsl:param name="query_args" select="/*/request/@query_args" />
  <xsl:param name="url" select="/*/request/@url" />
  
  <xsl:template match="/">
    <html>
      <head>
        <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico" />
        <title>
          <xsl:value-of select="canvas/@title" />
        </title>
        <style type="text/css">
          html, body { margin: 0; padding: 0; height: 100%; }
          body { background-color: <xsl:value-of select="/canvas/@bgcolor"/>; }
        </style>
      </head>
      <body>
        <xsl:choose>
          <xsl:when test="/canvas/request/@windows = 'true'">
            <!-- WinIE specific code, not valid XHTML, fails to display in some browsers -->
            <!-- Ensures Flash 6.0r79 or better plugin is installed -->
            <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                    codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,79,0"
                    data="{$url}?lzt=swf{$query_args}"
                    width="{canvas/@width}" height="{canvas/@height}">
              <param name="movie" value="{$url}?lzt=swf{$query_args}" />
              <param name="quality" value="best" />
              <param name="scale" value="noscale" />
              <param name="salign" value="LT" />
              <param name="menu" value="false" />
              <param name="bgcolor" value="{/canvas/@bgcolor}" />
              <embed src="{$url}?lzt=swf{$query_args}"
                     quality="best"
                     scale="noscale"
                     salign="lt"
                     width="{/canvas/@width}"
                     height="{/canvas/@height}"
                     bgcolor="{/canvas/@bgcolor}"
                     type="application/x-shockwave-flash"
                     pluginspage="http://www.macromedia.com/go/getflashplayer" />
            </object>
          </xsl:when>
          <xsl:otherwise>
            <!-- XHTML 1.0 code valid for all browsers -->
            <object type="application/x-shockwave-flash"
                    data="{$url}?lzt=swf{$query_args}"
                    width="{canvas/@width}" height="{canvas/@height}">
              <param name="movie" value="{$url}?lzt=swf{$query_args}" />
              <param name="quality" value="best" />
              <param name="scale" value="noscale" />
              <param name="salign" value="LT" />
              <param name="menu" value="false" />
            </object>
          </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>

