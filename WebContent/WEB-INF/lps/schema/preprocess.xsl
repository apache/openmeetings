<?xml version="1.0" encoding="utf-8"?>
<!--=======================================================================-->
<!--                                                                       -->
<!-- preprocess.xsl                                                        -->
<!--                                                                       -->
<!-- LZX preprocessor                                                      -->
<!--                                                                       -->
<!--=======================================================================-->

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lzx="http://www.laszlosystems.com/2003/05/lzx"
  xmlns:exslt="http://exslt.org/common"
  exclude-result-prefixes="exslt lzx"
  version="1.0">
  
  <xsl:output method="xml"/>
  
  <xsl:template match="/">
    <!-- phase 1: update the namespace -->
    <!--xsl:param name="phase1"-->
    <xsl:apply-templates mode="update-ns"/>
    <!--/xsl:param-->
    <!-- phase 2: promote toplevel library/* elements -->
    <!--xsl:apply-templates mode="phase2" select="exslt:node-set($phase1)"/-->
  </xsl:template>
  
  <!-- default copy rule -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!--
  phase 1: add or update the namespace
  -->
  
  <!-- Add a namespace to elements without one. -->
  <xsl:template mode="update-ns" match="*[namespace-uri()='']">
    <xsl:element
      name="{name()}"
      namespace="http://www.laszlosystems.com/2003/05/lzx">
      <xsl:apply-templates mode="update-ns" select="@*|*|node()"/>
    </xsl:element>
  </xsl:template>
  
  <!-- Update elements with the LPS 1.0 namespace to the current namespace -->
  <xsl:template mode="update-ns" match="*[
                namespace-uri()='http://www.laszlosystems.com/2003/01/lzx']">
    <xsl:element
      name="{name()}"
      namespace="http://www.laszlosystems.com/2003/05/lzx">
      <xsl:apply-templates mode="update-ns" select="@*|*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template mode="update-ns" match="/|@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates mode="update-ns" select="node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!--
    phase 2: move toplevel elements from library/* to canvas/*
    phase2-toplevel applies to each toplevel element
    phase2-promote
    phase2-inner
  -->
  <xsl:template mode="phase2" match="/">
    <xsl:copy>
      <xsl:apply-templates mode="phase2-toplevel" select="*"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template mode="phase2-toplevel" match="lzx:canvas|lzx:library">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates mode="phase2-toplevel" select="node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template mode="phase2-toplevel" match="@*|node()">
    <xsl:apply-templates mode="phase2-promote" select="*"/>
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates mode="phase2-inner" select="node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template mode="phase2-promote" match="*" priority="5">
    <xsl:param name="toplevel-only">
      <xsl:apply-templates mode="toplevel-only" select="."/>
    </xsl:param>
    <xsl:choose>
      <xsl:when test="normalize-space($toplevel-only)='true'">
        <xsl:copy-of select="."/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates mode="phase2-promote" select="*"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template mode="phase2-inner" match="lzx:library[not(@href)]">
    <xsl:apply-templates mode="phase2-inner" select="node()"/>
  </xsl:template>
  
  <xsl:template mode="phase2-inner" match="@*|node()">
    <xsl:param name="toplevel-only">
      <xsl:apply-templates mode="toplevel-only" select="."/>
    </xsl:param>
    <xsl:if test="normalize-space($toplevel-only)!='true'">
      <xsl:copy>
        <xsl:apply-templates select="@*"/>
        <xsl:apply-templates mode="phase2-inner" select="node()"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  
  <xsl:template mode="toplevel-only" match="lzx:library/*[
                contains(' class connectiondatasource dataset datasource javadatasource resource ',
                concat(' ', name(), ' '))
                ]">
    true
  </xsl:template>

  <xsl:template mode="toplevel-only" match="lzx:library/lzx:library[@href]">
    true
  </xsl:template>
  
  <xsl:template mode="toplevel-only" match="*">
    false
  </xsl:template>
  
</xsl:stylesheet>
