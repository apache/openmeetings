<?xml version="1.0" encoding="utf-8"?>
<!--
  js-response.xslt
 
  This software is the proprietary information of Laszlo Systems, Inc.
  Use is subject to license terms.
--> 

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  
  <xsl:output method="text"/>
  
  <xsl:template match="/">
    <text>
        lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@fullpath"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'});
    </text>
  </xsl:template>

</xsl:stylesheet>
