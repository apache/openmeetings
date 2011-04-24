<?xml version="1.0" encoding="utf-8"?>

<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2008 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html"
              indent="yes"
              doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
              doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
  
  <xsl:template name="containerdiv">
    <xsl:param name="methodname" />
      <h2>Embedding applications in an existing HTML div</h2>
      <p>By default, applications are placed inside a new div with the id 'Container' appended to the app id, or '<code><xsl:value-of select="/canvas/@id"/>Container</code>' for the application above.  To place the application inside an existing div, you can specify the div's id as an argument to <code>lz.embed.<xsl:value-of select="$methodname"/>({... appenddivid: 'divid'})</code>.  Alternatively, you can change the id of an existing div or add a new one, e.g. '<code>&lt;div id="<xsl:value-of select="/canvas/@id"/>Container">...&lt;/div></code>'. For best results, please ensure the div you wish to append to appears before the call to <code>lz.embed.<xsl:value-of select="$methodname"/>()</code>.</p>
  </xsl:template>

  <xsl:template name="solodeployment">
    <xsl:param name="jspname" />
      <h2>SOLO Deployment</h2>
      There is a <a href='{canvas/request/@lps}/lps/admin/{$jspname}.jsp?appurl={canvas/request/@relurl}'>SOLO Deployment</a> tool on the OpenLaszlo server which can assist in packaging an application for standalone use.
  </xsl:template>

  <xsl:template name="disablehistory">
    <xsl:param name="methodname" />
      <p>To disable the history feature for an application, you can set the <code>history</code> argument to false, e.g. <code>lz.embed.<xsl:value-of select="$methodname"/>({... history: false}...)</code>.</p>
  </xsl:template>

  <xsl:template name="querystringoptions">
    <xsl:param name="methodname" />
       <p>Arguments passed into <code>lz.embed.<xsl:value-of select="$methodname"/>({...})</code> can be overridden by adding them to the query string when the app is loaded, with 'lz' prepended to the name.  Currently, the 'lzusemastersprite', 'lzskipchromeinstall', 'lzcancelkeyboardcontrol', 'lzcancelmousewheel', 'lzhistory', 'lzaccessible', 'lzapproot', 'lzserverroot' and 'lzwmode' properties are supported.  For example, to test this application with the mousewheel disabled, add <code>lzcancelmousewheel=true</code> to the app's URL, e.g <code><xsl:value-of select="/canvas/request/@url"/>?lzt=html<xsl:value-of select="/canvas/request/@query_args"/>&amp;lzcancelmousewheel=true</code>.  Please adjust your wrapper HTML to reflect these settings in <code>lz.embed.<xsl:value-of select="$methodname"/>({...})</code> when you get them the way you'd like.</p>
  </xsl:template>

  <xsl:template name="dhtmloptions">
      <p>If the HTML is located in a different directory than the
      lzx source file, the value of the 'url' parameter will need to
      be changed and the 'approot' parameter specified as a URL with a trailing slash, e.g.:</p>

<pre>
&lt;script type="text/javascript"&gt;
    lz.embed.dhtml({url: 'url/to/app/<xsl:value-of select="/canvas/request/@url"/>?lzt=object<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', approot: 'url/to/app/'});
    lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
        //Called when the application finishes loading
    }
&lt;/script></pre>

    <p>To prevent the application from grabbing mousewheel events and keyboard events for tab, arrow and enter keys, set the <code>cancelkeyboardcontrol</code> argument to true, e.g. <code>lz.embed.dhtml({... cancelkeyboardcontrol: true}...)</code>.</p>
    <p>To skip prompts to install the Google chrome frame in IE 6, set the <code>skipchromeinstall</code> argument to true, e.g. <code>lz.embed.dhtml({... skipchromeinstall: true}...)</code>.</p>
    <p>To use a single sprite resource when possible, set the <code>usemastersprite</code> argument to true, e.g. <code>lz.embed.dhtml({... usemastersprite: true}...)</code>.</p>
  </xsl:template>

    <xsl:template name="cancelmousewheel">
    <xsl:param name="methodname" />
      <p>To disable the mouse wheel for an application, you can set the <code>cancelmousewheel</code> argument to true, e.g. <code>lz.embed.<xsl:value-of select="$methodname"/>({... cancelmousewheel: true}...)</code>.</p>
  </xsl:template>

  <xsl:template name="exampledeployment">
      <p>Click <a href="{/canvas/request/@url}?lzt=html{/canvas/request/@query_args}">here</a> to see an example deployment page.</p>

      <h2>Optional arguments for embedding</h2>
      <p>Each runtime has optional arguments that can be specified when the application is embedded on the page.</p>
  </xsl:template>

  <xsl:template match="/">
