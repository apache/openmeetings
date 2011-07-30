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

  <xsl:param name="lps"><xsl:value-of select="/*/request/@lps"/></xsl:param>
  <xsl:param name="lfc"><xsl:value-of select="/*/request/@lfc"/></xsl:param>
  <xsl:param name="debug"><xsl:value-of select="(/*/info/@debug = 'true') or (/canvas/@debug = 'true')"/></xsl:param>
  <xsl:param name="lzruntime" select="/*/@runtime"/>
  <xsl:param name="runtimes" select="/canvas/@runtimes"/>
  <xsl:param name="proxied" select="/canvas/@proxied"/>
  <xsl:param name="appinfo"><xsl:value-of select="/*/console_appinfo/text()"/></xsl:param>
  <xsl:param name="assets"><xsl:value-of select="/*/request/@lps"/>/lps/assets</xsl:param>
  <xsl:param name="console-floating-window"><xsl:value-of select="/*/request/@console-floating-window"/></xsl:param>


  <!-- console will be 70 high, unless the remote console debugger is on, in which case
       it will be 370 -->
  <xsl:param name="consoleheight"><xsl:choose><xsl:when test="/*/request/@console-remote-debug = 'true'">370</xsl:when><xsl:otherwise>70</xsl:otherwise> </xsl:choose></xsl:param>

  <xsl:param name="consolefooter"><xsl:choose><xsl:when test="/*/request/@console-remote-debug = 'true'">console-large</xsl:when><xsl:otherwise>console</xsl:otherwise></xsl:choose></xsl:param>

  <!--  <xsl:param name="appuid"><xsl:value-of select="/*/request/@appuid"/></xsl:param> -->
  <!-- we'll use the app name for now, to make it easier to point a remote debugger at it -->
  <xsl:param name="appuid"><xsl:value-of select="/*/request/@url"/></xsl:param>

  <xsl:param name="opturl" select="/*/request/@opt-url"/>
  <xsl:param name="unopturl" select="/*/request/@unopt-url"/>

  <!-- shared body template for embedding -->
  <xsl:include href="embed-body_jp.xslt"/>

  <!--
      In standards mode, your dimensions must have explicit units
  -->
  <xsl:template name="dimension">
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

  <xsl:param name="canvasheight"><xsl:call-template name="dimension"><xsl:with-param name="value" select="/canvas/@height" /></xsl:call-template></xsl:param>
  <xsl:param name="canvaswidth"><xsl:call-template name="dimension"><xsl:with-param name="value" select="/canvas/@width" /></xsl:call-template></xsl:param>

  <xsl:template match="/">
    <html>
      <head>
        <xsl:copy-of select="/canvas/wrapperheaders/node()"/> 
        <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
        <link rel="stylesheet" href="{$lps}/lps/includes/console.css" type="text/css"/>
        <xsl:comment>[if IE]&gt;
        &lt;style type="text/css"&gt;
            /* Fix IE scrollbar braindeath */
            html { overflow: auto; overflow-x: hidden; }
        &lt;/style&gt;
        &lt;![endif]</xsl:comment>
        <title>
          <xsl:choose>
            <xsl:when test="canvas">
              <xsl:value-of select="canvas/@title"/>
            </xsl:when>
            <xsl:otherwise>
              コンパイルエラー
            </xsl:otherwise>
          </xsl:choose>
        </title>
        <script src="{/canvas/request/@lps}/lps/includes/embed-compressed.js" type="text/javascript"/>
        <xsl:choose>
          <xsl:when test="/canvas/@runtime = 'dhtml'">
            <xsl:comment>[if lt IE 9]&gt;&lt;script type="text/javascript" src="<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/excanvas.js"&gt;&lt;/script&gt;&lt;![endif]</xsl:comment>
          </xsl:when>
        </xsl:choose>
      </head>
      <body>
        <xsl:if test="/canvas/warnings">
          <div id="warnings">
            <a href="#warnings">確認</a>
            <xsl:text> </xsl:text>
            <xsl:value-of select="count(/canvas/warnings/error)"/>
            <xsl:text> コンパイルワーニング</xsl:text>
            <xsl:if test="count(/canvas/warnings/error) > 1"></xsl:if>.
          </div>
        </xsl:if>
        <xsl:apply-templates select="canvas|errors"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="canvas">
    <xsl:call-template name="body"/>
    <xsl:call-template name="footer"/>
  </xsl:template>

  <xsl:template name="footer">
    <xsl:param name="url" select="request/@url"/>
    <xsl:param name="query_args" select="request/@query_args"/>
    <xsl:if test="not(//param[@name='showTaskBar'])">
      <div id="footer" style="position: relative">
        <xsl:call-template name="tasks"/>
        <xsl:apply-templates select="info"/>
      </div>
    </xsl:if>
    <xsl:apply-templates select="warnings"/>

  </xsl:template>

  <xsl:template name="tasks">
    <xsl:param name="url" select="request/@url"/>
    <xsl:param name="query_args" select="request/@query_args"/>

    <div id="{$consolefooter}">
      <!-- an embedded SOLO console app to replace the HTML console -->
      <iframe src="{$lps}/lps/admin/dev-console.html?lzr={$lzruntime}&amp;lzappuid={$appuid}&amp;appinfo={$appinfo}&amp;runtimes={$runtimes}&amp;appproxied={$proxied}"
              style="width: 100%; height: {$consoleheight}px; border: 0 none;"
              width="100%" height="{$consoleheight}" frameborder="0"/>

      <!-- pop up console debugger window -->
      <!--
          <script>
          CWin = window.open('<xsl:value-of select="$lps"/>/lps/admin/dev-console-popup.html?lzappuid=<xsl:value-of select="$appuid"/>&amp;lzt=swf&amp;appinfo=<xsl:value-of select="$appinfo"/>', "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=1,copyhistory=0,width=1250,height=400");
          </script>
      -->
    </div>
  </xsl:template>

  <xsl:template match="info">
    <xsl:param name="url" select="../request/@url"/>
    <xsl:param name="query_args" select="../request/@query_args"/>
    <xsl:param name="size" select="@size"/>
    <xsl:param name="gzsize" select="@gzsize"/>
    <xsl:param name="lfcsize" select="@lfcsize"/>
    <xsl:param name="gzlfcsize" select="@gzlfcsize"/>
    <xsl:param name="totalsize" select="$size + $lfcsize"/>
    <xsl:param name="gztotalsize" select="$gzsize + $gzlfcsize"/>
    <xsl:param name="runtime" select="@runtime"/>
    <!-- megabytes * 10 -->
    <xsl:param name="mb10" select="round($size * 10 div 1024 div 1024)"/>

    <div class="info">
      <b>ターゲットランタイム: </b> <xsl:value-of select="$runtime"/><br/>
      <xsl:choose>
        <xsl:when test="@runtime = 'dhtml'">
            <b>合計サイズ: </b>
            <b><xsl:value-of select="round($gztotalsize div 1024)"/>K</b>
            (<xsl:value-of select="round($totalsize div 1024)"/>K 非圧縮)
            <b> = </b>
            <b><xsl:value-of select="round($gzsize div 1024)"/>K</b>
            (<xsl:value-of select="round($size div 1024)"/>K 非圧縮) アプリケーション
            <b> + </b>
            <b><xsl:value-of select="round($gzlfcsize div 1024)"/>K</b>
            (<xsl:value-of select="round($lfcsize div 1024)"/>K 非圧縮) LFC
        </xsl:when>
        <xsl:otherwise>
            <b>アプリケーションサイズ: </b>
            <!-- print "ddK" or "d.dMB" -->
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
            <a target="_blank"
                href="{$url}?lzt=info{$query_args}">サイズ情報</a>
        </xsl:otherwise>
      </xsl:choose>
      <br/>
      <a href="{$url}?lzt=html{$query_args}">Show application only</a>
    </div>
  </xsl:template>

  <!-- prints a decimal with interpolated commas -->
  <xsl:template name="decimal">
    <xsl:param name="value"/>
    <xsl:param name="remainder" select="$value mod 1000"/>
    <xsl:if test="$value &gt; 1000">
      <xsl:call-template name="decimal">
        <xsl:with-param name="value" select="floor($value div 1000)"/>
      </xsl:call-template>
      <xsl:text>,</xsl:text>
      <xsl:if test="$remainder &lt; 100">0</xsl:if>
      <xsl:if test="$remainder &lt; 10">0</xsl:if>
    </xsl:if>
    <xsl:value-of select="$remainder"/>
  </xsl:template>

  <xsl:template match="canvas/warnings">
    <div id="warnings">
      <h2>コンパイルワーニング</h2>
      <pre class="warning">
        <xsl:for-each select="error">
          <xsl:if test="position() > 1">
            <br/>
          </xsl:if>
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </pre>
    </div>
  </xsl:template>

  <xsl:template match="error">
    <xsl:choose>
      <xsl:when test="starts-with(text(), '.tmp_')">
        <xsl:value-of select="substring-after(substring-after(text(), '_'), '_')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="text()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="error_duplicate">
    <xsl:call-template name="str.subst">
      <xsl:with-param name="str" select="." />
      <xsl:with-param name="from" select="'&#xA;'" />
      <xsl:with-param name="to"><br/></xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="/errors">
    <div style="border-top: 1px solid; border-bottom: 1px solid; background-color: #fcc; padding: 1pt; padding-left: 2pt">
      このアプリケーションは次のエラーによりコンパイルできませんでした。:
    </div>
    <h1>コンパイルエラー</h1>
    <code class="error">
      <xsl:apply-templates select="error"/>
    </code>
    <xsl:if test="error/error">
      <div id="warnings">
        <h2>コンパイルワーニング</h2>
        <pre class="warning">
          <xsl:for-each select="error/error">
            <xsl:if test="position() > 1">
              <br/>
            </xsl:if>
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </pre>
      </div>
    </xsl:if>

    <xsl:call-template name="footer"/>
  </xsl:template>

  <xsl:template name="str.subst">
    <xsl:param name="str" />
    <xsl:param name="from" />
    <xsl:param name="to"><br /></xsl:param>
    <xsl:choose>
      <xsl:when test="contains($str, $from)">
        <xsl:value-of select="substring-before($str, $from)" />
        <xsl:copy-of select="$to" />
        <xsl:call-template name="str.subst">
          <xsl:with-param name="str" select="substring-after($str, $from)" />
          <xsl:with-param name="from" select="$from" />
          <xsl:with-param name="to" select="$to" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$str" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2006, 2008, 2009, 2010 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

