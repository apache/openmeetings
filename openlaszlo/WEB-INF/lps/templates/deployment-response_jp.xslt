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
      <p>デフォルトでアプリケーションはアプリidに'Container'を付加された id 具体的には上記のアプリケーションの例では'<code><xsl:value-of select="/canvas/@id"/>Container</code>'のidを持つ新規のdivの内部に置かれます。アプリケーションを既存のdiv内に置くには、divのidを<code>lz.embed.<xsl:value-of select="$methodname"/>({... appenddivid: 'divid'})</code>の引数とすることも出来ます。別の方法としては既存divのidを変更するか、例えば'<code>&lt;div id="<xsl:value-of select="/canvas/@id"/>Container">...&lt;/div></code>'のように新規のidを追加することも出来ます。</p>
  </xsl:template>

  <xsl:template name="solodeployment">
    <xsl:param name="jspname" />
      <h2>SOLO配置ウィザード</h2>
      OpenLaszloサーバーには、SOLO配置用にアプリケーションのパッケージ化を支援する<a href='{canvas/request/@lps}/lps/admin/{$jspname}.jsp?appurl={canvas/request/@relurl}'>SOLO配置用ウィザード</a>アプリケーションが用意されています。
  </xsl:template>

  <xsl:template name="disablehistory">
    <xsl:param name="methodname" />
      <p>アプリケーションのヒストリー機能をディスエーブルとしたいときは<code>history</code> 引数をfalseに設定します。例えば<code>lz.embed.<xsl:value-of select="$methodname"/>({... history: false}...)</code>のように。</p>
  </xsl:template>

  <xsl:template name="exampledeployment">
      <p> サンプルのデプロイ結果を見るページは <a href="{/canvas/request/@url}?lzt=html{/canvas/request/@query_args}">こちらです。</a> </p>
      
      <p>もしHTMLがLZXソースファイルとは別のディレクトリに置かれているならば、
      'url'パラメータの変更が必要となるでしょう。</p>
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
      <h1>このアプリケーションの配置</h1>
      
      <h2>HTMLラッパーを利用</h2>
      <p>アプリケーションの配置に一番簡単な方法は 
      <code>?lzt=html</code> リクエストを使うことです。
      こうすることでJavaScriptライブラリを使いアプリケーションを埋め込んだHTMLを作成します。
      開発用ページ(<code>?lzt=app_console</code>)とは違い、デベロッパーコンソールとコンパイラ・ワーニングを表示しません。</p>
      
      <p>HTML配置ページ結果のプレビューは <a
      href="{/canvas/request/@url}?lzt=html{/canvas/request/@query_args}">こちら</a>です。
      (このページはブラウザ内で動的に作成されており、各種ブラウザに対応します。ブラウザからのソースビューとHTML配置ページ用のページソースとは異なります。)</p>
      
<xsl:choose><xsl:when test="/canvas/@runtime != 'dhtml'">

      <h2>埋め込み<code>オブジェクト</code>タグによる配置</h2>
      <p>JavaScriptライブラリを使わなくても、アプリケーションをページ内に埋め込むことができます。
      もしほとんどの対象クライアントがブラウザのJavaScriptを無効にしていることが予想されるなら、この方法がよい選択となります。</p>
      
      <p>埋め込みオブジェクトタグによるHTMLページは<a
      href="{/canvas/request/@url}?lzt=html-object{/canvas/request/@query_args}">こちら</a>.
      (このページはサーバーによって動的に作成されており、各種ブラウザに対応します。
      すべてのブラウザ向けに生成されたコードはXHTML1.0に対応していますが、Windows版Internet Explorerのみ特別な扱いを受けます。
Windows版Internet Explorer向けに生成されたコードは、正しいFlashプラグインバージョンがインストールされているか確認する拡張コードが埋め込まれています。そのため、アプリケーションを大きな静的ページの中に埋め込む必要がある場合は、XHTML1.0バージョンを利用してください。)</p>

      <p>静的XHTML1.0仕様のコードを使って配置するには、Laszloアプリケーションを表示させたいHTMLドキュメント内に以下のコードを貼り付けてください。</p>

      <pre>&lt;object type="application/x-shockwave-flash"
        data="<xsl:value-of select='canvas/request/@url' />?lzt=swf<xsl:value-of select='canvas/request/@query_args' />"
        width="<xsl:value-of select='canvas/@width' />" height="<xsl:value-of select='canvas/@height' />">
  &lt;param name="movie" value="<xsl:value-of select='canvas/request/@url' />?lzt=swf<xsl:value-of select='canvas/request/@query_args' />" />
  &lt;param name="quality" value="best" />
  &lt;param name="scale" value="noscale" />
  &lt;param name="salign" value="LT" />
  &lt;param name="menu" value="false" />