<html>
  <head>
    <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico"/>
    <title>
      Deploying This Application
    </title>
    <style type="text/css">
      pre {border: 1px solid; background-color: #eef}
    </style>
  </head>
  <body>
      <h1>Deploying This Application</h1>
      
      <h2>Using the HTML Wrapper</h2>
      <p>The simplest way to deploy an application is to use the
      <code>?lzt=html</code> request type.  This generates an HTML page
      that uses the JavaScript embedding library to embed the
      application.  Unlike the development page (<code>?lzt=app_console</code>),
      it does not display the developer console or compiler
      warnings.</p>
      
      <p>View the result of the HTML deployment page <a
      href="{/canvas/request/@url}?lzt=html{/canvas/request/@query_args}">here</a>.
      (Note that this page is generated dynamically in the browser,
      adapting to the
      runtime choice and
      particular browser's capabilities, so the
      browser source view is not equivalent to the HTML deployment
      page source.)</p>
      
<xsl:choose><xsl:when test="/canvas/@runtime != 'dhtml'">

      <h2>Deployment with an Embedded <code>object</code> Tag</h2>
      <p>The application can also be embedded within a page that does
      not use the JavaScript library.  This can be a good choice if you
      expect many clients to have JavaScript disabled in their
      browser.</p>
      
      <p>View the HTML page with the embedded object tag <a
      href="{/canvas/request/@url}?lzt=html-object{/canvas/request/@query_args}">here</a>.
      (Note that while this page is generated dynamically by the
      server, adapting to the particular browser's capabilities, only
      Internet Explorer on Windows gets special treatment; the code
      generated for all other browsers is XHTML 1.0 compliant.  The
      code generated for Internet Explorer on Windows uses proprietary
      extensions to ensure that the correct version of the Flash
      plug-in is installed.  Internet Explorer on Windows can also
      display the XHTML 1.0 version, so if you need to embed your
      application into a larger static page, you should use the XHTML
      1.0 version.)</p>

      <p>To deploy using static XHTML 1.0 compliant code, place the
      following in your HTML document where the OpenLaszlo application
      should appear:</p>

      <pre>&lt;object type="application/x-shockwave-flash"
        data="<xsl:value-of select='canvas/request/@url' />?lzt=swf<xsl:value-of select='canvas/request/@query_args' />"
        width="<xsl:value-of select='canvas/@width' />" height="<xsl:value-of select='canvas/@height' />">
  &lt;param name="movie" value="<xsl:value-of select='canvas/request/@url' />?lzt=swf<xsl:value-of select='canvas/request/@query_args' />" />
  &lt;param name="quality" value="best" />
  &lt;param name="scale" value="noscale" />
  &lt;param name="salign" value="LT" />
  &lt;param name="menu" value="false" />
  &lt;param name="allowFullScreen" value="<xsl:value-of select='canvas/@allowfullscreen' />" />
&lt;/object></pre>
      
      <h2>Deployment with the <code>embed-compressed.js</code> Library</h2>
      <p>To deploy using the <code>embed-compressed.js</code> JavaScript library,
      place the following line within the <code>&lt;head&gt;</code>
      section of the HTML document that embeds the OpenLaszlo
      application:</p>
      
      <pre>&lt;script src="<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/embed-compressed.js" type="text/javascript">&lt;/script></pre>
      
      <p>Next, place the following element within the <code>&lt;body></code>
      section of the document, at the location where the Laszlo
      application should appear:</p>
      
      <pre>&lt;script type="text/javascript"&gt;
    lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', allowfullscreen: '<xsl:value-of select="/canvas/@allowfullscreen"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'});
&lt;/script></pre>

      <xsl:call-template name="exampledeployment"/>

      <xsl:call-template name="disablehistory"><xsl:with-param name="methodname" select="'swf'" /></xsl:call-template>

      <xsl:call-template name="cancelmousewheel"><xsl:with-param name="methodname" select="'swf'" /></xsl:call-template>

      <xsl:call-template name="containerdiv"><xsl:with-param name="methodname" select="'swf'" /></xsl:call-template>

      <!--p>UNSUPPORTED! You can also use the <code>js</code> request type to generate
      the call to <code>lz.embed.swf()</code>:</p>
      
      <pre>&lt;script src="<xsl:value-of select="/canvas/request/@url"/>?lzt=js" type="text/javascript"&gt;
&lt;/script></pre-->

      <h2>Specifying the version of Flash to be used</h2>
      <p>To upgrade the Flash player where necessary, specify a version number with the second argument to <code>lz.embed.swf(properties[, minimumVersion])</code>.  The default version number is 10, to help ensure users update their players for security reasons.  This example would always use Flash Player 11 or later: 
      <pre>lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'}, 11)</pre> 
      </p>

      <xsl:call-template name="solodeployment"><xsl:with-param name="jspname" select="'solo-deploy'" /></xsl:call-template>

<h2>Passing Parameters to SOLO applications</h2>
<p>
If you are deploying a SOLO application and wish to pass parameters down to the application from the browser location bar, you need to make some
 modifications to the stock html wrapper page that the server provides. 
</p>
<p>
Here is an example call to <code>lz.embed.swf()</code> that passes all of the query params down to the Laszlo app undamaged:</p>
<pre>
lz.embed.swf({url: 'main.lzx.swf9.swf?'+window.location.search.substring(1), bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'});
</pre>


</xsl:when><xsl:otherwise>

      <h2>Deployment with the <code>embed-compressed.js</code> Library</h2>
      <p>To deploy using the <code>embed-compressed.js</code> JavaScript library,
      place the following line within the <code>&lt;head&gt;</code>
      section of the HTML document that embeds the OpenLaszlo
      application:</p>
      
      <pre>&lt;script src="<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/embed-compressed.js" type="text/javascript">&lt;/script></pre>

      Next, you'll need to load a copy of the LFC inside the <code>&lt;head&gt;</code> section of the HTML document that embeds the application.  <code>lz.embed.lfc()</code> writes a  &lt;script/> tag into the document to load the LFC.  It expects two arguments: the URL for the LFC to be loaded, and the base URL to load resources from.  This only needs to be done once per page.  Note that these URLs may change for applications deployed using SOLO deployment.  For this application, you can use aa call like this: 
      <pre>&lt;script type="text/javascript"&gt;
    lz.embed.lfc('<xsl:value-of select="/canvas/request/@lps"/>/<xsl:value-of select="/canvas/@lfc"/>', '<xsl:value-of select="/canvas/request/@lps"/>/');
&lt;/script></pre>
      
      <p>Finally, place the following element within the <code>&lt;body></code>
      section of the document, at the location where the Laszlo
      application should appear:</p>
      
      <pre>
&lt;script type="text/javascript"&gt;
    lz.embed.dhtml({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=object<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>'});
    lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
        //Called when the application finishes loading
    }
&lt;/script></pre>

      <xsl:call-template name="exampledeployment"/>

      <xsl:call-template name="disablehistory"><xsl:with-param name="methodname" select="'dhtml'" /></xsl:call-template>

      <xsl:call-template name="cancelmousewheel"><xsl:with-param name="methodname" select="'dhtml'" /></xsl:call-template>

      <xsl:call-template name="dhtmloptions"/>

      <xsl:call-template name="querystringoptions"><xsl:with-param name="methodname" select="'dhtml'" /></xsl:call-template>

      <xsl:call-template name="containerdiv"><xsl:with-param name="methodname" select="'dhtml'" /></xsl:call-template>

      <xsl:call-template name="solodeployment"><xsl:with-param name="jspname" select="'solo-dhtml-deploy'" /></xsl:call-template>

<h2>Passing Parameters to SOLO applications</h2>
<p>
If you are deploying a SOLO application and wish to pass parameters down to the application from the browser location bar, you need to make some
 modifications to the stock html wrapper page that the server provides. 
</p>
<p>
Here is an example call to <code>lz.embed.dhtml()</code> that passes all of the query params down to the Laszlo app undamaged:</p>
<pre>
lz.embed.dhtml({url: 'main.lzx.js?'+window.location.search.substring(1), bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'});
</pre>
</xsl:otherwise></xsl:choose>

      <h2>Accessing applications from browser JavaScript</h2>
      <p>Each application reserves its own place inside <code>lz.embed</code>, depending on the id passed for <code>lz.embed.swf(...)</code> or <code>lz.embed.dhtml(...)</code>.  In addition, <code>lz.embed.applications</code> keeps track of all applications embedded on the page by id.  For example, an application embedded with <code>id: 'foo'</code> can be accessed at <code>lz.embed.foo</code> or <code>lz.embed.applications.foo</code>.  This application can be accessed at <code>lz.embed.<xsl:value-of select="/canvas/@id"/></code> or <code>lz.embed.applications.<xsl:value-of select="/canvas/@id"/></code>.</p> 

      <p>Before accessing an application, you can check the 'loaded' property to make sure it's set to <code>true</code>: 
      <pre>lz.embed.<xsl:value-of select="/canvas/@id"/>.loaded</pre>
      </p>

      <p>To find out when an application is loaded and ready to be called, use: 
      <pre>
lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
    ...
}
      </pre>
      </p>

      <p>To update the page with load progress information, use: 
      <pre>
lz.embed.<xsl:value-of select="/canvas/@id"/>.onloadstatus = function loadstatus(p) {
    // called with a percentage (0-100) indicating load progress
}
      </pre>
      </p>

      <p>To read a canvas attribute in an application, use: 
      <pre>value = lz.embed.<xsl:value-of select="/canvas/@id"/>.getCanvasAttribute('attributename')</pre></p>
      
      <p>To set a canvas attribute in an application, use: 
      <pre>lz.embed.<xsl:value-of select="/canvas/@id"/>.setCanvasAttribute('attributename', value)</pre></p>

      <p>To set canvas attributes for all applications on the page, use:
      <pre>lz.embed.setCanvasAttribute('attributename', value[, history])</pre>
      If the optional <code>history</code> argument is <code>true</code> the browser's history mechanism will track the call to setCanvasAttribute(), and will reset the canvas attribute value when the browser's forward or back buttons are pressed.</p>
     
      <p>To call a method in an application, use: 
      <pre>value = lz.embed.<xsl:value-of select="/canvas/@id"/>.callMethod('globalreference.reference.anyMethod(...)')</pre> 
      passing a string representation of the method and any arguments you wish to pass.  You can call any available method in the application as long as '<code>globalreference</code>' can be found in the global scope.</p>

      <p>To call a method in all applications on the page, use:
      <pre>lz.embed.callMethod('globalreference.reference.anyMethod(...)')</pre> 
      </p>
      
      <h2>More Information</h2>
      <ul>
        <li><a href="{/canvas/request/@lps}/docs/deployers/">System Administrator's Guide to Deploying OpenLaszlo Applications</a></li>
        <li><a href="{/canvas/request/@lps}/docs/developers/">Software Engineer's Guide to Developing OpenLaszlo Applications</a></li>
        <li><a href="http://forum.openlaszlo.org/">Developer Forums</a></li>
      </ul>
  </body>
</html>
  </xsl:template>
</xsl:stylesheet>
