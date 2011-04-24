<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                version="1.0" >

<!-- This is another simple identity function;
     It is a good starting point to write XSLT transforms. -->

 <xsl:template match="@*|*|processing-instruction()|comment()">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|text()|processing-instruction()|comment()" />
  </xsl:copy>
</xsl:template>

</xsl:stylesheet> 

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2006 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

