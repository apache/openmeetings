<?xml version="1.0" encoding="utf-8"?>
<!--
@!@!@!@!@ ATTENTION EDITORS OF THIS FILE @!@!@!@!@

If you edit this file, please validate your work using http://validator.w3.org/
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

 <xsl:output 
  doctype-system="http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd"
  doctype-public="-//W3C//DTD SVG 1.0//EN"
       media-type="image/svg" indent="yes"/>
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
 <svg id="svgdoc" 
    xmlns="http://www.w3.org/2000/svg"
    xmlns:xlink="http://www.w3.org/1999/xlink" 
     width="100%" height="100%" viewBox="0 0 1200 600" 
     onload="onLoad(evt)"
     >


<!--      onmousemove="LzMouseKernel.__mouseEvent()"
     onmousedown="LzMouseKernel.__mouseEvent()"
     onmouseup="LzMouseKernel.__mouseEvent()"
-->

     <title> <xsl:value-of select="/canvas/@title"/> </title>

    <g  x="0" y="0" width="1200" height="1200" id="_canvasrect" />


    <rect x="800" width="20" height="20" fill="green" id="_protoRect" 
      onclick="_debug('clicked on '+this.id); "
      />

    <g transform="translate(0,100)" id="DebugConsole" font-size="12px" font-family="Arial">
      <text id="_protoText" ></text>
    </g>

    <script type="text/ecmascript">

      // placeholder
      Debug = {error: function (a1, a2,a3) { _debug(a1+","+a2+","+a3); },
               write: function (a1, a2,a3) { _debug(a1+","+a2+","+a3); },
               info: function (a1, a2,a3) { _debug(a1+","+a2+","+a3); }
      }

      function getTimer () { return (new Date).getTime(); }

      function gid (id) { return document.getElementById(id);}
          var svg_ns = "http://www.w3.org/2000/svg";    

          var DebugConsole;
          var _svgdebug = {};
          _svgdebug.lineheight = 14;
          _svgdebug.lines = [''];

          var LzApplicationRoot = '<xsl:value-of select="/canvas/request/@lps"/>';

         
         function _debug (str) {
             _svgdebug.lines[_svgdebug.lines.length]= str;
             var tspan = document.createElementNS(svg_ns, "text");
             tspan.setAttribute('x', 0);
             tspan.setAttribute('y', _svgdebug.lines.length*_svgdebug.lineheight);
             var content = document.createTextNode(str);
             tspan.appendChild(content);
             DebugConsole.appendChild(tspan);
         }

          function onLoad(evt){
              // Get Document
                var svgElement = evt.target;
                var doc = svgElement.ownerDocument;
                top.eval_in_svg = function (x) { return eval(x); } ;
                DebugConsole = document.getElementById("DebugConsole");
                //DebugConsole = document.getElementById("DebugConsole");
                _debug('loaded');

            }

   DebugConsole = document.getElementById("DebugConsole");

    </script>

   <script xmlns:xlink="http://www.w3.org/1999/xlink"  xlink:href="{/canvas/request/@url}?lzt=lfc{/canvas/request/@query_args}&amp;_canvas_debug={/canvas/@debug}"/>

   <script xmlns:xlink="http://www.w3.org/1999/xlink"  xlink:href="{/canvas/request/@url}?lzt=object{/canvas/request/@query_args}"/>

</svg>
  </xsl:template>
</xsl:stylesheet>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
     * Copyright 2001-2006 Laszlo Systems, Inc.  All Rights Reserved.              *
     * Use is subject to license terms.                                            *
     * X_LZ_COPYRIGHT_END ****************************************************** -->