&lt;/object></pre>
      
      <h2><code>embed-compressed.js</code> JavaScriptライブラリを利用した配置</h2>
      <p><code>embed-compressed.js</code>JavaScriptライブラリを利用して配置するには、次の行をLaszloアプリケーションを貼り付けるHTMLドキュメントの<code>&lt;head&gt;</code>セクションに貼り付けます。</p>
      
      <pre>&lt;script src="<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/embed-compressed.js" type="text/javascript">&lt;/script></pre>
      
      <p>次に次のコードを<code>&lt;body></code>セクション内のLaszloアプリケーションを表示したい場所へ貼り付けます。</p>
      
      <pre>&lt;script type="text/javascript"&gt;
    lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'});
&lt;/script></pre>

      <xsl:call-template name="exampledeployment"/>
      
      <xsl:call-template name="disablehistory"><xsl:with-param name="methodname" select="'swf'" /></xsl:call-template>

      <xsl:call-template name="containerdiv"><xsl:with-param name="methodname" select="'swf'" /></xsl:call-template>

      <p><code>lz.embed.swf()</code>を呼ぶコードを生成するよう<code>js</code>リクエストタイプを使うこともできます。 </p>
      
      <pre>&lt;script src="<xsl:value-of select="/canvas/request/@url"/>?lzt=js" type="text/javascript"&gt;
