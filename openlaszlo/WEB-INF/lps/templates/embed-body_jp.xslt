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
                <div id="appcontainer"></div>
                <div id="lzsplash" style="z-index: 10000000; top: 0; left: 0; width: {$canvaswidth}; height: {$canvasheight}; position: fixed; display: table"><p style="display: table-cell; vertical-align: middle;"><img src="{/canvas/request/@lps}/lps/includes/spinner.gif" style="display: block; margin: 20% auto" alt="application initializing"/></p></div>
                <script type="text/javascript" defer="defer">
                  lz.embed.resizeWindow('<xsl:value-of select="/canvas/@width"/>', '<xsl:value-of select="/canvas/@height"/>');
                  lz.embed.dhtml({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=object<xsl:value-of select="/canvas/request/@query_args"/>', lfcurl: '<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/lfc/<xsl:value-of select="/canvas/@lfc"/>', serverroot: '<xsl:value-of select="/canvas/request/@lps"/>/', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>', cancelmousewheel: false, cancelkeyboardcontrol: false, skipchromeinstall: false, usemastersprite: false, approot: '', appenddivid: 'appcontainer'});
                  lz.embed.applications.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
                    // called when this application is done loading
                    var el = document.getElementById('lzsplash');
                    if (el.parentNode) {
                        el.parentNode.removeChild(el);
                    }
                  }
                </script>
              </xsl:when>
              <xsl:otherwise>
                <div id="appcontainer"></div>
                <div id="lzsplash" style="z-index: 10000000; top: 0; left: 0; width: {$canvaswidth}; height: {$canvasheight}; position: fixed; display: table"><p style="display: table-cell; vertical-align: middle; align: center;"><div id="lzsplashtext" style="display: block; margin: 20% auto; font-size:12px; font-family:Helvetica,sans-serif;" align="center">Loading...</div></p></div>
                <script type="text/javascript" defer="defer">
                  lz.embed.resizeWindow('<xsl:value-of select="/canvas/@width"/>', '<xsl:value-of select="/canvas/@height"/>');
                  lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', allowfullscreen: '<xsl:value-of select="/canvas/@allowfullscreen"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>', cancelmousewheel: false, appenddivid: 'appcontainer'});

                  lz.embed.applications.<xsl:value-of select="/canvas/@id"/>.onloadstatus = function loadstatus(p) {
                    // called with a percentage (0-100) indicating load progress
                    var el = document.getElementById('lzsplashtext');
                    if (el) {
                        if (p == 100) {
                            var splash = document.getElementById('lzsplash');
                            if (splash) {
                                splash.parentNode.removeChild(splash);
                            }
                        } else {
                            el.innerHTML = p + '% loaded'
                        }
                    }
                  }

                  lz.embed.applications.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
                    // called when this application is done loading and the 
                    // canvas has initted
                  }
                </script>
              </xsl:otherwise>
            </xsl:choose>
            <noscript>
              アプリケーションを利用するにはJavaScriptを有効にする必要があります。
            </noscript>
          </xsl:otherwise>
        </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2010 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
