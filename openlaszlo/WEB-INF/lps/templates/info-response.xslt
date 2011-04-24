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

  <xsl:output method="html" indent="yes"/>
  
  <xsl:param name="lps"><xsl:value-of select="/*/request/@lps"/></xsl:param>
  <xsl:param name="sum" select="/canvas/info/@size"/>
  <xsl:param name="runtime" select="/canvas/@runtime"/>
  <xsl:param name="showpercent" select="$runtime = 'swf5'"/>
  
  <xsl:param name="title" select="concat('Compilation Statistics: ', canvas/@title)"/>
  
  <xsl:template match="/canvas">
    <html>
      <head>
        <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
        <link rel="stylesheet" href="{$lps}/lps/includes/styles.css" type="text/css"/>
        
        <title>
          <xsl:value-of select="$title"/>
        </title>
      </head>
      <body>
        <h1><xsl:value-of select="$title"/></h1>
        
        <h2>Summary</h2>
        <xsl:apply-templates select="info"/>
        <xsl:if test="$showpercent">
          <p>
            The following tables show the contributions of various
            elements to the <em>uncompressed</em> application size.  The
            percentage contribution of class definitions and instances
            to the <em>compressed</em> application size is probably less
            than what is shown.  The percentage contribution of fonts
            and resources is probably greater.
          </p>
        </xsl:if>
        
        <table border="1">
          <xsl:call-template name="overview-row">
            <xsl:with-param name="name" select="'LFC'"/>
            <xsl:with-param name="href" select="'#includes'"/>
            <xsl:with-param name="size" select="sum(stats/include/@size)"/>
          </xsl:call-template>
          <xsl:call-template name="overview-row">
            <xsl:with-param name="name" select="'Class Definitions'"/>
            <xsl:with-param name="href" select="'#classes'"/>
            <xsl:with-param name="size" select="sum(stats/block[@tagname='class']/@size)"/>
          </xsl:call-template>
          <xsl:call-template name="overview-row">
            <xsl:with-param name="name" select="'Instances'"/>
            <xsl:with-param name="href" select="'#instances'"/>
            <xsl:with-param name="size" select="sum(stats/block[@tagname!='class']/@size)"/>
          </xsl:call-template>
          <xsl:call-template name="overview-row">
            <xsl:with-param name="name" select="'Resources'"/>
            <xsl:with-param name="href" select="'#resources'"/>
            <xsl:with-param name="size" select="sum(stats/swf/resource/@filesize)"/>
          </xsl:call-template>
          <xsl:call-template name="overview-row">
            <xsl:with-param name="name" select="'Fonts'"/>
            <xsl:with-param name="href" select="'#fonts'"/>
            <xsl:with-param name="size" select="sum(stats/swf/font/@filesize)"/>
          </xsl:call-template>
        </table>
        
        <h2 id="includes">Auto-Includes</h2>
        <p>These are the library files that this application
        auto-includes.  Every application includes a version of the
        LFC.lzl library, where the version is selected depending on
        the values of the krank, profile, and debug settings.  Other
        library files are autoincluded if the application references
        (uses or extends) a tag that is defined in one of these
        libraries, and that the application itself does not define a
        different definition for.</p>
        <table border="1">
            <tr><th>Library</th>
                <th>Explanation</th>
                <th>Size</th>
            </tr>
          <xsl:apply-templates select="stats/include"/>
        </table>
        <p>The application may include additional files using
        <code>include</code> statements.  The sizes from elements in
        these files are included in the tables below.</p>
        
        <h2 id="classes">Class Definitions</h2>

        <p>These are sizes that class definitions compile to. The
        sizes are uncompressed block sizes, and overestimate their
        size in the compressed application.</p>
        <table border="1">
          <tr>
            <th>Pathname:line number</th>
            <th>name</th>
            <th>Size<sup><a href="#size-legend">*</a></sup></th>
            <xsl:if test="$showpercent">
              <th>Percentage<sup><a href="#percentage-legend">*</a></sup></th>
            </xsl:if>
          </tr>
          <xsl:apply-templates select="stats/block[@tagname='class']">
              <xsl:sort data-type="number" order="descending" select="@size"/>
          </xsl:apply-templates>
          <tr>
            <td><i>Total</i></td>
            <td><i><xsl:value-of select="count(stats/block[@tagname='class'])"/> items</i></td>
            <xsl:call-template name="size-and-percentage">
              <xsl:with-param name="class" select="'total'"/>
              <xsl:with-param name="value" select="sum(stats/block[@tagname='class']/@size)"/>
            </xsl:call-template>
          </tr>
        </table>
        <ul>
          <li id="size-legend">Size in bytes</li>
          <xsl:if test="$showpercent">
            <li id="percentage-legend">Percentage of uncompressed
            application size.</li>
          </xsl:if>
        </ul>

        <h2 id="instances">Top-Level Instances</h2>

        <p>These are sizes that the top-level instances (the children
        of <code>canvas</code> and <code>library</code> elements)
        compile to.  The sizes are uncompressed block sizes, and
        overestimate their size in the compressed application.</p>
        
        <p>These sizes include children of top-level instances.</p>
        
        <table border="1">
          <tr>
            <th>Pathname:line number</th>
            <th>ID or name</th>
            <th>Size<sup><a href="#size-legend">*</a></sup></th>
            <xsl:if test="$showpercent">
              <th>Percentage<sup><a href="#percentage-legend">*</a></sup></th>
            </xsl:if>
          </tr>
          <xsl:apply-templates select="stats/block[@tagname!='class']">
              <xsl:sort data-type="number" order="descending" select="@size"/>
          </xsl:apply-templates>
          <tr>
            <td><i>Total</i></td>
            <td><i><xsl:value-of select="count(stats/block[@tagname!='class'])"/> items</i></td>
            <xsl:call-template name="size-and-percentage">
              <xsl:with-param name="class" select="'total'"/>
              <xsl:with-param name="value" select="sum(stats/block[@tagname!='class']/@size)"/>
            </xsl:call-template>
          </tr>
        </table>

        <h2 id="resources">Resources</h2>
        <p>These are "approximate" sizes of imported resources.  Some resources
        have been transcoded from their native format (e.g. JPEG) 
        to SWF.  The size of the resource when transcoded is reported here and it is 
        a small overestimate of the size taken up in the actual output
        program.</p>
        <table border="1">
          <tr>
            <th>Name</th>
            <?ignore <th>mime-type</th> ?>
            <th>Source</th> 
            <th>Size</th>
            <xsl:if test="$showpercent">
              <th>Percentage</th>
            </xsl:if>
          </tr>
          <xsl:apply-templates select="stats/swf/resource">
              <xsl:sort data-type="number" order="descending" select="@filesize"/>
          </xsl:apply-templates>
          <tr>
            <td><i>Total</i></td>
            <td/>
            <xsl:call-template name="size-and-percentage">
              <xsl:with-param name="class" select="'total'"/>
              <xsl:with-param name="value" select="sum(stats/swf/resource/@filesize)"/>
            </xsl:call-template>
          </tr>
        </table>

        <h2 id="fonts">Fonts</h2>
        <p>These are "approximate" sizes of imported fonts.  These assets
        have been transcoded from their native format (e.g. TTF) 
        to a SWF Font format.  This doesn't include additional font metrics tables
        added for use by the LFC.  These are underestimates.  Also,
        fonts that are imported by way of coming in inside SWF assets
        are ignored.</p>
        <table border="1">
          <tr>
            <th>Face</th>
            <th>Style</th>
            <th>Source</th>
            <th>Size</th>
            <xsl:if test="$showpercent">
              <th>Percentage</th>
            </xsl:if>
          </tr>
          <xsl:apply-templates select="stats/swf/font"/>
          <tr>
            <td><i>Total</i></td>
            <td/>
            <td/>
            <xsl:call-template name="size-and-percentage">
              <xsl:with-param name="class" select="'total'"/>
              <xsl:with-param name="value" select="sum(stats/swf/font/@filesize)"/>
            </xsl:call-template>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="include">
    <tr>
      <td><xsl:value-of select="@name"/></td>
      <td><xsl:value-of select="@explanation"/></td>
      <td align="right">
        <xsl:call-template name="decimal">
          <xsl:with-param name="value" select="@size"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template match="block">
    <tr>
      <td><xsl:value-of select="@pathname"/>:<xsl:value-of select="@lineno"/></td>
      <td><xsl:value-of select="@id"/> <xsl:value-of select="@name"/></td>
      <xsl:call-template name="size-and-percentage">
        <xsl:with-param name="value" select="@size"/>
      </xsl:call-template>
    </tr>
  </xsl:template>
  
  <xsl:template match="resource">
    <tr>
      <td><xsl:value-of select="@name"/></td>
      <?ignore <td><xsl:value-of select="@mime-type"/></td> ?>
      <td>
        <xsl:call-template name="print-pathname">
          <xsl:with-param name="pathname" select="@source"/>
        </xsl:call-template>
      </td>
      <xsl:call-template name="size-and-percentage">
        <xsl:with-param name="value" select="@filesize"/>
      </xsl:call-template>
    </tr>
  </xsl:template>

  <xsl:template name="print-pathname">
    <xsl:param name="pathname"/>
    <xsl:param name="src" select="/canvas/stats/resolve[@pathname=$pathname]"/>
    <xsl:choose>
      <xsl:when test="$src">
        <xsl:value-of select="$src/@src"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$src"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="font">
    <tr>
      <td><xsl:value-of select="@face"/></td>
      <td><xsl:value-of select="@style"/></td>
      <td>
        <xsl:call-template name="print-pathname">
          <xsl:with-param name="pathname" select="@source"/>
        </xsl:call-template>
      </td>
      <xsl:call-template name="size-and-percentage">
        <xsl:with-param name="value" select="@filesize"/>
      </xsl:call-template>
    </tr>
  </xsl:template>

  <xsl:template match="info">
    <xsl:param name="size" select="@size"/>
    <xsl:param name="gzsize" select="@gz-size"/>
    <!-- megabytes * 10 -->
    <xsl:param name="mb10" select="round($size * 10 div 1024 div 1024)"/>
    <xsl:param name="gzmb10" select="round($gzsize * 10 div 1024 div 1024)"/>


    <xsl:choose>
      <xsl:when test="$runtime = 'swf5'">       
        <div class="info">
          <table>
            <tr>
              <th align="left">Runtime Target: </th><td><xsl:value-of select="$runtime"/></td>
            </tr>
            <tr>
              <th align="left">Uncompressed:</th>
              <!-- print "ddK" or "d.dMB" -->
              <td>
                <xsl:choose>
                  <xsl:when test="$mb10 >= 9">
                    <xsl:value-of select="floor($mb10 div 10)"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of select="$mb10 mod 10"/>MB
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="round($size div 1024)"/>K
                  </xsl:otherwise>
                </xsl:choose>
                <!-- "(ddd,ddd bytes)" -->
                (<xsl:call-template name="decimal">
                <xsl:with-param name="value" select="$size"/>
                </xsl:call-template> bytes)
              </td>
            </tr>
            <xsl:if test="$showpercent">
            <tr>
              <th align="left">Compressed:</th>
              <td>
                <xsl:choose>
                  <xsl:when test="$gzmb10 >= 9">
                    <xsl:value-of select="floor($gzmb10 div 10)"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of select="$gzmb10 mod 10"/>MB
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="round($gzsize div 1024)"/>K
                  </xsl:otherwise>
                </xsl:choose>
                <!-- "(ddd,ddd bytes)" -->
                (<xsl:call-template name="decimal">
                <xsl:with-param name="value" select="$gzsize"/>
                </xsl:call-template> bytes)
              </td>
            </tr>
            </xsl:if>
            <tr>
              <th align="left">Encoding:</th>
              <td>
                <xsl:value-of select="@encoding"/>
                <xsl:if test="@encoding = ''">uncompressed</xsl:if>
              </td>
            </tr>
          </table>
        </div>
      </xsl:when>

      <xsl:otherwise>
        <div class="info">
          <table>
            <tr>
              <th align="left">Runtime Target: </th><td><xsl:value-of select="$runtime"/></td>
            </tr>
            <tr>
              <th align="left">Application Size (compressed):</th>
              <!-- print "ddK" or "d.dMB" -->
              <td>
                <xsl:choose>
                  <xsl:when test="$mb10 >= 9">
                    <xsl:value-of select="floor($mb10 div 10)"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of select="$mb10 mod 10"/>MB
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="round($size div 1024)"/>K
                  </xsl:otherwise>
                </xsl:choose>
                <!-- "(ddd,ddd bytes)" -->
                (<xsl:call-template name="decimal">
                <xsl:with-param name="value" select="$size"/>
                </xsl:call-template> bytes)
              </td>
            </tr>
          </table>
        </div>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>
  <xsl:template name="overview-row">
    <xsl:param name="name"/>
    <xsl:param name="href"/>
    <xsl:param name="size"/>
    <tr>
      <th align="left">
        <a href="{$href}"><xsl:value-of select="$name"/></a>
      </th>
      <xsl:call-template name="size-and-percentage">
        <xsl:with-param name="value" select="$size"/>
      </xsl:call-template>
    </tr>
  </xsl:template>
  
  <xsl:template name="size-and-percentage">
    <xsl:param name="class"/>
    <xsl:param name="value"/>
    <td align="right">
      <xsl:if test="$class">
        <xsl:attribute name="class"><xsl:value-of select="$class"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="decimal">
        <xsl:with-param name="value" select="$value"/>
      </xsl:call-template>
    </td>
    <xsl:if test="$showpercent">
      <td align="right">
        <xsl:if test="$class">
          <xsl:attribute name="class"><xsl:value-of select="$class"/></xsl:attribute>
        </xsl:if>
        <xsl:call-template name="percentage">
          <xsl:with-param name="value" select="$value"/>
        </xsl:call-template>
      </td>
    </xsl:if>
  </xsl:template>
  
  <!-- prints a decimal with interpolated commas -->
  <xsl:template name="decimal">
    <xsl:param name="value"/>
    <xsl:if test="$value != ''" >
      <xsl:number value="$value" grouping-separator="," grouping-size="3"/>
    </xsl:if>
  </xsl:template>

  <!-- print the number as d.dd% -->
  <xsl:template name="percentage">
    <xsl:param name="value"/>
    <!-- xsl:number rounds the value off, so don't use it -->
    <xsl:value-of select="floor($value * 10000 div $sum) div 100"/>
    <xsl:text>%</xsl:text>
  </xsl:template>

</xsl:stylesheet>
