<?xml version="1.0" encoding="utf-8"?>
<!--
  html-response.xslt
--> 

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
  
  <xsl:template match="/">
<html>
  <head><title>Canvas XML</title></head>
  <body>
    <h1>Canvas XML</h1>
    <p>This page is intended to help debug implementation of the LPS
  console.  View its source to see the XML that the LPS passes to the
  response templates.</p>
    
    <xsl:text>


    </xsl:text>

    <xsl:comment>Canvas XML starts here:</xsl:comment>
    <xsl:copy-of select="/"/>
    <xsl:comment>Canvas XML ends here.</xsl:comment>
    <xsl:text>

    </xsl:text>
  </body>
</html>
  </xsl:template>
</xsl:stylesheet>
