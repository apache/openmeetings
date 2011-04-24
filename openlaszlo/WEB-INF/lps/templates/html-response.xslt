<?xml version="1.0" encoding="utf-8"?>
<!--
@!@!@!@!@ ATTENTION EDITORS OF THIS FILE @!@!@!@!@

If you edit this file, please validate your work using http://validator.w3.org/
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html"
              indent="no"
              doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
              doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <!--
      In standards mode, your dimensions must have explicit units
  -->
  <xsl:template name="canvasdimension">
    <xsl:param name="value" />
    <xsl:choose>
      <xsl:when test="substring-before($value, '%') = ''">
        <xsl:value-of select="concat($value, 'px')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:param name="canvasheight"><xsl:call-template name="canvasdimension"><xsl:with-param name="value" select="/canvas/@height" /></xsl:call-template></xsl:param>
  <xsl:param name="canvaswidth"><xsl:call-template name="canvasdimension"><xsl:with-param name="value" select="/canvas/@width" /></xsl:call-template></xsl:param>

  <!-- shared body template for embedding -->
  <xsl:include href="embed-body.xslt"/>

  <xsl:template match="/">
    <html>
      <head>
        <xsl:copy-of select="/canvas/wrapperheaders/node()"/> 
        <meta http-equiv="X-UA-Compatible" content="chrome=1"/>
        <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
        <!-- this tag helps laszlo apps look good on the iPhone. -->
        <meta name="viewport" content="width=device-width; initial-scale=1.0;"/>
        <title>
          <xsl:value-of select="/canvas/@title"/>
        </title>
        <script type="text/javascript" src="{/canvas/request/@lps}/lps/includes/embed-compressed.js"/>
        <xsl:choose>
          <xsl:when test="/canvas/@runtime = 'dhtml'">
            <xsl:comment>[if lt IE 9]&gt;&lt;script type="text/javascript" src="<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/excanvas.js"&gt;&lt;/script&gt;&lt;![endif]</xsl:comment>
          </xsl:when>
        </xsl:choose>
        <style type="text/css">
            html, body
            {
                /* http://www.quirksmode.org/css/100percheight.html */
                height: 100%;
                /* prevent browser decorations */
                margin: 0;
                padding: 0;
                border: 0 none;
            }
            body {
                background-color: <xsl:value-of select="/canvas/@bgcolor"/>;
            }
            img { border: 0 none; }
        </style>
        <xsl:comment>[if IE]&gt;
        &lt;style type="text/css"&gt;
            /* Fix IE scrollbar braindeath */
            html { overflow: auto; overflow-x: hidden; }
        &lt;/style&gt;
        &lt;![endif]</xsl:comment>
      </head>
      <body>
        <xsl:call-template name="body"/>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
     * Copyright 2001-2010 Laszlo Systems, Inc.  All Rights Reserved.              *
     * Use is subject to license terms.                                            *
     * X_LZ_COPYRIGHT_END ****************************************************** -->
