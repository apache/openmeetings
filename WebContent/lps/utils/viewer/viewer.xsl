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

  <xsl:param name="fname"/>
  <xsl:param name="base"/>
  <xsl:param name="url"/>
  <xsl:param name="urlbase"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" href="styles.css" type="text/css"/>
        <link rel="alternate stylesheet" title="Methods and Attributes"
              href="methods-only.css" type="text/css"/>
        <link rel="alternate stylesheet" title="No Methods or Attributes"
              href="no-methods.css" type="text/css"/>
        <title><xsl:value-of select="$fname"/></title>
      </head>
      <body>
        <h1><xsl:value-of select="$fname"/></h1>
        <pre>
          <xsl:apply-templates/>
        </pre>
        <div class="references">
          <h2>Cross References</h2>
          <xsl:if test="//include/@href">
            <h3>Includes</h3>
            <ul class="nl">
              <xsl:for-each select="//include/@href">
                <xsl:sort select="../@href"/>
                <li><xsl:apply-templates select="../@href"/></li>
              </xsl:for-each>
            </ul>
          </xsl:if>
          <xsl:if test="//resource">
            <h3>Resources</h3>
            <table border="0">
              <tr><th>Name</th><th>Source</th><th>Image</th></tr>
              <xsl:for-each select="//resource">
                <xsl:if test="@src">
                  <tr valign="top">
                    <td valign="top"><a href="#{generate-id()}"><xsl:value-of select="@name"/></a><a href="#{generate-id(..)}"><xsl:value-of select="../@name"/></a></td>
                    <td><a href="{$base}{@src}"><xsl:value-of select="@src"/></a></td>
                    <td><xsl:apply-templates select="@src" mode="imagelist"/></td>
                  </tr>
                </xsl:if>
                <xsl:for-each select="frame">
                  <tr valign="top">
                    <td valign="top">
                      <xsl:if test="position() = 1">
                        <a href="#{generate-id(..)}"><xsl:value-of select="../@name"/></a>
                      </xsl:if>
                    </td>
                    <td><a href="{$base}{@src}"><xsl:value-of select="@src"/></a></td>
                    <td valign="top"><xsl:apply-templates select="@src" mode="imagelist"/></td>
                  </tr>
                </xsl:for-each>
              </xsl:for-each>
            </table>
          </xsl:if>
          <xsl:if test="/*/class">
            <h3>Classes</h3>
            <ul class="nl">
              <xsl:for-each select="/*/class">
                <li>
                  <xsl:text>&lt;</xsl:text>
                  <xsl:value-of select="name()"/> name="<a href="#{generate-id()}"><xsl:value-of select="@name"/></a><xsl:text>"</xsl:text>
                  <xsl:if test="@extends">
                    <xsl:text> extends="</xsl:text>
                    <xsl:value-of select="@extends"/>
                    <xsl:text>"</xsl:text>
                  </xsl:if>
                  <xsl:text>&gt;</xsl:text>
                </li>
                <xsl:text> </xsl:text>
              </xsl:for-each>
            </ul>
          </xsl:if>
          <xsl:if test="//*[@id] or /*/*[@name and name()!='font' and name()!='class' and name()!='resource']">
            <h3>Named Instances</h3>
            <ul class="nl">
              <xsl:for-each select="//*[@id]">
                <xsl:sort select="name()"/>
                <xsl:sort select="@id"/>
                <li>&lt;<xsl:value-of select="name()"/> id="<a href="#{generate-id()}"><xsl:value-of select="@id"/></a>"&gt;</li>
                <xsl:text> </xsl:text>
              </xsl:for-each>
              <xsl:for-each select="/*/*[@name and name()!='font' and name()!='class' and name()!='resource']">
                <xsl:sort select="name()"/>
                <xsl:sort select="@name"/>
                <li>&lt;<xsl:value-of select="name()"/> name="<a href="#{generate-id()}"><xsl:value-of select="@name"/></a>"&gt;</li>
                <xsl:text> </xsl:text>
              </xsl:for-each>
            </ul>
          </xsl:if>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template priority="0" match="node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template priority="1" name="element" match="*">
    <span class="element">
      <span class="markup"><xsl:text>&lt;</xsl:text></span>
      <span class="starttag">
        <xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
        <xsl:value-of select="name()"/>
      </span>
      <xsl:for-each select="@*">
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="."/>
      </xsl:for-each>
      <xsl:choose>
        <xsl:when test="node()">
          <span class="markup"><xsl:text>&gt;</xsl:text></span>
          <xsl:apply-templates/>
          <span class="markup"><xsl:text>&lt;/</xsl:text></span>
          <span class="endtag"><xsl:value-of select="name()"/></span>
          <span class="markup"><xsl:text>&gt;</xsl:text></span>
        </xsl:when>
        <xsl:otherwise><span class="markup">/&gt;</span></xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>

  <xsl:template priority="4" match="comment()[local-name(following-sibling::*)='attribute' or local-name(following-sibling::*)='method']">
    <span class="{local-name(following-sibling::*)}-element-comment">
      <xsl:call-template name="comment"/>
    </span>
  </xsl:template>

  <xsl:template priority="2" match="text()[normalize-space()='' and (local-name(following-sibling::*)='attribute' or local-name(following-sibling::*)='method')]">
    <span class="{local-name(following-sibling::*)}-element-text">
      <xsl:call-template name="text"/>
    </span>
  </xsl:template>

  <xsl:template match="attribute|method">
    <span class="{local-name()}-element">
      <xsl:call-template name="element"/>
    </span>
  </xsl:template>

  <xsl:template match="@*">
    <span class="attrname"><xsl:value-of select="name()"/></span>
    <xsl:text>="</xsl:text>
    <span class="attrvalue">
      <xsl:apply-templates mode="attribute-value" select="."/>
    </span>
    <xsl:text>"</xsl:text>
  </xsl:template>

  <!--
    Special display for attribute values
  -->

  <xsl:template mode="attribute-value" match="@*">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template mode="attribute-value" match="@*[
                starts-with(normalize-space(.),'${') or
                starts-with(normalize-space(.),'$once{') or
                starts-with(normalize-space(.),'$path{') or
                starts-with(normalize-space(.),'$immediately{')]" priority="1">
    <span class="constraint-markup">
      <xsl:value-of select="substring-before(., '{')"/>
      <xsl:text>{</xsl:text>
    </span>
    <xsl:variable name="after" select="substring-after(., '{')"/>
    <span class="constraint-expression">
      <xsl:value-of select="substring($after, 1, string-length($after)-1)"/>
    </span>
    <span class="constraint-markup">}</span>
  </xsl:template>

  <xsl:template mode="attribute-value" match="@id">
    <span class="name"><xsl:value-of select="."/></span>
  </xsl:template>

  <xsl:template mode="attribute-value" match="@name">
    <span class="name"><xsl:value-of select="."/></span>
  </xsl:template>

  <xsl:template mode="attribute-value" match="include/@href">
    <a href="{$url}{.}"><xsl:value-of select="."/></a>
  </xsl:template>

  <xsl:template mode="attribute-value" match="resource/@src
                |resource/frame/@src">
    <a href="{$base}{.}"><xsl:value-of select="."/></a>
  </xsl:template>

  <xsl:template name="text" match="text()">
    <xsl:param name="quote" select="contains(., '&lt;') or contains(., '&gt;') or contains(., '&amp;')"/>
    <code>
      <xsl:if test="$quote">
        <span class="markup"><xsl:text>&lt;![CDATA[</xsl:text></span>
      </xsl:if>
      <xsl:value-of select="."/>
      <xsl:if test="$quote">
        <span class="markup"><xsl:text>]</xsl:text><xsl:text>]></xsl:text></span>
      </xsl:if>
    </code>
  </xsl:template>

  <xsl:template mode="attribute-value" match="attribute/@value">
    <code><xsl:value-of select="."/></code>
  </xsl:template>

  <!--xsl:template match="@bgcolor">
    <xsl:value-of select="."/>
    <span style="background-color: {.}">.</span>
  </xsl:template-->

  <xsl:template priority="3" name="comment" match="comment()">
    <xsl:if test="not(ancestor::*)">
      <xsl:text>&#10;</xsl:text>
    </xsl:if>

    <span class="comment-markup"><xsl:text>&lt;!--</xsl:text></span>
    <span class="comment"><xsl:value-of select="."/></span>
    <span class="comment-markup"><xsl:text>--&gt;</xsl:text></span>

    <xsl:if test="not(ancestor::*) and following-sibling::*[1]=following-sibling::node()[1]">
      <xsl:text>&#10;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="//resource/@src|//resource/frame/@src" mode="imagelist">
    <xsl:choose>
      <xsl:when test="substring(., string-length()-3, string-length()) = '.swf'">
        <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,79,0"
                width="100" height="100">
          <param name="movie" value="{$base}{.}"/>
          <param name="quality" value="high"/>
          <param name="bgcolor" value="#FFFFFF"/>
          <param name="align" value="t1"/>
          <embed src="{$base}{.}"
            quality="high"
            bgcolor="#FFFFFF"
            width="100" height="100"
            salign="tl"
            type="application/x-shockwave-flash"
            pluginspage="http://www.macromedia.com/go/getflashplayer">
          </embed>
        </object>
      </xsl:when>
      <xsl:otherwise>
        <img src="{$base}{.}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2006 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
