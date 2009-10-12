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

  <xsl:template name="windowdimension">
    <xsl:param name="value" />
    <xsl:param name="default" />
    <xsl:choose>
      <xsl:when test="substring-before($value, '%') = ''">
        <xsl:value-of select="$value"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$default"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:param name="windowheight">
    <xsl:call-template name="windowdimension">
      <xsl:with-param name="value" select="/canvas/@height" />
      <xsl:with-param name="default" select="'null'" />
    </xsl:call-template>
  </xsl:param>
  <xsl:param name="windowwidth">
    <xsl:call-template name="windowdimension">
      <xsl:with-param name="value" select="/canvas/@width" />
      <xsl:with-param name="default" select="'null'" />
    </xsl:call-template>
  </xsl:param>

  <xsl:template match="/">
    <html>
      <head>
        <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
        <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
        <!-- this tag helps laszlo apps look good on the iPhone. -->
        <meta name="viewport" content="width=device-width; initial-scale=1.0;"/>
        <title>
          <xsl:value-of select="/canvas/@title"/>
        </title>
        <script type="text/javascript">
          // If loaded bare into a browser, set the browser size to the canvas size
          if (window === top) {
            (function (width, height) {
              // Cf. http://www.quirksmode.org/viewport/compatibility.html
              if (window.innerHeight) {
                // Sadly, innerHeight/Width is not r/w on some browsers, and resizeTo is for outerHeight/Width
                window.resizeTo(width ? (width + window.outerWidth - window.innerWidth) : window.outerWidth,
                                height ? (height + window.outerHeight - window.innerHeight) : window.outerHeight);
              } else if (document.documentElement &amp;&amp; document.documentElement.clientHeight) {
                if (width) {
                  document.documentElement.clientWidth = width;
                }
                if (height) {
                  document.documentElement.clientHeight = height;
                }
              } else {
                if (width) {
                  document.body.clientWidth = width;
                }
                if (height) {
                  document.body.clientHeight = height;
                }
              }
            })(<xsl:value-of select="$windowwidth"/>, <xsl:value-of select="$windowheight"/>);
          }
        </script>
        <script type="text/javascript" src="{/canvas/request/@lps}/lps/includes/embed-compressed.js"/>
        <xsl:choose>
          <xsl:when test="/canvas/@runtime = 'dhtml'">
            <script type="text/javascript">
              lz.embed.lfc('<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/lfc/<xsl:value-of select="/canvas/@lfc"/>', '<xsl:value-of select="/canvas/request/@lps"/>/');
            </script>
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
     * Copyright 2001-2009 Laszlo Systems, Inc.  All Rights Reserved.              *
     * Use is subject to license terms.                                            *
     * X_LZ_COPYRIGHT_END ****************************************************** -->