&lt;/script></pre>

      <p>必要とあれば<code>lz.embed.swf(properties[, minimumVersion])</code>の2番目の引数にバージョンナンバーを指定してFlashプレーヤをアップグレードします。デフォルトのバージョンナンバーは 7 です。現在Safariでは 8 である必要があります。このサンプルではFlashプレーヤ 8 もしくはそれ以降を使用します。
      <pre>lz.embed.swf({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=swf<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'}, 8)</pre> 
      </p>

<h2>SOLOアプリケーションへパラメータを渡す</h2>
<p>
配置したSOLOアプリケーションへ、パラメータをアプリケーションに渡したい場合はサーバーが生成したHTMLラッパーページの修正が必要となります。 
</p>
<p>
次の<code>lz.embed.swf()</code>行はすべてのクエリパラメータをLaszloアプリケーションへ渡しています。</p>
<pre>
lz.embed.swf({url: 'main.lzx.lzr=swf7.swf?'+window.location.search.substring(1), bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>', accessible: '<xsl:value-of select="/canvas/@accessible"/>'});
</pre>
<p>
<code>main.lzx.lzr=swf7.swf? </code>から<code>main.lzx?lzt=swf</code>への変更点は
<code>'+window.location.search.substring(1)'</code>が追加されていることです。
</p>

      <xsl:call-template name="solodeployment"><xsl:with-param name="jspname" select="'solo-deploy'" /></xsl:call-template>

</xsl:when><xsl:otherwise>

      <h2><code>embed-compressed.js</code> JavaScriptライブラリを利用した配置</h2>
      <p><code>embed-compressed.js</code>JavaScriptライブラリを利用して配置するには、
      次の行をLaszloアプリケーションを表示したいHTMLドキュメントの<code>&lt;head&gt;</code>セクションに貼り付けます。</p>
      
      <pre>&lt;script src="<xsl:value-of select="/canvas/request/@lps"/>/lps/includes/embed-compressed.js" type="text/javascript">&lt;/script></pre>

      次に次のLFCのコピーを<code>&lt;head&gt;</code>セクション内のLaszloアプリケーションを表示したい場所へ貼り付けます。
      LFCをロードするために<code>lz.embed.lfc()</code> は&lt;script/&gt; タグをHTMLに書き込みます。
      ここでは2個の引数を扱います。: ロードされるLFCのURL、リソースをロードすべきベースURL。
      この動作はページごとに一度だけ必要です。SOLO配置をするときはアプリケーションのこれらのURLは変わるかも知れませんので、ご注意ください。
      このアプリケーションでは次のようにします。
      <pre>&lt;script type="text/javascript"&gt;
    lz.embed.lfc('<xsl:value-of select="/canvas/request/@lps"/>/<xsl:value-of select="/canvas/@lfc"/>', '<xsl:value-of select="/canvas/request/@lps"/>');
&lt;/script></pre>
      
      <p>最後に次のコードを<code>&lt;body></code>内のLaszloアプリケーションを表示したい場所へ貼り付けます。</p>
      
      <pre>
&lt;script type="text/javascript"&gt;
    lz.embed.dhtml({url: '<xsl:value-of select="/canvas/request/@url"/>?lzt=object<xsl:value-of select="/canvas/request/@query_args"/>', bgcolor: '<xsl:value-of select="/canvas/@bgcolor"/>', width: '<xsl:value-of select="/canvas/@width"/>', height: '<xsl:value-of select="/canvas/@height"/>', id: '<xsl:value-of select="/canvas/@id"/>'});
    lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
        //アプリケーションのローディング完了時に呼ばれます。
    }
&lt;/script></pre>

      <xsl:call-template name="exampledeployment"/>

      <xsl:call-template name="disablehistory"><xsl:with-param name="methodname" select="'dhtml'" /></xsl:call-template>

      <xsl:call-template name="containerdiv"><xsl:with-param name="methodname" select="'dhtml'" /></xsl:call-template>

      <xsl:call-template name="solodeployment"><xsl:with-param name="jspname" select="'solo-dhtml-deploy'" /></xsl:call-template>
</xsl:otherwise></xsl:choose>

      <h2>ブラウザのJavaScriptからアプリケーションにアクセスする</h2>
      <p>各々のアプリケーションは<code>lz.embed.swf(...)</code> もしくは <code>lz.embed.dhtml(...)</code> に渡した id に応じて <code>lz.embed</code>の内部に自分の場所を確保しています。
それに加え、<code>lz.embed.applications</code> はそのページにid によって埋め込まれた全てのアプリケーションを追跡します。
例えば、<code>id: 'foo'</code>で埋め込まれたアプリケーションは<code>lz.embed.foo</code> もしくは <code>lz.embed.applications.foo</code>でアクセスできます。
このアプリケーションの場合は<code>lz.embed.<xsl:value-of select="/canvas/@id"/></code> もしくは <code>lz.embed.applications.<xsl:value-of select="/canvas/@id"/></code>でアクセスできます。
</p> 

      <p>アプリケーションにアクセスする前に'loaded'プロパティが<code>true</code>かどうかを確認すると良いでしょう。
      <pre>lz.embed.<xsl:value-of select="/canvas/@id"/>.loaded</pre>
      </p>

      <p>何時アプリケーションがロードされ、実行準備が出来たかどうかを知るには次のようにします。
      <pre>
lz.embed.<xsl:value-of select="/canvas/@id"/>.onload = function loaded() {
    ...
}
      </pre>
      </p>

      <p>アプリケーションの中でcanvas属性を見るには次のようにします。
      <pre>value = lz.embed.<xsl:value-of select="/canvas/@id"/>.getCanvasAttribute('attributename')</pre></p>
      
      <p>アプリケーションの中でcanvas属性を設定するには次のようにします。
      <pre>lz.embed.<xsl:value-of select="/canvas/@id"/>.setCanvasAttribute('attributename', value)</pre></p>

      <p>ページ内の全てのアプリケーションの中でcanvas属性を設定するには次のようにします。
      <pre>lz.embed.setCanvasAttribute('attributename', value[, history])</pre>
      オプションの<code>history</code> 引数が<code>true</code> のとき、ブラウザのヒストリーメカニズムが働き、setCanvasAttribute()を追跡します。その結果、ブラウザのフォワードやバックボタンが押されたとき、canvasの属性がリセットされます。</p>
     
      <p>アプリケーションの中でメソッドをコールするには次のようにします。
      <pre>value = lz.embed.<xsl:value-of select="/canvas/@id"/>.callMethod('globalreference.reference.anyMethod(...)')</pre> 
      メソッドの文字列表現を渡すことで引数を渡します。
      グローバルスコープ内に'<code>globalreference</code>'がみつけらられる限り、アプリケーション内のどのメソッドも使用できます。</p>

      <p>ページ内の全てのアプリケーションの中でメソッドをコールするには次のようにします。
      <pre>lz.embed.callMethod('globalreference.reference.anyMethod(...)')</pre> 
      </p>
      
      <h2>もっと情報が必要なら</h2>
      <ul>
        <li><a href="{/canvas/request/@lps}/docs/deployers/">Laszloアプリケーション配置用システム管理者用ガイド</a></li>
        <li><a href="{/canvas/request/@lps}/docs/developers/">ソフトウェア開発者ガイド</a></li>
        <li><a href="http://forum.openlaszlo.org/">開発者フォーラム</a></li>
      </ul>
  </body>
</html>
  </xsl:template>
</xsl:stylesheet>
