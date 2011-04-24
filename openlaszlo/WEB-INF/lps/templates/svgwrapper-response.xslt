<?xml version="1.0" encoding="utf-8"?>
<!--
@!@!@!@!@ ATTENTION EDITORS OF THIS FILE @!@!@!@!@

If you edit this file, please validate your work using http://validator.w3.org/
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html"
              indent="yes"
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

  <xsl:template match="/">
    <html>
      <head>
        <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
        <title>
          <xsl:value-of select="/canvas/@title"/>
        </title>
      </head>
      <body>

<script type="text/javascript">

function doSVGEval (expr) {
  val = top.eval_in_svg(expr);
  alert('eval := '+val);
}

</script>

  <form id="dhtml-debugger-input" action="#" onsubmit="doSVGEval(document.getElementById('LaszloDebuggerInput').value); return false">
    <div>
      <input size="150" id="LaszloDebuggerInput" type="text"/>
      <input  type="button" value="eval" onclick="doSVGEval(document.getElementById('LaszloDebuggerInput').value); return false"/>
    </div>
  </form>


<div>
<object type="image/svg+xml" name="app" 
         data="{/canvas/request/@url}?lzt=svg{/canvas/request/@query_args}" 
         width="1200" height="600">Your browser does not support SVG rendering</object>
</div>

      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
     * Copyright 2001-2006 Laszlo Systems, Inc.  All Rights Reserved.              *
     * Use is subject to license terms.                                            *
     * X_LZ_COPYRIGHT_END ****************************************************** -->


