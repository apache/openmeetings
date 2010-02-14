<?xml version="1.0" encoding="utf-8"?>
<!--
@!@!@!@!@ ATTENTION EDITORS OF THIS FILE @!@!@!@!@

If you edit this file, please validate your work using http://validator.w3.org/
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:template name="body">
        <xsl:choose>
          <xsl:when test="/canvas/request/@pocketpc = 'true'">
            <OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                    width="{/canvas/@width}"
                    height="{/canvas/@height}"
                    id="{/canvas/@id}">
              <PARAM NAME="movie" VALUE="{/canvas/request/@url}?lzt=swf{/canvas/request/@query_args}"/>
            </OBJECT>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="/canvas/@runtime = 'dhtml'">
                <div id="lzsplash" style="z-index: 10000000; top: 0; left: 0; width: {$canvaswidth}; height: {$canvasheight}; position: fixed; display: table"><p style="display: table-cell; vertical-align: middle;"><img src="{/canvas/request/@lps}/lps/includes/spinner.gif" style="display: block; margin: 20% auto" alt="application initializing"/></p></div>
                <script type="text/javascript">
                  lz.embed.dhtml({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=object<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>', cancelmousewheel: false, cancelkeyboardcontrol: false, skipchromeinstall: false, usemastersprite: false, approot: ''});
                  lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
                    var s = document.getElementById('lzsplash');
                    if (s) LzSprite.prototype.__discardElement(s);
                  }
                </script>
              </xsl:when>
              <xsl:otherwise>
                <script type="text/javascript">
                  lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', allowfullscreen: '<xsl:value-of select="/canvas/@allowfullscreen"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>', cancelmousewheel: false});

                  lz.embed.<xsl:value-of select="/canvas/@id"/>.onloadstatus = function loadstatus(p) {
                    // called with a percentage (0-100) indicating load progress
                  }

                  lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
                    // called when this application is done loading
                  }
                </script>
              </xsl:otherwise>
            </xsl:choose>
            <noscript>
                Please enable JavaScript in order to use this application.
            </noscript>
          </xsl:otherwise>
        </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
     * Copyright 2001-2009 Laszlo Systems, Inc.  All Rights Reserved.              *
     * Use is subject to license terms.                                            *
     * X_LZ_COPYRIGHT_END ****************************************************** -->
